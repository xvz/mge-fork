package objects;

import java.io.File;
import java.util.ArrayList;

import model.FileDiscriminator;
/**
 * This is just an objective representation of a media file. Upon creating a MediaFile
 * you must ensure that all of the private fields are actually initialized, because
 * there's no way this object will be able to initialize them itself. The whole point
 * of having this object is to store data about a particular file, not analyze the file
 * itself. We'll be using a static class to populate data about this file. 
 * 
 * Remember, you have hydrate this object before using it. 
 * @author ajohnson
 *
 */
public class MediaFile implements Comparable<MediaFile>{
	private File file;
	private String name;
	private String extension;
	private MediaFileType type;
	private ArrayList<Track> tracks;
	private double fps;
	private int width;
	private int height;
	private NaturalOrderComparator NOC_INSTANCE = NaturalOrderComparator.getInstance();

	public MediaFile(File file){
		this.file = file;
		hydrateNames();
	}

	private void hydrateNames(){
		String fullName = file.getName();
		int index_of_period = FileDiscriminator.indexOfLastChar(fullName, '.');
		name = fullName.substring(0, index_of_period);
		extension = fullName.substring(index_of_period+1);
		tracks = new ArrayList<Track>();
	}

	public File getFile() {
		return file;
	}

	public String getExtension() {
		return extension;
	}

	public MediaFileType getType() {
		return type;
	}

	public void setType(MediaFileType type) {
		this.type = type;
	}

	public ArrayList<Track> getTracks() {
		return tracks;
	}

	public void addTrack(Track track){
		tracks.add(track);
	}
	
	public void setTracks(ArrayList<Track> tracks) {
		this.tracks = tracks;
	}

	@Override
	public String toString(){
		return name;
	}
	
	@Override
	public boolean equals(Object mf2){
		if(! (mf2 instanceof MediaFile)){
			return false;
		}
		
		if(this.file.equals(((MediaFile)mf2).getFile())){
			return true;
		}
		
		return false;
		
	}
	
	public String getPathWithoutExtension(){
		String fileName = file.getAbsolutePath();
		int index_of_period = FileDiscriminator.indexOfLastChar(fileName, '.');
		return fileName.substring(0, index_of_period);
	}
	
	public void setFPS(double fps){
		this.fps = fps;
	}
	
	public double getFPS(){
		return fps;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public String getProperName(){
		return name+extension;
	}

	@Override
	public int compareTo(MediaFile mf2) {
		String mf2ProperName = mf2.getProperName();
		return NOC_INSTANCE.compare(getProperName(), mf2ProperName);
	}	
}
