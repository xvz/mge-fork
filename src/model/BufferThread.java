package model;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.log4j.Logger;

public class BufferThread implements Runnable{
	private BufferedReader br;

	private static Logger logger = Logger.getLogger(BufferThread.class.getName());

	public BufferThread(BufferedReader br){
		this.br = br;
	}

	public void run() {
		try {
			String tempString;
			while((tempString = br.readLine()) != null){
				logger.info(tempString);
			}
		} catch (IOException e) {
			logger.error("", e);
		}
	}
}
