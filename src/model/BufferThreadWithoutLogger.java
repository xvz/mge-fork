package model;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class BufferThreadWithoutLogger implements Runnable {
	private BufferedReader br;

	private static Logger logger = Logger.getLogger(BufferThreadWithoutLogger.class.getName());

	public BufferThreadWithoutLogger(BufferedReader br){
		this.br = br;
	}

	public void run() {
		try {
			String tempString;
			StringBuffer garbageString = new StringBuffer();
			while((tempString = br.readLine()) != null){
				garbageString.append(tempString);
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}

}
