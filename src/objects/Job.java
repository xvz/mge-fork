package objects;

import java.util.ArrayList;

public abstract class Job {
	protected ArrayList<Job> requiredJobs;
	protected ArrayList<Job> enabledJobs;
	public int jobNumber;
	
	public Job(int jobNumber){
		this.jobNumber = jobNumber;
		requiredJobs = new ArrayList<Job>();
		enabledJobs = new ArrayList<Job>();
	}
	
	
	public abstract void exportXML();
	
	public void addRequiredJob(Job job){
		requiredJobs.add(job);
	}
	
	public void addEnabledJob(Job job){
		enabledJobs.add(job);
	}
	
}
