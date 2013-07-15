package objects;

import java.io.File;

public class EncodingProfile {
	private String name;
	private String properName;
	private ProfileType type;
	private File file;
	
	
	public EncodingProfile(File file, ProfileType type, String name, String properName){
		this.file = file;
		this.type = type;
		this.name = name;
		this.properName = properName;
	}

	public ProfileType getType() {
		return type;
	}

	public void setType(ProfileType type) {
		this.type = type;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public String toString(){
		return name;
	}
	/**
	 * This is just to get around the limitations of the combo-box. Can't seem
	 * to find a good way to redo the combo box model
	 * @param string
	 * @return
	 */
	public boolean matchStringToEncodingProfile(String string){
		if(name.equals(string)){
			return true;
		}
		return false;
	}
	
	public String getProperName(){
		return this.properName;
	}
	
}
