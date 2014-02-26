import java.util.ArrayList;
import java.util.Random;


public class Tester {
	public static void main(String[] args)
	{	
		FCFSScheduler fs = new FCFSScheduler();
		SJF sjf = new SJF();
		fs.createProcess(26); // let's make it 26!!!
		sjf.createProcess(26);
		sjf.run();
		fs.run();
	}
}
