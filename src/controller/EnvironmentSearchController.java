package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import model.SeparateProcess;
import objects.EncodingProfile;
import objects.ProfileType;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * This class is in charge of detecting the runtime environment
 * so that it can provide system information to the program and
 * the user.
 * 
 * This class is also responsible for delivering information 
 * regarding the MeGUI JobLists XML file and the MeGUI XML profile
 * files.
 * @author Andrew
 *
 */
public class EnvironmentSearchController {
	private File meGuiFolder;
	private static final String DEFAULT_MEGUI_FOLDER_LOCATION_X86 = "C:" 
		+ File.separator 
		+ "Program Files" 
		+ File.separator 
		+ "megui";
	private static final String DEFAULT_MEGUI_FOLDER_LOCATION_X64 = "C:" 
		+ File.separator 
		+ "Program Files (x86)" 
		+ File.separator 
		+ "megui";

	private static final String ALTERNATE_MEGUI_FOLDER_LOCATION_X86 = "C:" 
		+ File.separator 
		+ "Program Files" 
		+ File.separator 
		+ "MeGUI";
	private static final String ALTERNATE_MEGUI_FOLDER_LOCATION_X64 = "C:" 
		+ File.separator 
		+ "Program Files (x86)" 
		+ File.separator 
		+ "MeGUI";

	private static final String MEGUI_PROCESS_NAME = "MeGUI.exe";

	private static Logger logger = Logger.getLogger(EnvironmentSearchController.class.getName());

	private ArrayList<EncodingProfile> audioProfiles;
	private ArrayList<EncodingProfile> videoProfiles;
	private HashMap<File, Document> fileToDocMap;
	

	public EnvironmentSearchController(){
		audioProfiles = new ArrayList<EncodingProfile>();
		videoProfiles = new ArrayList<EncodingProfile>();
		fileToDocMap = new HashMap<File, Document>();

		//Add default "none" encoding profiles for both
		EncodingProfile noneProfile = new EncodingProfile(null, ProfileType.NONE, "none", "");

		audioProfiles.add(noneProfile);
		videoProfiles.add(noneProfile);
	}


	/**
	 * This is the preliminary check of the system environment to ensure that the program
	 * can continue running--basically, we're verifying our assumptions about the environment
	 */
	public void preliminaryCheck(){
		logger.info("Starting preliminary system check");
		String runtimeSystem = System.getProperty("os.name");
		if(runtimeSystem.indexOf("Windows") == -1){
			logger.info("Found an unsuitable environment (" + runtimeSystem + "). Quitting");
			JOptionPane.showMessageDialog(
					null,
					"A Windows environment is required to run this program. Shutting down.",
					"Unsuitable Runtime Environment",
					JOptionPane.ERROR_MESSAGE);

			MainController.getInstance().exit();
		}


		//Detect MeGUI Instance
		if(isMeGUIRunning()){
			logger.info("Detected instance of MeGUI running. Prompting user");
			String message = "We found and instance of MeGUI Running. This can, and most likely will, " +
			"lead to a race-hazard. Do you still want to continue?";
			int result = JOptionPane.showOptionDialog(
					null, 
					message, 
					"MeGUI Instance Detected", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.WARNING_MESSAGE, 
					null, 
					null, 
					null);
			if(result == JOptionPane.NO_OPTION){
				logger.info("User chose to shut down MGE. Shutting down");
				MainController.getInstance().exit();
			}
		}

		//Check meGUIFolder
		meGuiFolder = new File(DEFAULT_MEGUI_FOLDER_LOCATION_X86);

		if(!meGuiFolder.exists()){
			//Couldn't find it in the usual x86 folder
			logger.info("Could not find MeGUI Folder at default x86 location");
			meGuiFolder = new File(ALTERNATE_MEGUI_FOLDER_LOCATION_X86);
			if(!meGuiFolder.exists()){
				//Couldn't find it in the alternate x86 folder
				logger.info("Could not find MeGUI Folder at alternate x86 location");
				meGuiFolder = new File(DEFAULT_MEGUI_FOLDER_LOCATION_X64);
				if(!meGuiFolder.exists()){
					//Couldn't find it in the usual x64 folder
					logger.info("Could not find MeGUI Folder at default x64 location");
					meGuiFolder = new File(ALTERNATE_MEGUI_FOLDER_LOCATION_X64);
					if(!meGuiFolder.exists()){
						//Couldn't find it in the alternate x64 folder
						logger.info("Could not find MeGUI Folder at alternate x64 location");
						String message = "We could not locate the MeGUI install directory. " +
						"Would you like to locate it? If you do not wish to locate it, this program will exit";
						int result = JOptionPane.showOptionDialog(
								null, 
								message, 
								"Unable to find MeGUI Folder", 
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.QUESTION_MESSAGE, 
								null, 
								null, 
								null);
						if(result == JOptionPane.YES_OPTION){
							if(!askUserForMeGUIFolder()){
								MainController.getInstance().exit();
							}
						}else{
							MainController.getInstance().exit();
						}
					}
				}
			}
		}

		logger.info("MeGUI folder set to: " + meGuiFolder);
	}

