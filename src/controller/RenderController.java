package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import model.MediaFileInfoFactory;
import objects.AspectRatio;
import objects.AudioJob;
import objects.EncodingProfile;
import objects.Job;
import objects.MediaContainer;
import objects.MediaFile;
import objects.MuxJob;
import objects.ProfileType;
import objects.VideoJob;
import objects.VideoJobType;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import viewer.ProgressWindow;

/**
 * This class is meant to handle all Job XML file creation, as well as
 * moving it to the MeGUI folder. 
 * @author ajohnson
 *
 */
public class RenderController extends Controller{
	private static Logger logger = Logger.getLogger(RenderController.class.getName());

	//Static encoding modes
	public static final int ONE_PASS_ENCODING_MODE = 9;
	public static final int TWO_PASS_ENCODING_MODE_FIRST_PASS = 2;
	public static final int TWO_PASS_ENCODING_MODE_SECOND_PASS = 3;

	private ArrayList<MediaFile> mediaFiles;
	private EncodingProfile audioProfile;
	private EncodingProfile videoProfile;
	private MediaContainer container;
	private String avisynthScript;
	private VideoJobType videoJobType;

	private ArrayList<Job> audioJobs;
	private ArrayList<Job> videoJobs;
	private ArrayList<Job> muxJobs;

	private ProgressWindow progressWindow;

	public RenderController(
			ArrayList<MediaFile> mediaFiles,
			EncodingProfile audioProfile,
			EncodingProfile videoProfile,
			MediaContainer container,
			String avisynthScript){

		this.mediaFiles = mediaFiles;
		this.audioProfile = audioProfile;
		this.videoProfile = videoProfile;
		this.container = container;
		this.avisynthScript = avisynthScript;
		this.audioJobs = new ArrayList<Job>();
		this.videoJobs = new ArrayList<Job>();
		this.muxJobs = new ArrayList<Job>();

		progressWindow = new ProgressWindow();

	}

	@Override
	public void run() {
		//Set flag in maincontroller
		MainController.getInstance().setCurrentlyEncoding();

		//Show Progress Window
		progressWindow.setVisible(true);

		//Hydrate Media Files
		hydrateMediaFiles();
		progressWindow.setHydrateMediaFilesStep();

		//Run Avisynth Rendering
		AvisynthController scriptController = new AvisynthController(mediaFiles, avisynthScript);
		logger.info("Running Avisynth Controller");
		scriptController.run(); //This is imperative. We now have to update the media files based upon the user's decided resolution
		progressWindow.setGenerateAviSynthScriptsStep();

		boolean audioProfileGo = !audioProfile.getType().equals(ProfileType.NONE);
		boolean videoProfileGo = !videoProfile.getType().equals(ProfileType.NONE);

		int globalJobNumber = MainController.getInstance().getStartJobNumber()+1; 

		if(audioProfileGo){
			if(MainController.getInstance().getLinkAudioJobWithAviSynthScript()){
				for(MediaFile m : mediaFiles){
					AudioJob job = new AudioJob(
							globalJobNumber, 
							m.getPathWithoutExtension() + ".avs", 
							audioProfile, 
							MainController.getInstance().getDelay()); 
					audioJobs.add(job);
					globalJobNumber++;
				}
			}else{
				for(MediaFile m : mediaFiles){
					AudioJob job = new AudioJob(
							globalJobNumber, 
							m.getFile().toString(), 
							audioProfile, 
							MainController.getInstance().getDelay()); 
					audioJobs.add(job);
					globalJobNumber++;
				}
			}
		}

		if(videoProfileGo){
			videoJobType = getVideoJobType();
			if(videoJobType.equals(VideoJobType.ONE_PASS)){
				//Onepass video job
				for(MediaFile m : mediaFiles){
					VideoJob job = new VideoJob(
							globalJobNumber,
							m.getPathWithoutExtension() + ".avs", 
							videoProfile, 
							new AspectRatio(m.getWidth(), m.getHeight()),
							ONE_PASS_ENCODING_MODE);
					videoJobs.add(job);
					globalJobNumber++;
				}
			}else{
				//Multipass Job
				for(MediaFile m : mediaFiles){
					VideoJob job1 = new VideoJob(
							globalJobNumber, 
							m.getPathWithoutExtension()  + ".avs", 
							videoProfile, 
							new AspectRatio(m.getWidth(), m.getHeight()),
							TWO_PASS_ENCODING_MODE_FIRST_PASS);
					VideoJob job2 = new VideoJob(
							globalJobNumber, 
							m.getPathWithoutExtension() + ".avs", 
							videoProfile, 
							new AspectRatio(m.getWidth(), m.getHeight()),
							TWO_PASS_ENCODING_MODE_SECOND_PASS);

					job1.addEnabledJob(job2);
					job2.addRequiredJob(job1);

					videoJobs.add(job1);
					videoJobs.add(job2);
					globalJobNumber += 2;
				}
			}

			for(MediaFile m : mediaFiles){
				String videoExtension = ProfileType.getExtension(videoProfile.getType());
				String audioExtension = ProfileType.getExtension(audioProfile.getType());

				//Check to make sure that the audio job actually exists
				if(audioProfileGo){
					MuxJob job = new MuxJob(
							globalJobNumber,
							m.getPathWithoutExtension()+"."+videoExtension, 
							m.getPathWithoutExtension()+"."+audioExtension,
							container,
							m.getFPS());

					muxJobs.add(job);
					globalJobNumber++;
				}else{
					MuxJob job = new MuxJob(
							globalJobNumber,
							m.getPathWithoutExtension()+"."+videoExtension, 
							null,
							container,
							m.getFPS());

					muxJobs.add(job);
					globalJobNumber++;
				}
			}
		}
		progressWindow.setCreateMeGUIJobsStep();

		outputAllJobXMLFiles();
		progressWindow.setOutputJobXMLFilesStep();

		editJobListXML();
		progressWindow.setEditJobXMLFileStep();

		//reset maincontroller flag
		MainController.getInstance().finishedEncoding();

		//Close Progress window
		progressWindow.dispose();
	}

