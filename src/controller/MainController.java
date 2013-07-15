package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import model.FileDiscriminator;
import model.SeparateProcess;

import objects.EncodingProfile;
import objects.MediaContainer;
import objects.MediaFile;
import objects.ProfileType;
import viewer.MainWindowView;

public class MainController extends Controller{
	public static final String VERSION = "1.3.2b (Modified)";
	private static final MediaContainer[] MEDIA_CONTAINERS = MediaContainer.values();
	private ArrayList<MediaFile> mediaFiles;
	private MainWindowView mainWindowView;
	private static Logger logger = Logger.getLogger(MainController.class.getName());
	private EnvironmentSearchController esc;
	private ArrayList<EncodingProfile> audioProfiles;
	private ArrayList<EncodingProfile> videoProfiles;
	private boolean currentlyEncoding = false;
	
	private static final String AVISYNTH_MESSAGE = "#Put your avisynth script here\n" +
	"#Refer to the release notes for instructions on how to use symbolic variables\n" +
	"#Example:\n" +
	"#DirectShowSource(\"<MEDIAFILE>\", fps=<FPS>, convertfps=true, audio=false)\n" + 
	"#Spline64Resize(<WIDTH>, <HEIGHT>) # Spline64 (Sharp)";

	private MainController() {
		mediaFiles = new ArrayList<MediaFile>();
		//Have to prep the profile types
		ProfileType.hydrateMaps();
		MediaContainer.hydrateMaps();
	}

	private static class MainControllerHolder {
		private static final MainController INSTANCE = new MainController();
	}

	public static MainController getInstance() {
		return MainControllerHolder.INSTANCE;
	}

	public void addFileAction(File baseFile){
		FileDiscriminator fd = new FileDiscriminator(baseFile);
		for(MediaFile mf : fd.getMediaFileList()){
			if(!mediaFiles.contains(mf)){
				logger.info("Added: " + mf + " to list of files");
				mediaFiles.add(mf);
			}else{
				logger.info(mf + " is already in the media list");
			}
		}

		mainWindowView.updateFileList(mediaFiles);
	}

	public void addListOfFilesAction(List<File> listOfFiles){
		for(File f : listOfFiles){
			addFileAction(f); //This will recursively add folders that are dragged and dropped
		}
	}

	public void exit(){
		logger.info("Shutdown requested. Shutting down");
		if(mainWindowView != null){
			mainWindowView.dispose();
			logger.info("Window disposed of");
		}
		System.exit(0);
	}