	/**
	 * This method is used to locate and populate the Profile fields
	 */
	public void populateMeGUIProfiles(){
		String rootProfileFolder =
			meGuiFolder.getAbsolutePath() + 
			File.separator + 
			"allprofiles" + 
			File.separator;

		ArrayList<File> listOfProfileFolders = new ArrayList<File>();

		listOfProfileFolders.add(new File(rootProfileFolder + "Aften AC-3"));
		listOfProfileFolders.add(new File(rootProfileFolder + "Aud-X MP3"));
		listOfProfileFolders.add(new File(rootProfileFolder + "FAAC"));
		listOfProfileFolders.add(new File(rootProfileFolder + "FFmpeg AC-3"));
		listOfProfileFolders.add(new File(rootProfileFolder + "FFmpeg MP2"));
		listOfProfileFolders.add(new File(rootProfileFolder + "LAME MP3"));
		listOfProfileFolders.add(new File(rootProfileFolder + "Nero AAC"));
		listOfProfileFolders.add(new File(rootProfileFolder + "Snow"));
		listOfProfileFolders.add(new File(rootProfileFolder + "Vorbis"));
		listOfProfileFolders.add(new File(rootProfileFolder + "Winamp AAC"));
		listOfProfileFolders.add(new File(rootProfileFolder + "x264"));
		listOfProfileFolders.add(new File(rootProfileFolder + "XviD"));

		logger.info("Captured all profile folders. Beginning verification");
		for(File f : listOfProfileFolders){
			processFolderOfProfiles(f);
		}
	}

	/**
	 * This method takes a folder, retrieves all the xml files within it,
	 * and calls the "verifyProfile" method to see if it should be added
	 * to the list of profiles.
	 * 
	 * Obviously, this will create a profile object as well
	 * @param file
	 */
	private void processFolderOfProfiles(File file){
		if(file.exists()){
			logger.info("Processing profile folder: "+ file);
			if(file.isDirectory()){
				File[] potientialProfiles = file.listFiles();
				for(File f : potientialProfiles){
					String fileName = f.getName();
					int length = fileName.length();

					String extension = fileName.substring(length-3);
					if(extension.equalsIgnoreCase("xml")){
						try{
							populateHashMap(f);
							
							ProfileType currentType = determineProfileType(f);
							String profileName = getProfileName(f);
							String nameOfProfileEntry = currentType.toString() + ": " + profileName;

							if(currentType.equals(ProfileType.X264) || currentType.equals(ProfileType.XVID) || currentType.equals(ProfileType.SNOW)){
								//Video Profile
								EncodingProfile currentProfile = new EncodingProfile(f, currentType, nameOfProfileEntry, profileName);
								videoProfiles.add(currentProfile);
								logger.info("Found video profile: " + currentProfile);
							}else{
								//Audio Profile
								EncodingProfile currentProfile = new EncodingProfile(f, currentType, nameOfProfileEntry, profileName);
								audioProfiles.add(currentProfile);
								logger.info("Found audio profile: "+ currentProfile);
							}
						}catch(JDOMException e){
							logger.error("Could not determine profile of " + f, e);
							logger.error("Skipping over " + f + " due to validation error");
						}catch(IOException e){
							logger.error("Some sort of IO error occured", e);
						}
					}
				}
			}
		}else{
			logger.error("Profile folder " + file + " could not be processed. It does not exist");
		}
	}

	private void populateHashMap(File file) throws JDOMException, IOException{
		FileInputStream inputStream = new FileInputStream(file);
		SAXBuilder builder = new SAXBuilder();
		fileToDocMap.put(file, builder.build(inputStream));
	}
	
