import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SJF extends Scheduler {
	private double totalTurnAroundTime, aveTurnAroundTime, aveWaitingTime, totalWaitTime, aveResponseTime, totalTime;
	ArrayList<Double> averageResponseTime = new ArrayList<Double>();	
	ArrayList<Double> averageWaitTime = new ArrayList<Double>();
	ArrayList<Double> averageTurnAround = new ArrayList<Double>();
	ArrayList<Integer> throughput = new ArrayList<Integer>();

	
	public SJF() {
		processQueue= new ArrayList<Process>();
		processList = new ArrayList<Process>();
		totalTime = 0;
	}

	public void createProcess(int numberOfProcesses)
	{
		int i;
		for(i=0; i < numberOfProcesses; i++){
			processList.add(new Process((char)(i + 97)));
		}

	}
	@Override
	public void run() {
		
		int i = 1;
		for(Process p: processList) {
			processQueue.add(p);
		}
		
		//Collections.sort(processQueue.subList(1, processQueue.size()), new Comparator<Process>(){
		Collections.sort(processQueue, new Comparator<Process>(){	
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
		
		Process p = null;
		
		double currentPosition = 0, currentResponse = 0,currentAverage = 0, currentTotal = 0, cumBurstTime = 0;
		while(processQueue.size() > 0 && totalTime <= 99) 
			if(processQueue.get(0).getExpectedRunTime() <= totalTime) {
			p = processQueue.remove(0);
			float temp = (float) p.getExpectedRunTime();
			while(temp > 0) {
				System.out.printf("%5c  %20.4f %16.4f %10d \n",p.getProcessId(),p.getExpectedRunTime(),p.getArrivalTime(),p.getPriority());
				temp--;
				totalTime++;
			}
			
			
			if (i > 1) {		
				currentResponse = currentResponse + p.getExpectedRunTime();
				currentTotal = currentTotal + currentResponse;
				totalTurnAroundTime =cumBurstTime  + p.getExpectedRunTime() + currentTotal;
				i++;
				currentAverage = currentTotal / i;
				averageResponseTime.add(currentAverage);
				averageTurnAround.add(totalTurnAroundTime);
				throughput.add(i);
				averageWaitTime.add(currentResponse);
			}
			else {
				currentResponse = p.getExpectedRunTime();
				currentPosition = p.getExpectedRunTime();
				currentTotal = currentTotal + currentResponse;
				cumBurstTime = p.getExpectedRunTime() + currentResponse + currentAverage;
				currentResponse = currentPosition;
				i++;
				averageResponseTime.add(currentAverage);
				averageTurnAround.add(cumBurstTime);
				throughput.add(i);
				averageWaitTime.add(currentResponse);
			}
		}
			else {
				totalTime++;
			}
		
		calculateAverage();
	}
	
	public void calculateAverage() {
		double average= 0, temp = 0, total = 0;
		for(int i = 0 ; i < averageTurnAround.size(); i++) {
			temp = averageTurnAround.get(i);
			total = total + temp;
			average = total / i;
			setaverageTAT(average);
		}
		System.out.printf("average response time is : %9.3f \n", averageResponseTime.get(averageResponseTime.size()-2));
		System.out.printf("average waiting time is : %10.3f\n", averageResponseTime.get(averageResponseTime.size()-1));
		System.out.printf("average turnaround time is : %2.2f\n", getaverageTAT());
		System.out.printf("throughput is :  %16d \n", throughput.get(throughput.size()-1));

	}
	
	public void setaverageTAT(double aveTurnAroundTime) {
		this.aveTurnAroundTime = aveTurnAroundTime;
	}
	public double getaverageTAT() {
		return aveTurnAroundTime;
	}	
	
}
