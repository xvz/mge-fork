package objects;

import java.util.HashMap;

public enum MediaContainer {
	AVI("avi"),
	M2TS("m2ts"),
	MKV("mkv"),
	MP4("mp4");
	
	private String name;
	private static HashMap<MediaContainer, String> mediaContainerToProgramMap = new HashMap<MediaContainer, String>();
	
	private MediaContainer(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
	
	public static MediaContainer getMediaContainerFromString(String name){
		for(MediaContainer m : MediaContainer.values()){
			if(m.toString().equals(name)){
				return m;
			}
		}
		return null;
	}
	
	public static void hydrateMaps(){
		mediaContainerToProgramMap.put(AVI, "AVIMUXGUI");
		mediaContainerToProgramMap.put(M2TS, "TSMUXER");
		mediaContainerToProgramMap.put(MKV, "MKVMERGE");
		mediaContainerToProgramMap.put(MP4, "MP4BOX");
	}
	
	public static String getMuxTool(MediaContainer m){
		return mediaContainerToProgramMap.get(m);
	}
}