	private ProfileType determineProfileType(File file) throws JDOMException, IOException{
//		SAXBuilder builder = new SAXBuilder();
//		Document doc = builder.build(file);
		Document doc = fileToDocMap.get(file);
		Element root = doc.getRootElement();
		String nameOfRoot = root.getName();

		if(nameOfRoot.equalsIgnoreCase("GenericProfileOfAftenSettings")){
			return ProfileType.AFTEN_AC3;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfAudXSettings")){
			return ProfileType.AUD_X_MP3;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfFaacSettings")){
			return ProfileType.FAAC;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfAC3Settings")){
			return ProfileType.FFMPEG_AC3;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfMP2Settings")){
			return ProfileType.FFMPEG_MP2;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfMP3Settings")){
			return ProfileType.LAME_MP3;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfNeroAACSettings")){
			return ProfileType.NERO_AAC;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfsnowSettings")){
			return ProfileType.SNOW;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfOggVorbisSettings")){
			return ProfileType.OGG_VORBIS;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfWinAmpAACSettings")){
			return ProfileType.WINAMP_AAC;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfx264Settings")){
			return ProfileType.X264;
		}else if(nameOfRoot.equalsIgnoreCase("GenericProfileOfxvidSettings")){
			return ProfileType.XVID;
		}else{
			return null;
		}

	}

	public File getMeGUILocation(){
		return meGuiFolder;
	}

	public boolean askUserForMeGUIFolder(){
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = jfc.showOpenDialog(MainController.getInstance().getMainWindow());

		if(result == JFileChooser.APPROVE_OPTION){
			File basefile = jfc.getSelectedFile();
			meGuiFolder = basefile; 
			return true;
		}else{
			return false;
		}

	}

	public ArrayList<EncodingProfile> getAudioProfiles() {
		return audioProfiles;
	}

	public ArrayList<EncodingProfile> getVideoProfiles() {
		return videoProfiles;
	}

	private String getProfileName(File file){
		Document doc = fileToDocMap.get(file);
		
		return doc.getRootElement().getChildText("Name");

	}

	public int getJobNumberFromJobList(){
		File file = new File(meGuiFolder.getAbsolutePath()+File.separator+"joblists.xml");
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			try{
				logger.info("Looking for start job number");
				if(file.exists()){
					SAXBuilder builder = new SAXBuilder();
					Document jobList = builder.build(inputStream);
					Element root = jobList.getRootElement();
					Element mainJobList = root.getChild("mainJobList");
					@SuppressWarnings("rawtypes")
					List jobs = mainJobList.getChildren();
					
					int largestJobNumber = 0;

					if(jobs.size() > 0){
						for(int i = 0; i < jobs.size(); i++){
							Element currentJob = (Element)jobs.get(i);

							String text = currentJob.getText().substring(3);

							int tempInt = Integer.parseInt(text);
							if(largestJobNumber < tempInt){
								largestJobNumber = tempInt;
							}
						}
						logger.info("Found the start job to be: "+largestJobNumber);
					}else{
						logger.info("The joblist is empty. Starting at 1");
					}		
					return largestJobNumber;
					
				}else{
					logger.info("Did not find largest job number. Starting at 1");
					return 0;
				}
			}catch(JDOMException e){
				logger.error("",e);
				return 1;
			}catch(IOException e){
				logger.error("", e);
				return 1;
			}finally{
				try {
					inputStream.close();
				} catch (IOException e1) {
					logger.error("Could not close stream", e1);
				}
			}
		} catch (FileNotFoundException e1) {
			logger.error("File doesn't exist", e1);
			return 1;
		}
		
	}
	/**
	 * Uses window's WMIC program to detect whether or not MeGUI is running or not
	 * @return
	 */
	private boolean isMeGUIRunning(){
		logger.info("Attempting to detect MeGUI Instance");
		String[] command = {"WMIC", "process", "get", "caption"};
		ArrayList<String> output;
		SeparateProcess meGUIDetection = new SeparateProcess(command);
		output = meGUIDetection.runWithResults();

		//Time to find out what's inside of the output
		for(String s : output){
			if(s.indexOf(MEGUI_PROCESS_NAME) != -1){
				logger.info("Detected MeGUI Instance");
				return true;
			}
		}
		logger.info("Did not detect MeGUI Instance");
		return false;
	}
}
