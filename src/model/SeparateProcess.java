package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

public class SeparateProcess implements Runnable{
	private List<String[]> listOfCommands = null;
	private String[] environment = null;
	private File dir = null;
	private boolean closeOutputStream = true;
	private Process mainProcess;
	private boolean withoutLogger = false;
	private boolean waitFor = false;

	private static Logger logger = Logger.getLogger(SeparateProcess.class.getName());

	public SeparateProcess(List<String[]> command){
		this.listOfCommands = command;	
	}

	public SeparateProcess(List<String[]> command, String[] environment){
		this.listOfCommands = command;
		this.environment = environment;

	}

	public SeparateProcess(List<String[]> command, String[] environment, File dir){
		this.listOfCommands = command;
		this.environment = environment;
		this.dir = dir;
	}

	public SeparateProcess(String[] singleCommand){
		ArrayList<String[]> command = new ArrayList<String[]>();
		command.add(singleCommand);
		listOfCommands = command;
	}

	public void setWithoutLogger(boolean choice){
		withoutLogger = choice;
	}
	public void run() {
		try {
			if(withoutLogger){
				int counter = 1;
				for(String[] immediateCommand : listOfCommands){

					logger.info("Executing: " + Arrays.toString(immediateCommand));
					logger.info("Environment: " + environment);
					logger.info("Inside directory: " + dir);
					mainProcess = Runtime.getRuntime().exec(immediateCommand, environment, dir);

					BufferedReader stdInput = new BufferedReader(new 
							InputStreamReader(mainProcess.getInputStream()));

					BufferedReader stdError = new BufferedReader(new 
							InputStreamReader(mainProcess.getErrorStream()));

					if(closeOutputStream){
						logger.info("Closing Output Stream");
						mainProcess.getOutputStream().close();
					}


					BufferThreadWithoutLogger ET = new BufferThreadWithoutLogger(stdInput);
					Thread threadOne = new Thread(ET);
					threadOne.start();
					BufferThreadWithoutLogger RT = new BufferThreadWithoutLogger(stdError);
					Thread threadTwo = new Thread(RT);
					threadTwo.start();

					if(waitFor){
						mainProcess.waitFor();
					}

					logger.info("Finished Process " + counter);
				}
			}else{
				int counter = 1;
				for(String[] immediateCommand : listOfCommands){

					logger.info("Executing: " + Arrays.toString(immediateCommand));
					logger.info("Environment: " + environment);
					logger.info("Inside directory: " + dir);
					mainProcess = Runtime.getRuntime().exec(immediateCommand, environment, dir);

					BufferedReader stdInput = new BufferedReader(new 
							InputStreamReader(mainProcess.getInputStream()));

					BufferedReader stdError = new BufferedReader(new 
							InputStreamReader(mainProcess.getErrorStream()));

					if(closeOutputStream){
						logger.info("Closing Output Stream");
						mainProcess.getOutputStream().close();
					}


					BufferThread processThreadOne = new BufferThread(stdInput);
					Thread threadOne = new Thread(processThreadOne);
					threadOne.start();
					BufferThread processThreadTwo = new BufferThread(stdError);
					Thread threadTwo = new Thread(processThreadTwo);
					threadTwo.start();

					if(waitFor){
						mainProcess.waitFor();
					}

					logger.info("Finished Process " + counter);
				}
			}
		}catch (IOException e) {
			logger.error( "", e);
		}catch(Throwable t){
			logger.error( "", t);
		}

	}

	public void runInLinearMode(){
		try {
			int counter = 1;
			for(String[] immediateCommand : listOfCommands){

				logger.info("Executing: " + Arrays.toString(immediateCommand));
				logger.info("Environment: " + environment);
				logger.info("Inside directory: " + dir);
				mainProcess = Runtime.getRuntime().exec(immediateCommand, environment, dir);

				BufferedReader stdInput = new BufferedReader(new 
						InputStreamReader(mainProcess.getInputStream()));

				BufferedReader stdError = new BufferedReader(new 
						InputStreamReader(mainProcess.getErrorStream()));

				if(closeOutputStream){
					logger.info("Closing Output Stream");
					mainProcess.getOutputStream().close();
				}

				String tempInput;
				String tempErr;

				while((tempInput = stdInput.readLine()) != null){
					logger.info(tempInput);
				}

				while((tempErr = stdError.readLine()) != null){
					logger.info(tempErr);
				}

				logger.info("Finished Process " + counter);
			}

		}catch (IOException e) {
			logger.error( "", e);
		}catch(Throwable t){
			logger.error( "", t);
		}
	}

	public ArrayList<String> runWithResults(){
		ArrayList<String> results = new ArrayList<String>();

		try {

			for(String[] immediateCommand : listOfCommands){

				logger.info("Executing: " + Arrays.toString(immediateCommand));
				logger.info("Environment: " + environment);
				logger.info("Inside directory: " + dir);
				mainProcess = Runtime.getRuntime().exec(immediateCommand, environment, dir);

				BufferedReader stdInput = new BufferedReader(new 
						InputStreamReader(mainProcess.getInputStream()));

				BufferedReader stdError = new BufferedReader(new 
						InputStreamReader(mainProcess.getErrorStream()));

				if(closeOutputStream){
					logger.info("Closing Output Stream");
					mainProcess.getOutputStream().close();
				}

				String tempInput;
				String tempErr;

				while((tempInput = stdInput.readLine()) != null){
					logger.info(tempInput);
					results.add(tempInput);
				}

				while((tempErr = stdError.readLine()) != null){
					logger.info(tempErr);
					results.add(tempErr);
				}

				logger.info("Finished Process");
			}

		}catch (IOException e) {
			logger.error( "", e);
		}catch(Throwable t){
			logger.error( "", t);
		}

		return results;
	}

	public void setCloseOutputStream(boolean closeOutputStream){
		this.closeOutputStream = closeOutputStream; 
	}

	public boolean isWaitFor() {
		return waitFor;
	}

	public void setWaitFor(boolean waitFor) {
		this.waitFor = waitFor;
	}
}
