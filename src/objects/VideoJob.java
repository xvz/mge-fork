package objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
import controller.RenderController;

public class VideoJob extends Job {
	private String input;
	private ArrayList<String> filesToDelete;
	private AspectRatio DAR;
	private EncodingProfile profile;
	private Document doc;
	private String rawOutput;
	private int encodingMode;

	private static Logger logger = Logger.getLogger(VideoJob.class.getName());

	public VideoJob(int jobNumber, String input, EncodingProfile profile, AspectRatio DAR, int encodingMode){
		super(jobNumber);
		this.input = input;
		this.profile = profile;
		this.DAR = DAR;
		this.encodingMode = encodingMode;

		int index_of_last_period = FileDiscriminator.indexOfLastChar(input, '.');
		this.rawOutput = input.substring(0, index_of_last_period);
		this.doc = new Document();
	}

	public void addFilesToDelete(String string){
		filesToDelete.add(string);
	}

	@Override
	public void exportXML() {
		if(profile.getType().equals(ProfileType.NONE)){
			logger.info("Found profile NONE for video profile. Skipping");
			return;
		}
		try{
			//Superroot Object
			Element taggedJob = new Element("TaggedJob");
			Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			Namespace xsd = Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");

			taggedJob.addNamespaceDeclaration(xsi);
			taggedJob.addNamespaceDeclaration(xsd);

			//Encoding speed element
			Element encodingSpeed = new Element("EncodingSpeed");

			//Add encoding speed to super root
			taggedJob.addContent(encodingSpeed);

			//Job Element
			Element jobE = new Element("Job");
			Attribute type = new Attribute("type", "VideoJob", xsi);
			jobE.setAttribute(type);

			//Job children
			Element input = new Element("Input");
			input.setText(this.input);

			Element output = new Element("Output");

			//Check encoding mode
			if(encodingMode ==RenderController.ONE_PASS_ENCODING_MODE){
				//One pass encoding
				output.setText(rawOutput+"."+ProfileType.getExtension(profile.getType()));

				Element filesToDelete = new Element("FilesToDelete");

				Element zones = new Element("Zones");

				Element darE = new Element("DAR");
				Element ar = new Element("ar");
				ar.setText("" + ( ((double)DAR.getWidth()) / ((double)DAR.getHeight())) );
				darE.addContent(ar);

				//Settings Element
				SAXBuilder builder = new SAXBuilder();
				Document profileSettings = builder.build(profile.getFile());

				Element profileRoot = profileSettings.getRootElement();
				Element settingsElement = (Element)profileRoot.getChild("Settings").clone();
				Attribute settingsType = new Attribute("type", profile.getType().toString(), xsi);
				settingsElement.setAttribute(settingsType);
				
				Element logFile = settingsElement.getChild("Logfile");
				logFile.setText(rawOutput+".stats");
				
				//End of settings element
				
				//Attached elements to job element
				jobE.addContent(input);
				jobE.addContent(output);
				jobE.addContent(filesToDelete);
				jobE.addContent(zones);
				jobE.addContent(darE);
				jobE.addContent(settingsElement);
				
				//Attached job element to super root
				taggedJob.addContent(jobE);
				
				//Create common elements
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
				
				//Attach other common elements
				taggedJob.addContent(requiredJobNames);
				taggedJob.addContent(enabledJobNames);
				taggedJob.addContent(name);
				taggedJob.addContent(status);
				taggedJob.addContent(start);
				taggedJob.addContent(end);
				
				//Add tagged job to doc
				doc.addContent(taggedJob);

				//Print out job
				printOutXMLJob();
				
			}else if(encodingMode == RenderController.TWO_PASS_ENCODING_MODE_FIRST_PASS){
				//Two pass encoding; This is the first pass
				Element filesToDelete = new Element("FilesToDelete");

				Element zones = new Element("Zones");

				Element darE = new Element("DAR");
				Element ar = new Element("ar");
				ar.setText("" + ( ((double)DAR.getWidth()) / ((double)DAR.getHeight())) );
				darE.addContent(ar);

				//Settings Element
				SAXBuilder builder = new SAXBuilder();
				Document profileSettings = builder.build(profile.getFile());

				Element profileRoot = profileSettings.getRootElement();
				Element settingsElement = (Element)profileRoot.getChild("Settings").clone();
				Attribute settingsType = new Attribute("type", profile.getType().toString(), xsi);
				settingsElement.setAttribute(settingsType);
				
				//Encoding mode element
				Element encodingModeE = settingsElement.getChild("EncodingMode");
				encodingModeE.setText(""+encodingMode);
				
				//Log file Element
				Element logFile = settingsElement.getChild("Logfile");
				logFile.setText(rawOutput+".stats");
				
				//End of settings element
				
				//Attached elements to job element
				jobE.addContent(input);
				jobE.addContent(output);
				jobE.addContent(filesToDelete);
				jobE.addContent(zones);
				jobE.addContent(darE);
				jobE.addContent(settingsElement);
				
				//Attached job element to super root
				taggedJob.addContent(jobE);
				
				//Create common elements
				Element requiredJobNames = new Element("RequiredJobNames");
				Element enabledJobNames = new Element("EnabledJobNames");
				
				//Cycle through enabled jobs
				for(Job j : super.enabledJobs){
					Element string = new Element("String");
					string.setText("job"+j.jobNumber);
					enabledJobNames.addContent(string);
				}
				
				Element name = new Element("Name");
				name.setText("job"+super.jobNumber);
				
				Element status = new Element("Status");
				status.setText("WAITING");
				
				Element start = new Element("Start");
				start.setText("0001-01-01T00:00:00");
				
				Element end = new Element("End");
				end.setText("0001-01-01T00:00:00");
				
				//Attach other common elements
				taggedJob.addContent(requiredJobNames);
				taggedJob.addContent(enabledJobNames);
				taggedJob.addContent(name);
				taggedJob.addContent(status);
				taggedJob.addContent(start);
				taggedJob.addContent(end);
				
				//Add tagged job to doc
				doc.addContent(taggedJob);
				
				//Print out job
				printOutXMLJob();
				
			}else if(encodingMode == RenderController.TWO_PASS_ENCODING_MODE_SECOND_PASS){
				//Two pass encoding; this is the second pass
				output.setText(rawOutput+"."+ProfileType.getExtension(profile.getType()));
				
				Element filesToDelete = new Element("FilesToDelete");

				Element zones = new Element("Zones");

				Element darE = new Element("DAR");
				Element ar = new Element("ar");
				ar.setText("" + ( ((double)DAR.getWidth()) / ((double)DAR.getHeight())) );
				darE.addContent(ar);

				//Settings Element
				SAXBuilder builder = new SAXBuilder();
				Document profileSettings = builder.build(profile.getFile());

				Element profileRoot = profileSettings.getRootElement();
				Element settingsElement = (Element)profileRoot.getChild("Settings").clone();
				Attribute settingsType = new Attribute("type", profile.getType().toString(), xsi);
				settingsElement.setAttribute(settingsType);
				
				//Encoding mode element
				Element encodingModeE = settingsElement.getChild("EncodingMode");
				encodingModeE.setText(""+encodingMode);
				
				//Log file Element
				Element logFile = settingsElement.getChild("Logfile");
				logFile.setText(rawOutput+".stats");
				
				//End of settings element
				
				//Attached elements to job element
				jobE.addContent(input);
				jobE.addContent(output);
				jobE.addContent(filesToDelete);
				jobE.addContent(zones);
				jobE.addContent(darE);
				jobE.addContent(settingsElement);
				
				//Attached job element to super root
				taggedJob.addContent(jobE);
				
				//Create common elements
				Element requiredJobNames = new Element("RequiredJobNames");
				Element enabledJobNames = new Element("EnabledJobNames");
				
				//Cycle through required jobs
				for(Job j : super.requiredJobs){
					Element string = new Element("String");
					string.setText("job"+j.jobNumber);
					requiredJobNames.addContent(string);
				}
				
				Element name = new Element("Name");
				name.setText("job"+super.jobNumber);
				
				Element status = new Element("Status");
				status.setText("WAITING");
				
				Element start = new Element("Start");
				start.setText("0001-01-01T00:00:00");
				
				Element end = new Element("End");
				end.setText("0001-01-01T00:00:00");
				
				//Attach other common elements
				taggedJob.addContent(requiredJobNames);
				taggedJob.addContent(enabledJobNames);
				taggedJob.addContent(name);
				taggedJob.addContent(status);
				taggedJob.addContent(start);
				taggedJob.addContent(end);
				
				//Add tagged job to doc
				doc.addContent(taggedJob);
				
				//print output
				printOutXMLJob();
			}else{
				logger.error("Could not determine encoding mode");
			}
		}catch(JDOMException e){
			logger.error("", e);
		}catch(IOException e){
			logger.error("", e);
		}
		logger.info("Successfully exported xml");
	}

	public int getEncodingMode(){
		return encodingMode;
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
