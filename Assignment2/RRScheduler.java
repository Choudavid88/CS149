import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class RRScheduler extends Scheduler {
	private double aveTurnAroundTime;
	private double aveWaitingTime;
	private double aveResponseTime;
	private double totalTime;
	private ArrayList<Double> turnAroundTimeList;
	private ArrayList<Double> waitingTimeList;
	private ArrayList<Double> responseTimeList;
	private ArrayList<Process> waitingQueue;
	private ArrayList<Character> output;
	
	public RRScheduler()
	{
		processQueue = new ArrayList<Process>(); 
		processList = new ArrayList<Process>();
		waitingQueue = new ArrayList<Process>();
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
		/*for(Process e: processQueue)
			System.out.println(e.getProcessId() +" Arr "+ e.getArrivalTime()+" RUN "+ e.getExpectedRunTime());
		*/
		printProcess();
		calculate();
		
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
				output.add(p.getProcessId());
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
				output.add(p.getProcessId());
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
				output.add(' ');
				totalTime++;
			}
		}
		while (waitingQueue.size() > 0){
			p = waitingQueue.remove(0);				
			float temp = (float) p.getExpectedRunTime();
			System.out.print(p.getProcessId());
			output.add(p.getProcessId());
			temp--;
			totalTime++;
			if(temp > 0){
				p.updateExpectedRunTime(temp);
				waitingQueue.add(p);
			}
		}
		
		System.out.println("");
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
					double turnAroundTime = i - p.getArrivalTime();
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
		aveTurnAroundTime = aveCalculation(this.turnAroundTimeList);
		System.out.println("Average turnaround time: " + this.aveTurnAroundTime);
		aveWaitingTime = aveCalculation(this.waitingTimeList);
		System.out.println("Average waiting time: " + this.aveWaitingTime);
		aveResponseTime = aveCalculation(this.responseTimeList);
		System.out.println("Average response time: " + this.aveResponseTime);
		System.out.println("Throughput: " + processList.size()/totalTime);
	}
	public double aveCalculation(ArrayList<Double> list){
		double total = 0;
		for (int i = 0; i < list.size(); i++){
			total += list.get(i);
		}
		return total/list.size();
	}

}