import java.util.Random;


public class Process {
	private float arrivalTime;
	private float expectedRunTime;
	private char processId;
	private int priority;
	private Random rand;
	
	public Process(char processId)
	{
		rand = new Random();
		arrivalTime = 0 + (99 - 0) * rand.nextFloat(); // formula for setting the range from 0 to 99 floating value
		expectedRunTime = (float) (0.1 + (10 - 0.1) * rand.nextFloat()); // formula for setting the range from 0.1 to 10 floating value
		priority = rand.nextInt(4) + 1; // formula for range from 1 to 4
		this.processId = processId;
	}
	public double getArrivalTime() {
		return arrivalTime;
	}
	
	public double getExpectedRunTime() {
		return expectedRunTime;
	}
	
	public char getProcessId() {
		return processId;
	}
	public void setProcessId(char processId) {
		this.processId = processId;
	}
	public int getPriority() {
		return priority;
	}
	
}
