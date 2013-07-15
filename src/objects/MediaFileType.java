package objects;
/**
 * This enum specifies a media file type. This list of file types
 * is inheriently lacking, as there are several other media containers
 * that exist in the world. We'll use the "OTHER" enum to catch anything
 * that is not one of the other media files listed
 * @author ajohnson
 *
 */
public enum MediaFileType {
	//VIDEO FILES
	AVI("AVI"),
	M2TS("M2TS"),
	MKV("MKV"),
	MP4("MP4"),
	OGM("OGM"),
	RMF("RMF"),
	RM("RM"),
	RMVB("RMVB"),
	TS("TS"),
	WMV("WMV"),
	//AUDIO FILES
	AAC("AAC"),
	M4P("M4P"),
	MP3("MP3"),
	WAV("WAV"),
	//All Others
	OTHER("Unknown");

	private String extension;

	private MediaFileType(String extension){
		this.extension = extension;
	}

	public static MediaFileType getMediaFileType(String string){
		if(string.equalsIgnoreCase(AVI.toString())){
			return AVI;
		}else if(string.equalsIgnoreCase(M2TS.toString())){
			return M2TS;
		}else if(string.equalsIgnoreCase(MKV.toString()) || string.equalsIgnoreCase("Matroska")){
			return MKV;
		}else if(string.equalsIgnoreCase(MP4.toString()) || string.equalsIgnoreCase("MPEG-4")){
			return MP4;
		}else if(string.equalsIgnoreCase(OGM.toString())){
			return OGM;
		}else if(string.equalsIgnoreCase(RMF.toString())){
			return RMF;
		}else if(string.equalsIgnoreCase(RM.toString())){
			return RM;
		}else if(string.equalsIgnoreCase(RMVB.toString())){
			return RMVB;
		}else if(string.equalsIgnoreCase(TS.toString())){
			return TS;
		}else if(string.equalsIgnoreCase(WMV.toString()) || string.equalsIgnoreCase("Windows Media")){
			return WMV;
		}else if(string.equalsIgnoreCase(AAC.toString())){
			return AAC;
		}else if(string.equalsIgnoreCase(MP3.toString())){
			return MP3;
		}else if(string.equalsIgnoreCase(WAV.toString())){
			return WAV;
		}else{
			return OTHER;
		}
	}

	public String toString(){
		return extension;
	}
}
