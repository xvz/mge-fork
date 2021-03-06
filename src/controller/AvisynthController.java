package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import objects.MediaFile;

import org.apache.log4j.Logger;

/**
 * What this controller does is handle all of the AviSynth file 
 * generation for all of the media files. It's kicked off after
 * the user executes the batch job.
 * 
 * By convention, the AviSynth Scripts will be named directly
 * @author ajohnson
 *
 */
public class AvisynthController extends Controller{
	private String avisynthScriptText;

	private static final String MEDIA_FILE_TOKEN = "<MEDIAFILE>";
	private static final String FPS_TOKEN = "<FPS>";
	private static final String HEIGHT_TOKEN = "<HEIGHT>";
	private static final String WIDTH_TOKEN = "<WIDTH>";	
	private static final String AUTO_GENERATE_MESSAGE = "#This script was generated by MGE " + MainController.VERSION;
	private static Logger logger = Logger.getLogger(AvisynthController.class.getName());

	private List<MediaFile> mediaFiles;
	
	//Resize REGEX Patterns
	private Pattern resizeAllLowerCase = Pattern.compile("resize\\s*\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)\\s*");
	private Pattern resizeCaptialFirstWord = Pattern.compile("Resize\\s*\\(\\s*\\d+\\s*,\\s*\\d+\\s*\\)\\s*");

	public AvisynthController(List<MediaFile> mediaFiles, String avisynthScriptText){
		this.mediaFiles = mediaFiles;
		this.avisynthScriptText = avisynthScriptText;
	}

	private void checkForResize(){
		Matcher resizeAllLowerCaseMatcher = resizeAllLowerCase.matcher(avisynthScriptText);
		Matcher resizeCapitalFirstWordMatcher = resizeCaptialFirstWord.matcher(avisynthScriptText);
		
		if(resizeAllLowerCaseMatcher.find()){
			logger.info("Detected custom DAR. Attempting to parse and hydrate");
			String matchedPortion = resizeAllLowerCaseMatcher.group();
			Pattern digitsCommaDigits = Pattern.compile("\\d+\\s*,\\s*\\d+");
			Matcher digitsCommaDigitsM = digitsCommaDigits.matcher(matchedPortion);
			
			if(digitsCommaDigitsM.find()){
				String render2 = digitsCommaDigitsM.group();
				String[] brokenUp = render2.split(",");
				try{
					int detectedWidth = Integer.parseInt(brokenUp[0].trim());
					int detectedHeight = Integer.parseInt(brokenUp[1].trim());
					
					double DAR = ((double)detectedWidth)/((double)detectedHeight);
					
					logger.info("Detected custom DAR as: " +
							detectedWidth +
							"x" +
							detectedHeight +
							" (" +
							DAR +
							")");
					
					for(MediaFile m : mediaFiles){
						m.setWidth(detectedWidth);
						m.setHeight(detectedHeight);
					}
				}catch(NumberFormatException e){
					logger.error("Could not parse integer from string", e);
				}
			}else{
				logger.info("Could not detect custom DAR");
			}			
		}else if(resizeCapitalFirstWordMatcher.find()){
			logger.info("Detected custom DAR. Attempting to parse and hydrate");
			String matchedPortion = resizeCapitalFirstWordMatcher.group();
			Pattern digitsCommaDigits = Pattern.compile("\\d+\\s*,\\s*\\d+");
			Matcher digitsCommaDigitsM = digitsCommaDigits.matcher(matchedPortion);
			
			if(digitsCommaDigitsM.find()){
				String render2 = digitsCommaDigitsM.group();
				String[] brokenUp = render2.split(",");
				try{
					int detectedWidth = Integer.parseInt(brokenUp[0].trim());
					int detectedHeight = Integer.parseInt(brokenUp[1].trim());
					
					double DAR = ((double)detectedWidth)/((double)detectedHeight);
					
					logger.info("Detected custom DAR as: " +
							detectedWidth +
							"x" +
							detectedHeight +
							" (" +
							DAR +
							")");
					
					for(MediaFile m : mediaFiles){
						m.setWidth(detectedWidth);
						m.setHeight(detectedHeight);
					}
				}catch(NumberFormatException e){
					logger.error("Could not parse integer from string", e);
				}
			}else{
				logger.info("Could not detect custom DAR");
			}
		}else{
			logger.info("Did not detect custom DAR. Using hydrated file DAR instead");
		}
	}
	
	@Override
	public void run() {
		logger.info("Generating AviSynth Scripts");
		
		logger.info("Checking for custom resize value");
		checkForResize();

		//Loop through all mediaFiles
		for(MediaFile m : mediaFiles){
			logger.info("Processing: " + m);
			String nameOfAviSynthFile = m.getPathWithoutExtension()+".avs";

			/* Opening file we will write to */
			try {
				logger.info("Attempting to write: " + nameOfAviSynthFile);
				FileWriter fileWriter = new FileWriter(nameOfAviSynthFile);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				PrintWriter printWriter = new PrintWriter(bufferedWriter);

				//Replace text within avisynth script
				String replacedText = avisynthScriptText.replace(MEDIA_FILE_TOKEN, m.getFile().getAbsolutePath());
				replacedText = replacedText.replace(FPS_TOKEN, m.getFPS()+"");
				
				replacedText = replacedText.replace(HEIGHT_TOKEN, m.getHeight()+"");
				replacedText = replacedText.replace(WIDTH_TOKEN, m.getWidth()+"");


				//Print autogeneration message
				printWriter.println(AUTO_GENERATE_MESSAGE);

				//Print out user input
				for(String s : replacedText.split("\n")){
					printWriter.println(s);
				}
				printWriter.close();
				logger.info("Write was successful");
				
			} catch (IOException e) {
				logger.error("Write was a failure. IO Exception", e);
			}
		}
	}
	
}