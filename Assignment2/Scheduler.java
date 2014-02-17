import java.util.ArrayList;

/*
 * Abstract class for scheduler such as FCFSScheduler, SJFScheduler and SRTScheduler
 * */
public abstract class Scheduler {
		protected ArrayList<Process> processQueue;
		protected ArrayList<Process> processList; // a set of processes to be run
		public abstract void run();
		
}
