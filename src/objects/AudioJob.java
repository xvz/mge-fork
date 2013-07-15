package objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import model.FileDiscriminator;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import controller.MainController;

public class AudioJob extends Job {
	private String input;
	private String rawOutput;
	private EncodingProfile profile;
	private int delay;
	private static Logger logger = Logger.getLogger(AudioJob.class.getName());
	private Document doc;
	
	public AudioJob(int jobNumber, String input, EncodingProfile profile, int delay){
		super(jobNumber);
		this.input = input;
		this.profile = profile;
		this.delay = delay;
		int index_of_last_period = FileDiscriminator.indexOfLastChar(input, '.');
		this.rawOutput = input.substring(0, index_of_last_period);
	}

	@Override
	public void exportXML() {
		if(profile.getType().equals(ProfileType.NONE)){
			logger.info("Audio encoding type is NONE. Skipping audio jobs");
			return;
		}
		
		try {
			doc = new Document();
			
			//Superroot Object
			Element taggedJob = new Element("TaggedJob");
			Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			Namespace xsd = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
			
			taggedJob.addNamespaceDeclaration(xsi);
			taggedJob.addNamespaceDeclaration(xsd);
			
			//Encoding speed element
			Element encodingSpeed = new Element("EncodingSpeed");
			
			//Attach encoding speed to super root
			taggedJob.addContent(encodingSpeed);
			
			//Job Element
			Element jobE = new Element("Job");
			Attribute type = new Attribute("type", "AudioJob", xsi);
			jobE.setAttribute(type);
			
			//Job children
			Element input = new Element("Input");
			input.setText(this.input);
			
			Element output = new Element("Output");
			String expectedExtension = ProfileType.getExtension(profile.getType());
			output.setText(rawOutput +"."+ expectedExtension);
			
			Element filesToDelete = new Element("FilesToDelete");
			
			Element cutFile = new Element("CutFile");
			
			//Profile Settings
			SAXBuilder builder = new SAXBuilder();
			Document profileSettings = builder.build(profile.getFile());
			
			Element profileRoot = profileSettings.getRootElement();
			Element settingsElement = (Element)profileRoot.getChild("Settings").clone();
			Attribute settingsType = new Attribute("type", profile.getType().toString(), xsi);
			settingsElement.setAttribute(settingsType);
			
			
			Element delay = new Element("Delay");
			delay.setText(this.delay + "");
			
			Element sizeBytes = new Element("SizeBytes");
			sizeBytes.setText("0");
			
			Element bitrateMode = new Element("BitrateMode");
			bitrateMode.setText("CBR");
			
			//Attach children
			jobE.addContent(input);
			jobE.addContent(output);
			jobE.addContent(filesToDelete);
			jobE.addContent(cutFile);
			jobE.addContent(settingsElement);
			jobE.addContent(delay);
			jobE.addContent(sizeBytes);
			jobE.addContent(bitrateMode);
			
			//Attach job to super root
			taggedJob.addContent(jobE);
			
			//Other common elements
			Element requiredJobNames = new Element("RequiredJobNames");
			Element enabledJobNames = new Element("EnabledJobNames");
			Element name = new Element("Name");
			name.setText("job"+super.jobNumber);
			Element status = new Element("Status");
			status.setText("WAITING");
			Element start = new Element("Start");
			start.setText("0001-01-01T00:00:00");
			Element end = new Element("End");
			end.setText("0001-01-01T00:00:00");
			
			//Attach other shit to super root
			taggedJob.addContent(requiredJobNames);
			taggedJob.addContent(enabledJobNames);
			taggedJob.addContent(name);
			taggedJob.addContent(status);
			taggedJob.addContent(start);
			taggedJob.addContent(end);
			
			//Add Tagged job to document
			doc.addContent(taggedJob);
			
			printOutXMLJob();
			
		} catch (JDOMException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
	private void printOutXMLJob(){
		try {
			FileWriter fileWriter;
			logger.info(
					"Writing to: " + 
					MainController.getInstance().getPathToMeGUIFolder()+ 
					File.separator +
					"jobs"+
					File.separator +
					"job" + 
					jobNumber + 
					".xml");
			
			fileWriter = new FileWriter(
					MainController.getInstance().getPathToMeGUIFolder() + 
					File.separator +
					"jobs" +
					File.separator +
					"job" +
					jobNumber +
					".xml");
			
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			PrintWriter printWriter = new PrintWriter(bufferedWriter);
			
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			outputter.output(doc, printWriter);
			
			printWriter.close();
			logger.info("Write successful");
		} catch (IOException e) {
			logger.error("", e);
		}
	}
	
}
