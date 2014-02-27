import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class SJF extends Scheduler {
	private double totalTurnAroundTime, Throughput, aveTurnAroundTime, aveWaitingTime, totalWaitTime, aveResponseTime, totalTime;
	ArrayList<Double> averageResponseTime = new ArrayList<Double>();	
	ArrayList<Double> averageWaitTime = new ArrayList<Double>();
	ArrayList<Double> averageTurnAround = new ArrayList<Double>();
	ArrayList<Double> burstTime = new ArrayList<Double>();
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
			
		});
		
		
		Process p = null;
		
		double currentPosition = 0, currentWait = 0, currentResponse = 0,TAT = 0, currentTotal = 0, cumBurstTime = 0;
		//System.out.printf("Process ID    ||    Run Time   ||   Arrival Time || Priority \n");
		while(processQueue.size() > 0 && totalTime <= 99) {
			if(processQueue.get(0).getArrivalTime() <= totalTime) {
			p = processQueue.remove(0);
			float temp = (float) p.getExpectedRunTime();
			while(temp > 0) {
				//System.out.printf("%5c  %20.4f %16.4f %10d \n",p.getProcessId(),p.getExpectedRunTime(),p.getArrivalTime(),p.getPriority());
				System.out.print(p.getProcessId());
				temp--;
				totalTime++;
			}
			
			
			if (i > 1) {	
					if(currentPosition < p.getExpectedRunTime()) {
						currentPosition = p.getExpectedRunTime();
						cumBurstTime =  p.getExpectedRunTime();
					}
					if (p.getArrivalTime() > currentPosition) {
						currentPosition = p.getArrivalTime();
						cumBurstTime = p.getArrivalTime();
					}
				//waiting is arrive to exectute ; TAT is arrive to finish
					currentWait = currentPosition - p.getArrivalTime();
					currentPosition = currentPosition + p.getExpectedRunTime();
				
				cumBurstTime = cumBurstTime + currentPosition + p.getExpectedRunTime();
				//TAT = cumBurstTime - p.getArrivalTime();
				TAT = currentWait + p.getExpectedRunTime();
		//		System.out.println("CP" + currentPosition + " CW" + currentWait + " CBT" +cumBurstTime + " TAT" + TAT);
		//		currentResponse = currentResponse + p.getExpectedRunTime();
	//			System.out.println("cumBurstTime " + cumBurstTime);
		//		System.out.println("current Wait " + currentWait);
		//		currentTotal = currentTotal + currentResponse;
			//	System.out.println("current TAT " + TAT);
			//	totalTurnAroundTime =cumBurstTime  + p.getExpectedRunTime() + currentTotal;
		//		totalTurnAroundTime =currentResponse  - p.getArrivalTime();
		//		System.out.println("totalTurnAroundTime " + totalTurnAroundTime);
				i++;
			//	currentAverage = currentTotal / i;
			//	averageResponseTime.add(currentAverage);
				burstTime.add(p.getExpectedRunTime());
				averageTurnAround.add(TAT);
				throughput.add(i);
				averageWaitTime.add(currentWait);
			}
			else {
				//start with current position = 0,
				currentPosition = p.getExpectedRunTime();
				currentWait = p.getArrivalTime() - 0;
				TAT =  p.getExpectedRunTime() - p.getArrivalTime();
				cumBurstTime = p.getExpectedRunTime();
//				currentResponse = currentResponse + p.getArrivalTime();
				i++;
				//wait time and response time are the same for nonpreemptive
			//	System.out.println("current cumBurstTime " + cumBurstTime);
			//	System.out.println("current Position " + currentPosition);
			//	averageResponseTime.add(currentWait);
				burstTime.add(p.getExpectedRunTime());
				averageTurnAround.add(TAT);
				throughput.add(i);
				averageWaitTime.add(currentWait);
			}
		}
			else {
				System.out.print(" ");
				totalTime++;
			}
		}
		calculateAverage();
	}
	
	public void calculateAverage() {
		double temp_TAT = 0, average= 0, temp = 0, temp2 = 0,total = 0,average_total=0;
		
		for(int i = 0 ; i < averageTurnAround.size(); i++) {
			temp_TAT = averageTurnAround.get(i);
			total = total + temp_TAT;
			average = total / i;
			setaverageTAT(average);
		}
		for(int j = 0 ; j < averageWaitTime.size(); j++) {
			temp = averageWaitTime.get(j);
		//	System.out.println(temp);
			temp2 = temp;
			average_total = average_total + temp2;
		//	System.out.println("total " + average_total);
			average = average_total / j;
			setAverageWait(average);
		}

		double tempBurst = 0, tempTotal = 0, throughput;
		for(int k = 0 ; k < burstTime.size(); k++) {
			tempBurst = burstTime.get(k);
			tempTotal = tempBurst + tempTotal;
			throughput = k/tempTotal;
			setThroughput(throughput);
		}
		
		System.out.printf("\n The statistics for SJF are as follows : \n");
		System.out.printf("average response time is : %9.3f \n", getAverageWait());
		System.out.printf("average waiting time is : %10.3f\n", getAverageWait());
		System.out.printf("average turnaround time is : %7.2f\n", getaverageTAT());
		System.out.printf("throughput is :  %21f \n", getThroughput());

	}
	
	public void setaverageTAT(double aveTurnAroundTime) {
		this.aveTurnAroundTime = aveTurnAroundTime;
	}
	public double getaverageTAT() {
		return aveTurnAroundTime;
	}	
	public void setThroughput(double Throughput) {
		this.Throughput = Throughput;
	}
	public double getThroughput() {
		return Throughput;
	}	
	public void setAverageWait(double aveWaitingTime) {
		this.aveWaitingTime = aveWaitingTime;
	}
	public double getAverageWait() {
		return aveWaitingTime;
	}	
	
}
