package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import objects.MediaFile;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
/**
 * This class is a collection of static methods that give us the ability
 * to analyze media files and hydrate MediaFile objects
 * @author ajohnson
 *
 */
public class MediaFileInfoFactory {
	private static final String mediaInfoExecutable_x86 = 
		System.getProperty("user.dir")  + 
		File.separator + 
		"thirdparty" + 
		File.separator + 
		"mediainfo_x86" + 
		File.separator + 
		"MediaInfo.exe";
	
//	private static final String mediaInfoExecutable_x64 = 
//		System.getProperty("user.dir")  + 
//		File.separator + 
//		"thirdparty" + 
//		File.separator + 
//		"mediainfo_x64" + 
//		File.separator + 
//		"MediaInfo.exe";

	private static Logger logger = Logger.getLogger(MediaFileInfoFactory.class.getName());
	
	/**
	 * This method will attempt to analyze a media file and hydrate the 
	 * associated fields of the mediafile object that was passed in
	 * @param file
	 */
	public static boolean hydrateMediaFile(MediaFile file){
		UUID randomID = UUID.randomUUID();
		String fileName = randomID.toString() + "-mediaInfo.xml";
		String mediaFileName = file.getFile().getAbsolutePath();
		String mediaFileNameWithoutExtension = file.getPathWithoutExtension();
		
		/* Runtime.exec(String[]) requires command arguments be broken at spaces.
		 * For example,
		 *
		 *   "mediainfo.exe" --logfile="C:\terrible folder name\my file.xml"
		 *
		 * must be broken into array
		 *
		 *  ["\"mediainfo.exe\"", "--logfile=\"C:\\terrible", "folder", "name\\my", "file.xml\""]
		 */
		List<String> command = new ArrayList<String>();
		String commandArgs = "--output=xml --logfile=\"" + mediaFileNameWithoutExtension + fileName + "\" \"" + mediaFileName + "\"";
		
		command.add("\"" + mediaInfoExecutable_x86 + "\"");
		command.addAll(Arrays.asList(commandArgs.split(" ")));
		
		logger.info("Creating MediaInfo XML file: " + mediaFileNameWithoutExtension+fileName);

		SeparateProcess mediaInfoProcess = new SeparateProcess(command.toArray(new String[command.size()]));
		mediaInfoProcess.setWithoutLogger(true);
		mediaInfoProcess.setWaitFor(true);
		Thread t1 = new Thread(mediaInfoProcess);
		t1.start();
		try {
			t1.join();
		} catch (InterruptedException e1) {
			logger.error("Could not join thread. Continuing.", e1);
		}

		File dataFile = new File(mediaFileNameWithoutExtension + fileName);

		boolean result = analyzeMediaFileUsingREGEX(file, dataFile);
		if(!result){
			logger.info("Could not read in data using REGEX. Going to JDOM");
				try {
					analyzeMediaFile(file, dataFile);
					return true;
				} catch (JDOMException e) {
					logger.error("", e);
					return false;
				} catch (IOException e) {
					logger.error("", e);
					return false;
				}
		}
		dataFile.delete();
		return result;
	}