	private void hydrateMediaFiles(){
		ArrayList<MediaFile> trashMediaFiles = new ArrayList<MediaFile>();
		for(MediaFile m : mediaFiles){
			if(!MediaFileInfoFactory.hydrateMediaFile(m)){
				logger.info("Media file " + m + " queued for removal. It was found to have mal-formed media data");
				trashMediaFiles.add(m);
			}
		}
		logger.info("Removing media files we could not hydrate");
		mediaFiles.removeAll(trashMediaFiles);
	}

	private void outputAllJobXMLFiles(){
		//Audio Jobs first
		for(Job a : audioJobs){
			a.exportXML();
		}

		//Video Jobs next
		for(Job v : videoJobs){
			v.exportXML();
		}

		//Mux Jobs last
		for(Job m : muxJobs){
			m.exportXML();
		}
	}

	private void editJobListXML(){
		//Just need to know how many jobs there are and at which number to start at
		File jobListsFile = new File(MainController.getInstance().getPathToMeGUIFolder()+File.separator+"joblists.xml");
		if(jobListsFile.exists()){
			//Modify existing joblists.xml file
			try {
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(jobListsFile);

				Element root = doc.getRootElement();
				Element mainJobList = root.getChild("mainJobList");

				//Ordering of jobs done here
				for(Job j : audioJobs){
					Element string = new Element("string");
					string.setText("job"+j.jobNumber);
					mainJobList.addContent(string);
				}

				for(Job j : videoJobs){
					Element string = new Element("string");
					string.setText("job"+j.jobNumber);
					mainJobList.addContent(string);
				}

				for(Job j : muxJobs){
					Element string = new Element("string");
					string.setText("job"+j.jobNumber);
					mainJobList.addContent(string);
				}

				printOutJobListFile(doc);

			} catch (JDOMException e) {
				logger.error("JDOM threw an error. The xml file is probably malformed", e);
				logger.info("Attempting to build new joblist.xml file");
				jobListsFile.delete();
				editJobListXML();
			} catch (IOException e) {
				logger.error("Fatal error. Unrecoverable IOException", e);
			}
		}else{
			//Build new joblists.xml file
			Document doc = new Document();
			Element jobListSerializer = new Element("JobListSerializer");

			Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			Namespace xsd = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");

			jobListSerializer.addNamespaceDeclaration(xsi);
			jobListSerializer.addNamespaceDeclaration(xsd);

			Element mainJobList = new Element("mainJobList");

			Element workers = new Element("workersAndTheirJobLists");

			//Ordering of jobs done here
			for(Job j : audioJobs){
				Element string = new Element("string");
				string.setText("job"+j.jobNumber);
				mainJobList.addContent(string);
			}

			for(Job j : videoJobs){
				Element string = new Element("string");
				string.setText("job"+j.jobNumber);
				mainJobList.addContent(string);
			}

			for(Job j : muxJobs){
				Element string = new Element("string");
				string.setText("job"+j.jobNumber);
				mainJobList.addContent(string);
			}

			jobListSerializer.addContent(mainJobList);
			jobListSerializer.addContent(workers);

			doc.addContent(jobListSerializer);

			printOutJobListFile(doc);

		}
	}

	private VideoJobType getVideoJobType(){
		try {
			logger.info("Getting video type");
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new File(videoProfile.getFile().getAbsolutePath()));

			Element root = doc.getRootElement();
			Element settings = root.getChild("Settings");
			Element encodingMode = settings.getChild("EncodingMode");

			int mode = Integer.parseInt(encodingMode.getText());

			if(mode == 4){
				logger.info("Found multipass video encode");
				return VideoJobType.MULTI_PASS;
			}else{
				logger.info("Found single pass video encode");
				return VideoJobType.ONE_PASS;
			}

		} catch (JDOMException e) {
			logger.info("", e);
			return null;
		} catch (IOException e) {
			logger.info("", e);
			return null;
		}
	}

	private void printOutJobListFile(Document doc){
		try {
			FileWriter fileWriter;
			logger.info("Writing joblist.xml file");

			fileWriter = new FileWriter(
					MainController.getInstance().getPathToMeGUIFolder()+
					File.separator +
			"joblists.xml");

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			PrintWriter printWriter = new PrintWriter(bufferedWriter);

			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(doc, printWriter);

			printWriter.close();
			logger.info("Write successful");
		} catch (IOException e) {
			logger.error("", e);
		}
	}
}

