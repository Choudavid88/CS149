import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RRScheduler extends Scheduler {
	private double aveTurnAroundTime;
	private double aveWaitingTime;
	private double aveResponseTime;
	private double totalTime;
	private ArrayList<Process> waitingQueue;
	private int quantum = 1;
	public RRScheduler()
	{
		processQueue = new ArrayList<Process>(); 
		processList = new ArrayList<Process>();
		waitingQueue = new ArrayList<Process>();
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
		for(Process e: processQueue)
			System.out.println(e.getProcessId() +" "+ e.getArrivalTime()+" "+ e.getExpectedRunTime());

		printProcess();
	}
	//print the time chart
	public void printProcess() // by jimmy again
	{
		//the correction has been made here
		//solution: create an instance variable called totalTime for keeping track the total it takes to run the processes
		Process p = null;
		int counter = 0;
		while(processQueue.size() > 0 && totalTime < 100)
		{
			if(processQueue.get(0).getArrivalTime() <= totalTime)
			{
				p = processQueue.remove(0);
				waitingQueue.add(p);
				p = waitingQueue.remove(0);				
				float temp = (float) p.getExpectedRunTime();
				System.out.print(p.getProcessId());
				temp--;
				totalTime++;
				if(temp > 0){
					p.updateExpectedRunTime(temp);
					waitingQueue.add(p);
				}
			} // if no process has arrived at this time, we just increment the totalTime and print a space
			else if (waitingQueue.size() > 0){
				p = waitingQueue.remove(0);				
				float temp = (float) p.getExpectedRunTime();
				System.out.print(p.getProcessId());
				temp--;
				totalTime++;
				if(temp > 0){
					p.updateExpectedRunTime(temp);
					waitingQueue.add(p);
				}
			}
			else
			{
				System.out.print(" "); //print a space indicate this quanta does nothing
				totalTime++;
				counter++;
			}
		}
		
		System.out.println(counter);
		for(Process e: waitingQueue)
			System.out.println(e.getProcessId() +" "+ e.getExpectedRunTime() + " " + e.getArrivalTime());

	}
	
	public void sort(ArrayList<Process> p ){
		Collections.sort(p, new Comparator<Process>(){
			@Override
			public int compare(Process o1, Process o2) {
				if(o1.getExpectedRunTime() < o2.getExpectedRunTime())
					return -1;
				else if(o1.getExpectedRunTime() > o2.getExpectedRunTime())
					return 1;
				else
					return 0;
			}
		});
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