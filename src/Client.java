import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	// final variables for HOST and PORT of connection
	private static final String HOST = "localhost";
	private static final int PORT = 1234;

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub

		// make the connection
		Socket socketConn = new Socket(HOST, PORT);

		// Streams that read or write object. In this case object can be Reply or
		// Request.
		ObjectOutputStream clientOutputStream = new ObjectOutputStream(socketConn.getOutputStream());
		ObjectInputStream clientInputStream = new ObjectInputStream(socketConn.getInputStream());

		// create the ClientProtocol that will handle the Request.
		ClientProtocol clientProt = new ClientProtocol();

		Request req;
		Reply rep = new Reply();
		//Request from the server
		req = (Request) clientInputStream.readObject();
		//gets a reply from clientProtocol and send it back to server (the reply is a part of pi)
		rep = clientProt.processRequest(req);
		clientOutputStream.writeObject(rep);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		clientOutputStream.close();
		clientInputStream.close();

		socketConn.close();

	}

}

class ClientProtocol {
	// no private variables cause we don't have constructor to initialize them. 
	
	int clientId; 
	// The number of threads are equal with number of PC cores. See Server class. 
	//This number can change without consequences. 
	int numClients; 
	int numSteps; 
	int myStart;
	int myStop;
	double pi;
	Reply rep = new Reply();
	// shared data structure
	double[] a; 
	//find the number of threads
	int numThreads = Runtime.getRuntime().availableProcessors();

	public Reply processRequest(Request theInput) throws IOException {
		
		clientId = theInput.getThreadId();
		numClients = theInput.getNumThreads();
		numSteps = theInput.getNumSteps();
		//because every client has its part
		numSteps = numSteps/numClients;
		a = new double[numThreads];
		//Assign 0 to every table's position.
		for (int i = 0; i < numThreads; i++)
			a[i] = 0;
		
		PiThread[] threads = new PiThread[numThreads];

		// thread execution
		for(int i=0;i<numThreads;i++) {
			threads[i] = new PiThread(i,clientId,numThreads,a,numSteps,numClients);
			threads[i].start();
		}
		
		// wait for threads to terminate
		for (int i = 0; i < numThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
			}
		}
		
		for (int i = 0; i < numThreads; i++) {
			pi += a[i];
		}
		System.out.println("Synolo "+pi);
		rep.setPiValue(pi);
		return rep;
	}

}

class PiThread extends Thread {
	private int clientId;
    private int numThreads;
    private double [] table;
    private int numSteps;
    private int myStart;
    private int myStop;
	private int threadId;
	private int numClients;
    
    
    public PiThread(int threadId, int clientId,int numThreads, double[] table, int numSteps, int numClients) {
    	this.numClients = numClients;
		this.clientId = clientId;  
		this.numThreads = numThreads;
		this.table = table;
		this.numSteps = numSteps;
		this.threadId = threadId;

		//myStart will begin from the part of client (for example if it is client 1 will begin from numSteps*1) + threadId part
		myStart = (clientId*numSteps)+threadId * ((numSteps) / this.numThreads);
		System.out.println(myStart);
        myStop = myStart + ((numSteps) / this.numThreads);
	}



	public void run() {
		System.out.println("Thread "+ threadId+ " of client " + clientId+"\n");
		//calculate step with the real numSteps (which is numSteps*numClients)
    	double step = 1.0 / (double)(numSteps*numClients);
    	double sum = 0;
        /* do computation */
        for (long i = myStart; i < myStop; ++i) {
            double x = ((double)i+0.5)*step;
            sum += 4.0/(1.0+x*x);
        }
        double pi = sum * step;
        table[threadId] = pi;
        System.out.println(pi);
    }
    
}
