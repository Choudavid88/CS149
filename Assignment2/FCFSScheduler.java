import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class FCFSScheduler extends Scheduler {
	private double aveTurnAroundTime;
	private double aveWaitingTime;
	private double aveResponseTime;
	private double totalTime;
	private ArrayList<Double> waitingTimeList;
	private ArrayList<Double> turnAroundTimeList;
	private ArrayList<Double> responseTimeList;
	private ArrayList<Character> output;
	public FCFSScheduler()
	{
		processQueue = new ArrayList<Process>(); 
		processList = new ArrayList<Process>();
		turnAroundTimeList= new ArrayList<Double>();
		waitingTimeList= new ArrayList<Double>();
		responseTimeList = new ArrayList<Double>();
		output = new ArrayList<Character>();
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
		calculate();
		
	}
	//print the time chart
	public void printProcess() // by jimmy again
	{
		//the correction has been made here
		//solution: create an instance variable called totalTime for keeping track the total it takes to run the processes
		Process p = null;
		while(processQueue.size() > 0 && totalTime < 99)
		{
			if(processQueue.get(0).getArrivalTime() <= totalTime)
			{
				p = processQueue.remove(0);
				float temp = (float) p.getExpectedRunTime();
				while(temp > 0)
				{
					System.out.print(p.getProcessId());
					output.add(p.getProcessId());
					temp--;
					totalTime++;
					}	
			} // if no process has arrived at this time, we just increment the totalTime and print a space
			else
			{
				System.out.print(" "); //print a space indicate this quanta does nothing
				output.add(' ');
				totalTime++;		
			}
		}
	}

	public void calculate(){
		int lastOfIndex = output.size();
		Collections.sort(processList, new Comparator<Process>(){

			@Override
			public int compare(Process o1, Process o2) {
				if(o1.getArrivalTime() < o2.getArrivalTime())
					return -1;
				else if(o1.getArrivalTime() > o2.getArrivalTime())
					return 1;
				else
					return 0;
			}

		});
		for(Process p : processList){
			
			boolean found = false;
			
			for(int i = lastOfIndex -1 ; i > 0 && !found ; i--){
				if(p.getProcessId() == output.get(i)){
					double turnAroundTime = (i - p.getArrivalTime()) + 1;
					double waitingTime = turnAroundTime - p.getExpectedRunTime();
					turnAroundTimeList.add(turnAroundTime);
					waitingTimeList.add(waitingTime);
					found = true;	
				}
			}
		}
		
		for(Process p : processList){
			boolean found = false;
			for(int i = 0; i < output.size() && !found; i++){
				if(p.getProcessId() == output.get(i))
				{
					double responseTime = i - p.getArrivalTime();
					responseTimeList.add(responseTime);
					found = true;
				}
			}
		}
		System.out.println("");
		aveTurnAroundTime = aveCalculation(this.turnAroundTimeList);
		System.out.println("Average turnaround time is " + this.aveTurnAroundTime);
		aveWaitingTime = aveCalculation(this.waitingTimeList);
		System.out.println("Average waiting time is " + this.aveWaitingTime);
		aveResponseTime = aveCalculation(this.responseTimeList);
		System.out.println("Average response time is " + this.aveResponseTime);
		System.out.println("Throughput is " + processList.size()/totalTime);
	}
	public void createProcess(int numberOfProcesses)
	{
		int i;
		for(i=0; i < numberOfProcesses; i++)
		{
			processList.add(new Process((char)(i + 97)));
		}
	}
	
	public double aveCalculation(ArrayList<Double> list){
		double total = 0;
		for (int i = 0; i < list.size(); i++){
			total += list.get(i);
		}
		return total/list.size();
	}


}