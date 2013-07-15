package objects;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.table.AbstractTableModel;
/**
 * This is the table model that holds the data for the main file 
 * table
 * @author ajohnson
 *
 */
@SuppressWarnings("serial")
public class MediaFileTableModel extends AbstractTableModel{

	private final String[] columnNames = {
			"File Name",
	};

	private ArrayList<MediaFile> mediaFiles;
	private Object[] selectedFiles;

	public MediaFileTableModel(ArrayList<MediaFile> mediaFiles){
		this.mediaFiles = mediaFiles;
		Collections.sort(this.mediaFiles);
		selectedFiles = new Object[mediaFiles.size()];
		
		int i = 0;
		for(MediaFile m : mediaFiles){
			selectedFiles[i] = m;
			i++;
		}
	}
	
	@Override
	public String getColumnName(int col){
		return columnNames[col];
	}
	
	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return mediaFiles.size();
	}

	public Object getValueAt(int row, int col) {
		return selectedFiles[row];
	}
	
	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	/**
	 * This method should be called whenever we need to update the
	 * file table. Instead of creating a completely new table model
	 * everytime we want to refresh the table, we'll just simply
	 * udpate all the variables. 
	 * 
	 * Not sure if this will actually refresh the view of the table
	 * refresh the view, 
	 * @param mediaFiles
	 */
	public void setMediaFileList(ArrayList<MediaFile> mediaFiles){
		this.mediaFiles = mediaFiles;
		Collections.sort(this.mediaFiles);
		//Resetting selected files
		selectedFiles = new Object[mediaFiles.size()];
		
		int i = 0;
		for(MediaFile m : mediaFiles){
			selectedFiles[i] = m;
			i++;
		}
	
	}
	
}
