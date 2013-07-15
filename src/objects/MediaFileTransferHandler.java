package objects;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.TransferHandler;

import org.apache.log4j.Logger;

import controller.MainController;
/**
 * This is the transfer handler for the drag and drop capibilities in 
 * MGE.
 * @author ajohnson
 *
 */
@SuppressWarnings("serial")
public class MediaFileTransferHandler extends TransferHandler {
	private static Logger logger = Logger.getLogger(MediaFileTransferHandler.class.getName());

	public boolean canImport(TransferSupport support){
		if(!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
			return false;
		}
		
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(TransferSupport support){
		
		Transferable trans = support.getTransferable();
		
		try{
			List<File> l = (List<File>)trans.getTransferData(DataFlavor.javaFileListFlavor);
			//OK, now we have to add the media files to the main window
			MainController.getInstance().addListOfFilesAction(l);
		}catch(UnsupportedFlavorException e){
			logger.info("Could not drag and drop files", e);
		}catch(IOException e){
			logger.info("Could not drag and drop files due to IO error", e);
		}
		
		return true;
	}
}