	private static boolean analyzeMediaFileUsingREGEX(MediaFile file, File dataFile){
		logger.info("Attempting to read in media info using regular expressions instead of a DOM reader");
		ArrayList<String> documentText = new ArrayList<String>();

		FileReader fileReader;
		try {
			fileReader = new FileReader(dataFile);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			Scanner fileScanner = new Scanner(bufferedReader);

			while(fileScanner.hasNextLine()){
				documentText.add(fileScanner.nextLine());
			}

			fileScanner.close();
			//Set up regular expressions
			Pattern fpsREG = Pattern.compile("<Frame_rate>.+</Frame_rate>");
			Pattern widthREG = Pattern.compile("<Width>.+</Width>");
			Pattern heightREG = Pattern.compile("<Height>.+</Height>");
			Pattern audioDelay = Pattern.compile("<Video_Delay_String>.+</Video_Delay_String>");

			Matcher fpsM = null;
			Matcher widthM = null;
			Matcher heightM = null;
			Matcher audioDelayM = null;

			boolean flagFPS = false;
			boolean flagWidth = false;
			boolean flagHeight = false;
			boolean flagAudioDelay = false;

			for(String s : documentText){
				if(!flagFPS){
					fpsM = fpsREG.matcher(s);
				}
				if(!flagWidth){
					widthM = widthREG.matcher(s);
				}
				if(!flagHeight){
					heightM = heightREG.matcher(s);
				}
				if(!flagAudioDelay){
					audioDelayM = audioDelay.matcher(s);
				}

				if(fpsM.find()){
					flagFPS = true;
					fpsM.reset();
				}

				if(widthM.find()){
					flagWidth = true;
					widthM.reset();
				}

				if(heightM.find()){
					flagHeight = true;
					heightM.reset();
				}
				
				if(audioDelayM.find()){
					flagAudioDelay = true;
					audioDelayM.reset();
				}

				if(flagFPS && flagWidth && flagHeight && flagAudioDelay){
					break;
				}
			}

			//Get data
			String fpsE = "";
			String widthE = "";
			String heightE = "";
			String delayE = "";

			if(fpsM.find()){
				fpsE = fpsM.group();
			}else{
				logger.info("Could not find FPS of " + file);
				return false;
			}

			if(widthM.find()){
				widthE = widthM.group();
			}else{
				logger.info("Could not find width of " + file);
				return false;
			}

			if(heightM.find()){
				heightE = heightM.group();
			}else{
				logger.info("Could not find height of " + file);
				return false;
			}
			
			if(audioDelayM.find()){
				delayE = audioDelayM.group();
			}else{
				logger.info("Did not find delay information. Assuming 0 delay");
			}
			
			fpsE = fpsE.substring(12, fpsE.length());
			widthE = widthE.substring(7, widthE.length());
			heightE = heightE.substring(8, heightE.length());
			
			/* Delay information parsed iff it exists */
			if(delayE.length() > 0){
				delayE.substring(20, delayE.length());
			}

			//Parse out width and height pixel space
			String[] widthSplit = widthE.split(" ");
			String[] heightSplit = heightE.split(" ");

			String widthNumbersOnly = "";
			String heightNumbersOnly = "";

			for(int i = 0; i < widthSplit.length-1; i++){
				widthNumbersOnly += widthSplit[i];
			}

			for(int i = 0; i < heightSplit.length-1; i++){
				heightNumbersOnly += heightSplit[i];
			}


			double fpsAsDouble = Double.parseDouble(fpsE.split(" ")[0]);
			logger.info("FPS: "+ fpsAsDouble);

			file.setWidth(Integer.parseInt(widthNumbersOnly));
			logger.info("Width: " + widthNumbersOnly);
			file.setHeight(Integer.parseInt(heightNumbersOnly));
			logger.info("Height: " + heightNumbersOnly);
			file.setFPS(fpsAsDouble);

			return true;
		} catch (FileNotFoundException e) {
			logger.error("Couldn't find the file", e);
			return false;
		} catch(Throwable t){
			logger.error("", t);
			return false;
		}

	}


	@SuppressWarnings("unchecked")
	private static void analyzeMediaFile(MediaFile file, File dataFile) throws JDOMException, IOException{
		FileInputStream inputStream = new FileInputStream(dataFile);
		
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(inputStream);

		//Closing file stream
		inputStream.close();

		Element mediaInfoRoot = doc.getRootElement();
		Element fileInfoRoot = mediaInfoRoot.getChild("File");

		//Figure out media file type
		List<Element> allChildren = fileInfoRoot.getChildren();

		for(Element e : allChildren){
			String attribute = e.getAttributeValue("type");

			if(attribute.equalsIgnoreCase("Video")){
				logger.info("Detecting video information");
				logger.info("File Statistics for " + file);

				String width = e.getChildText("Width");
				String height = e.getChildText("Height");
				String fps = e.getChildText("Frame_rate");

				//Parse out width and height pixel space
				String[] widthSplit = width.split(" ");
				String[] heightSplit = height.split(" ");

				String widthNumbersOnly = "";
				String heightNumbersOnly = "";

				for(int i = 0; i < widthSplit.length-1; i++){
					widthNumbersOnly += widthSplit[i];
				}

				for(int i = 0; i < heightSplit.length-1; i++){
					heightNumbersOnly += heightSplit[i];
				}


				double fpsAsDouble = Double.parseDouble(fps.split(" ")[0]);
				logger.info("FPS: "+ fpsAsDouble);

				file.setWidth(Integer.parseInt(widthNumbersOnly));
				logger.info("Width: " + widthNumbersOnly);
				file.setHeight(Integer.parseInt(heightNumbersOnly));
				logger.info("Height: " + heightNumbersOnly);
				file.setFPS(fpsAsDouble);
			}
		}
	}
}

