package objects;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.MainController;
/**
 * This class acts as the central listener for all actions made
 * within the MainWindowview
 * @author ajohnson
 *
 */
public class MainControllerListener implements ActionListener{
	
	private ActionType action;
	
	public MainControllerListener(ActionType action){
		this.action = action;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		MainController INSTANCE = MainController.getInstance();
		
		if(action.equals(ActionType.ABOUT)){
			INSTANCE.showAboutWindow();
		}else if(action.equals(ActionType.CLEAR)){
			INSTANCE.clearFileTable();
		}else if(action.equals(ActionType.DELETE)){
			INSTANCE.deleteFromFileTable();
		}else if(action.equals(ActionType.EXIT)){
			INSTANCE.exit();
		}else if(action.equals(ActionType.GO)){
			INSTANCE.renderFiles();
		}else if(action.equals(ActionType.LOG)){
			//TODO: Implement this
		}
	}

}
