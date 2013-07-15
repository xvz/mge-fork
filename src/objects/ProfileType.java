package objects;

import java.util.HashMap;

public enum ProfileType {
	AFTEN_AC3("AftenSettings"),
	AUD_X_MP3("AudXSettings"),
	FAAC("FaacSettings"),
	FFMPEG_AC3("AC3Settings"),
	FFMPEG_MP2("MP2Settings"),
	LAME_MP3("MP3Settings"),
	NERO_AAC("NeroAACSettings"),
	OGG_VORBIS("OggVorbisSettings"),
	SNOW("snowSettings"),
	WINAMP_AAC("WinAmpAACSettings"),
	X264("x264Settings"),
	XVID("xvidSettings"),
	NONE("None");

	private String name;
	private static HashMap<ProfileType, String> extensionMap = new HashMap<ProfileType, String>();
	private static HashMap<ProfileType, String> properNameMap = new HashMap<ProfileType, String>();

	private ProfileType(String name){
		this.name = name;
	}

	public ProfileType getProfileTypeFromString(String s){
		for(ProfileType p : ProfileType.values()){
			if(p.toString().equals(s)){
				return p;
			}
		}
		return null;
	}

	public String toString(){
		return name;
	}

	public static void hydrateMaps(){
		//Extension Map
		extensionMap.put(AFTEN_AC3, "ac3");
		extensionMap.put(AUD_X_MP3, "mp3");
		extensionMap.put(FAAC, "aac");
		extensionMap.put(FFMPEG_AC3, "ac3");
		extensionMap.put(FFMPEG_MP2, "mp2");
		extensionMap.put(LAME_MP3, "mp3");
		extensionMap.put(NERO_AAC, "m4a");
		extensionMap.put(OGG_VORBIS, "ogg");
		extensionMap.put(SNOW, "avi");
		extensionMap.put(WINAMP_AAC, "m4a");
		extensionMap.put(X264, "264");
		extensionMap.put(XVID, "avi");

		//Proper Name Map
		properNameMap.put(AFTEN_AC3, "Aften AC-3");
		properNameMap.put(AUD_X_MP3, "Aud-X MP3");
		properNameMap.put(FAAC, "FAAC");
		properNameMap.put(FFMPEG_AC3, "FFmpeg AC-3");
		properNameMap.put(FFMPEG_MP2, "FFmpeg MP2");
		properNameMap.put(LAME_MP3, "LAME MP3");
		properNameMap.put(NERO_AAC, "Nero AAC");
		properNameMap.put(OGG_VORBIS, "Vorbis");
		properNameMap.put(SNOW, "Snow");
		properNameMap.put(WINAMP_AAC, "Winamp AAC");
		properNameMap.put(X264, "x264");
		properNameMap.put(XVID, "xvid");
		properNameMap.put(NONE, "none");
	}

	public static String getExtension(ProfileType pt){
		return extensionMap.get(pt);
	}

	public static String getProperName(ProfileType p){
		return properNameMap.get(p);
	}
}