	/**
	 * This is called when the "GO" button is clicked
	 */
	public void renderFiles(){
		if(currentlyEncoding){
			JOptionPane.showMessageDialog(mainWindowView, 
					"MGE is currently executing a task at the moment and cannot perform two tasks simultaneously. " +
					"Wait until the first task finishes", 
					"Currently Encoding", 
					JOptionPane.INFORMATION_MESSAGE);
		}else{
			//get profiles
			EncodingProfile audioProfileSelected = getEncodingProfileFromString(mainWindowView.getSelectedAudioProfile());
			EncodingProfile videoProfileSelected = getEncodingProfileFromString(mainWindowView.getSelectedVideoProfile());
			MediaContainer mediaContainerSelected = MediaContainer.getMediaContainerFromString(mainWindowView.getSelectedMediaContainerProfile());

			//Get avisynth script
			String avisynthScript = mainWindowView.getAvisynthScript();

			if(avisynthScript.length() == 0){
				JOptionPane.showMessageDialog(
						mainWindowView, 
						"Your avisynth script is empty",
						"AviSynth Script Error",
						JOptionPane.ERROR_MESSAGE);

			}else if(mediaFiles.size() == 0){
				JOptionPane.showMessageDialog(
						mainWindowView, 
						"You have not selected any files",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}else{
				//Fire off rendering controller
				RenderController rc = new RenderController(
						mediaFiles, 
						audioProfileSelected, 
						videoProfileSelected, 
						mediaContainerSelected, 
						avisynthScript);

				Thread t1 = new Thread(rc);
				t1.start();

			}
		}

	}
	/**
	 * Call this method whenever you need to dereference, not destroy or alter, the 
	 * data that our private fields point to
	 */
	public void dereferenceVariables(){
		mediaFiles = new ArrayList<MediaFile>();
		mainWindowView.clearFileList();
		logger.info("Finished dereferencing variables");
	}

	public void clearFileTable(){
		mediaFiles.clear();
		mainWindowView.clearFileList();
		logger.info("Cleared media list");
	}

	public void deleteFromFileTable(){
		ArrayList<MediaFile> selectedFiles = mainWindowView.getSelectedFilesFromFileTable();
		mediaFiles.removeAll(selectedFiles);
		mainWindowView.updateFileList(mediaFiles);
		for(MediaFile m : selectedFiles){
			logger.info("Removed " + m + " from media list");
		}
	}

	@Override
	public void run() {
		logger.info("Starting MainController thread");
		esc = new EnvironmentSearchController();
		esc.preliminaryCheck();

		esc.populateMeGUIProfiles();

		audioProfiles = esc.getAudioProfiles();
		videoProfiles = esc.getVideoProfiles();

		String[] audioProfilesAsStrings = new String[audioProfiles.size()];
		String[] videoProfilesAsStrings = new String[videoProfiles.size()];
		String[] mediaContainers = new String[MEDIA_CONTAINERS.length];

		int i = 0;
		for(EncodingProfile p : audioProfiles){
			audioProfilesAsStrings[i] = ProfileType.getProperName(p.getType()) + ": " + p.getProperName();
			i++;
		}

		i = 0;
		for(EncodingProfile p : videoProfiles){
			videoProfilesAsStrings[i] = ProfileType.getProperName(p.getType())+ ": " + p.getProperName();
			i++;
		}

		i = 0;
		for(MediaContainer mc : MEDIA_CONTAINERS){
			mediaContainers[i] = mc.toString();
			i++;
		}

		mainWindowView = new MainWindowView(videoProfilesAsStrings, audioProfilesAsStrings, mediaContainers);
		mainWindowView.setAviSynthMessage(AVISYNTH_MESSAGE);
		mainWindowView.setVisible(true);
	}

	public JFrame getMainWindow(){
		return mainWindowView;
	}

	public void showAboutWindow(){
		logger.info("Launching about window");
		JOptionPane.showMessageDialog(
				mainWindowView, 
				"MeGUI Batch Job Expansion v" + VERSION + "\nAuthor: helios2k6\nModified by: xvz", 
				"About", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void viewLogs() {
		JFileChooser fc = new JFileChooser();
		File logsDir = new File(System.getProperty("user.dir"));
		fc.setCurrentDirectory(logsDir);
		
		// set file filter
		FileFilter ff = new FileFilter() {
			public boolean accept(File f) {
				return f.getName().matches("log\\.log|log\\.log\\.\\d{4}-\\d{2}-\\d{2}");
			}
			
			public String getDescription() {
				return "Log Files";
			}
		};
		
		fc.setFileFilter(ff);
		
		// set default text editor
		logger.info("Getting default text editor");
		String opencmd = "notepad.exe \"%1\"";
		
		// find user's default text editor, if one exists
		String[] regcmd = {"reg", "query", "\"HKCR\\txtfile\\shell\\open\\command\"", "/v", "\"\""};
		ArrayList<String> output;
		SeparateProcess registryReader = new SeparateProcess(regcmd);
		output = registryReader.runWithResults();

		for(String s : output) {
            // output has format: \n<Version information>\n\n<key>    <registry type>    <value>\n
            if( s.contains("    ") && s.contains(".exe") ) {
            	String[] parsed = s.split("    ");
            	opencmd = parsed[parsed.length-1];
            }
		}
		
		int textEditorEnd = opencmd.indexOf(".exe") + ".exe".length();
		if ( opencmd.contains(".exe\"") ) { textEditorEnd++; }
		logger.info("Default text editor: " + opencmd.substring(0, textEditorEnd));
		
		logger.info("Launching log selection window");
		if (fc.showOpenDialog(getMainWindow()) == JFileChooser.APPROVE_OPTION) {
			// no quotes around %1 is okay, since log filenames don't have spaces
			opencmd = opencmd.replaceAll("%1", fc.getSelectedFile().getName());
			
			ArrayList<String> cmd = new ArrayList<String>();
			// first element is path to text editor
			cmd.add(opencmd.substring(0, textEditorEnd));
			// rest of array is arguments, split at spaces
			cmd.addAll(Arrays.asList(opencmd.substring(textEditorEnd+1).split(" ")));
			
			SeparateProcess logeditor = new SeparateProcess(cmd.toArray(new String[cmd.size()]));
			logeditor.run();
			logger.info("Opened log: " + fc.getSelectedFile().getName());
		}
	}
	
	private EncodingProfile getEncodingProfileFromString(String s){
		for(EncodingProfile p : audioProfiles){
			String encodingProfileString = ProfileType.getProperName(p.getType()) + ": "+ p.getProperName();
			if(s.equalsIgnoreCase(encodingProfileString)){
				return p;
			}
		}

		for(EncodingProfile p : videoProfiles){
			String encodingProfileString = ProfileType.getProperName(p.getType()) + ": "+ p.getProperName();
			if(s.equalsIgnoreCase(encodingProfileString)){
				return p;
			}
		}

		return null;
	}

	public String getPathToMeGUIFolder(){
		String path = esc.getMeGUILocation().getAbsolutePath();
		return path;
	}

	public int getStartJobNumber(){
		return esc.getJobNumberFromJobList();
	}

	public int getDelay(){
		return mainWindowView.getDelay();
	}

	public void setCurrentlyEncoding(){
		this.currentlyEncoding = true;
	}

	public void finishedEncoding(){
		this.currentlyEncoding = false;
	}
	
	public boolean getLinkAudioJobWithAviSynthScript(){
		return mainWindowView.getLinkAudioJobWithAviSynthScript();
	}
}
