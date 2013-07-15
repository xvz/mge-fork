package objects;
/**
 * This is a representation of a data track inside of a media file. This 
 * object serves no real purpose, unless it's in the context of a media
 * file.
 * @author ajohnson
 *
 */
public class Track {
	private TrackType type;
	private int trackNumber;
	private String name;
	
	public Track(TrackType type, int trackNumbher){
		this.type = type;
		this.trackNumber = trackNumbher;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String toString(){
		if(name == null){
			return type.toString() + ": "+trackNumber;
		}
		return name;
	}
	
	public TrackType getTrackType(){
		return type;
	}
	
	public int getTrackNumber(){
		return trackNumber;
	}
}
