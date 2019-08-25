import java.io.Serializable;
//Request is the object that server sends to client
public class Request implements Serializable{
	private int threadId;
	private int numThreads;
	private int numSteps;
	
	public Request(int threadId, int numThreads, int numSteps) {
		this.threadId = threadId;
		this.numThreads = numThreads;
		this.numSteps = numSteps;
	}

	public int getThreadId() {
		return threadId;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public int getNumSteps() {
		return numSteps;
	}
	
	

	
	
}
