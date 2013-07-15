package objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import controller.MainController;

public class MuxJob extends Job {
	private String inputVideo;
	private String inputAudio;
	private MediaContainer mediaContainer;
	private double fps;
	private Document doc;
	private String rawOutput;
	private static String MUX_PROFILE_LOCATION = System.getProperty("user.dir")+File.separator+"muxprofile.xml";
	private static final File MUX_PROFILE = new File(MUX_PROFILE_LOCATION);

	private static Logger logger = Logger.getLogger(MuxJob.class.getName());

	public MuxJob(int jobNumber, String inputVideo, String inputAudio, MediaContainer mediaContainer, double fps){
		super(jobNumber);
		this.inputVideo = inputVideo;
		this.inputAudio = inputAudio;
		this.mediaContainer = mediaContainer;
		this.fps = fps;
		this.doc = new Document();

		int length = inputVideo.length();
		this.rawOutput = inputVideo.substring(0, length-4);
	}

	@Override
	public void exportXML() {
		try {
			//muxjob reference xml
			SAXBuilder builder = new SAXBuilder();
			Document reference = builder.build(MUX_PROFILE);

			this.doc = (Document)reference.clone();

			//Superroot Object
			Element taggedJob = doc.getRootElement();		

			Element jobE = taggedJob.getChild("Job");

			Element input = jobE.getChild("Input");
			input.setText(this.inputVideo);

			Element output = jobE.getChild("Output");
			output.setText(rawOutput+"-muxed."+mediaContainer.toString());

			Element containerTypeString = jobE.getChild("ContainerTypeString");
			containerTypeString.setText(mediaContainer.toString().toUpperCase());

			//Settings children
			Element settings = jobE.getChild("Settings");

			Element muxedOutput = settings.getChild("MuxedOutput");
			muxedOutput.setText(rawOutput+"-muxed."+mediaContainer.toString());

			Element videoInput = settings.getChild("VideoInput");
			videoInput.setText(this.inputVideo);

			//Make sure that the audio stream actually exists
			if(inputAudio != null){
				//Audio streams children
				Element audioStreams = settings.getChild("AudioStreams");

				//MuxStream children
				Element muxStream = audioStreams.getChild("MuxStream");

				//Path child
				Element path = muxStream.getChild("path");
				path.setText(inputAudio);
			}
			//Setting out to Settings child
			Element fpsE = settings.getChild("Framerate");
			fpsE.setText(fps+"");

			//Stepping out again to the Job child
			Element muxType = jobE.getChild("MuxType");
			muxType.setText(MediaContainer.getMuxTool(mediaContainer));

			//Other common elements
			Element name = taggedJob.getChild("Name");
			name.setText("job"+super.jobNumber);

			printOutXMLJob();

		} catch (JDOMException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}

	}

	private void printOutXMLJob(){
		try {
			FileWriter fileWriter;
			logger.info(
					"Writing to: " + 
					MainController.getInstance().getPathToMeGUIFolder()+ 
					File.separator +
					"jobs"+
					File.separator +
					"job" + 
					jobNumber + 
			".xml");

			fileWriter = new FileWriter(
					MainController.getInstance().getPathToMeGUIFolder() + 
					File.separator +
					"jobs" +
					File.separator +
					"job" +
					jobNumber +
			".xml");

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
