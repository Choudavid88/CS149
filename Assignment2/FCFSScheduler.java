import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
//Testing for gggggggithub

public class FCFSScheduler extends Scheduler {
	private double aveTurnAroundTime;
	private double aveWaitingTime;
	private double aveResponseTime;
	public FCFSScheduler()
	{
		processQueue = new ArrayList<Process>(); 
		processList = new ArrayList<Process>(); 
	}
	@Override
	public void run() {
		
		
		///////////////////////////
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
		///////////////////////////////
		printProcess();
	}
	//print the time chart
	public void printProcess() // by jimmy again
	{
		/*System.out.println("Arrival Time and ExpectedRunTime for each process");
		for(Process p : processQueue)
		{
			System.out.println(p.getProcessId() + " " + p.getArrivalTime() + " " + p.getExpectedRunTime());
		}*/ // for checking the order is correct base on the arrival time
		for(Process p : processQueue)
		{
			float temp = (float) p.getExpectedRunTime();
			while(temp > 0)
			{
				System.out.print(p.getProcessId());
				temp--;
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
