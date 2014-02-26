import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FCFSScheduler extends Scheduler {
	private double aveTurnAroundTime;
	private double aveWaitingTime;
	private double aveResponseTime;
	private double totalTime;
	public FCFSScheduler()
	{
		processQueue = new ArrayList<Process>(); 
		processList = new ArrayList<Process>(); 
		totalTime = 0;
	}
	@Override
	public void run() {
		
		for(Process p: processList)
		{
			processQueue.add(p);
		}
		Collections.sort(processQueue, new Comparator<Process>(){

			@Override
			public int compare(Process o1, Process o2) {
				if(o1.getArrivalTime() < o2.getArrivalTime())
					return -1;
				else if(o1.getArrivalTime() > o2.getArrivalTime())
					return 1;
				else
					return 0;
			}
			
		}); //by jimmy NOT KEN
		printProcess();
	}
	//print the time chart
	public void printProcess() // by jimmy again
	{
		//the correction has been made here
		//solution: create an instance variable called totalTime for keeping track the total it takes to run the processes
		Process p = null;
		int counter = 0;
		while(processQueue.size() > 0 && totalTime < 99)
		{
			if(processQueue.get(0).getArrivalTime() <= totalTime) //it should be less than equal because the arrival time of process should parallel with total time slice. 
			{
				p = processQueue.remove(0);
				float temp = (float) p.getExpectedRunTime();
				while(temp > 0)
				{
					System.out.print(p.getProcessId());
					temp--;
					totalTime++;
				}
			} // if no process has arrived at this time, we just increment the totalTime and print a space
			else
			{
				System.out.print(" "); //print a space indicate this quanta does nothing
				totalTime++;
			}
		}
		
	}
	
	public void createProcess(int numberOfProcesses)
	{
		int i;
		for(i=0; i < numberOfProcesses; i++)
		{
			processList.add(new Process((char)(i + 97)));
		}
	}
	
}
