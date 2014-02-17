import java.util.ArrayList;
import java.util.Random;


public class Tester {
	public static void main(String[] args)
	{	
		FCFSScheduler fs = new FCFSScheduler();
		fs.createProcess(26);
		
		fs.run();
	}
}
