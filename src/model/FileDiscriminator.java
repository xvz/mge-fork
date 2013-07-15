package model;

import java.io.File;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import objects.MediaFile;
import objects.MediaFileType;

/**
 * This class is meant to discriminate between files and folders. It also
 * filters out files with known mediafile extensions
 * @author ajohnson
 *
 */
public class FileDiscriminator {

	private ArrayList<File> rawFileList;
	private ArrayList<MediaFile> mediaFileList;
	private File baseFile;

	private static Logger logger = Logger.getLogger(FileDiscriminator.class.getName());

	public FileDiscriminator(File baseFile){
		this.baseFile = baseFile;
		rawFileList = new ArrayList<File>();
		mediaFileList = new ArrayList<MediaFile>();
		discriminateBetweenRawFiles();
		processFiles();
	}

	private void discriminateBetweenRawFiles(){
		logger.info("Discriminating between file/folder");
		if(baseFile.isDirectory()){
			logger.info(baseFile + " is a directory. Running through raw file list");
			for(File tempFile : baseFile.listFiles()){
				rawFileList.add(tempFile);
			}
		}else{
			rawFileList.add(baseFile);
		}
	}
	
	/**
	 * Finds the last occurrence of a given char e in string s. Returns -1 if there 
	 * isn't one
	 * @param s
	 * @param e
	 * @return
	 */
	public static int indexOfLastChar(String s, char e){
		int last_index = -1;
		int current_index = 0;
		while(current_index > -1 && current_index < s.length()){
			current_index = s.indexOf(e, current_index);
			if(current_index > -1){
				last_index = current_index;
				current_index++;
			}else{
				current_index = -1;
			}		
		}
	
		
		return last_index;
	}
	
	private void processFiles(){
		logger.info("Determining files through file extension");
		String[] patternsForVideoFiles = {
				"avi",
				"m2ts",
				"mkv",
				"mp4",
				"ogm",
				"rmf",
				"rm",
				"rmvb",
				"ts",
				"wmv",
		};
		
		String[] patternsForAudioFiles = {
			"mp3",
			"aac",
			"m4p",
			"wav"
		};

		for(File tempFile : rawFileList){
			String fileName = tempFile.getName();
			
			int occurance_of_last_period = indexOfLastChar(fileName, '.');
			
			String extension = fileName.substring(occurance_of_last_period+1);
			//Find Video Files
			for(String pattern : patternsForVideoFiles){
				if(extension.equalsIgnoreCase(pattern)){

					MediaFile tempMediaFile = new MediaFile(tempFile);

					tempMediaFile.setType(MediaFileType.getMediaFileType(pattern));

					mediaFileList.add(tempMediaFile);

					logger.info("Adding Video File: " + tempFile);
				}
			}
			
			//Find Audio Files
			for(String pattern : patternsForAudioFiles){
				if(extension.equalsIgnoreCase(pattern)){

					MediaFile tempMediaFile = new MediaFile(tempFile);

					tempMediaFile.setType(MediaFileType.getMediaFileType(pattern));
					
					mediaFileList.add(tempMediaFile);

					logger.info("Adding Audio File: " + tempFile);
				}
			}
		}

	}

	public ArrayList<File> getRawFileList(){
		return rawFileList;
	}

	public ArrayList<MediaFile> getMediaFileList(){
		return mediaFileList;
	}

	public void removeMediaFile(MediaFile mediaFile){
		mediaFileList.remove(mediaFile);
	}

	public void addMediaFile(MediaFile mediaFile){
		mediaFileList.add(mediaFile);
	}
}
