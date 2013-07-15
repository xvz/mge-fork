package controller;

import java.io.File;
import javax.swing.UIManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Driver {
	private static Logger logger = Logger.getLogger(Driver.class.getName());
	public static void main(String[] args) {
		PropertyConfigurator.configure(
				System.getProperty("user.dir") + 
				File.separator +
				"configuration" +
				File.separator +
		"log4j.properties");

		try{	
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		}catch(Throwable t){
			logger.error("", t);
		}

		java.awt.EventQueue.invokeLater(MainController.getInstance());			

	}
}
