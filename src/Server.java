import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
	private static final int PORT = 1234;
	private static PiClass piClass = new PiClass();
	private static int numThreads = Runtime.getRuntime().availableProcessors(); //takes the number of PC cores. 
	private static int numSteps = 100000000; 
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		ServerSocket socketConn = new ServerSocket(PORT);
		ServerThread threads[] = new ServerThread[numThreads];
		
		//time before procedure starts
		long start = System.currentTimeMillis();
		
		for(int i=0; i<numThreads;i++) {
			System.out.println("Server is waiting...");
			Socket pipe = socketConn.accept();
			System.out.println("Received request from" + pipe.getInetAddress());
			threads[i] = new ServerThread(pipe,piClass,i,numThreads,numSteps);
			threads[i].start();
		}
		for(int i=0; i<numThreads;i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		socketConn.close();
		
		// get current time and calculate elapsed time
        long endTime = System.currentTimeMillis();
        System.out.printf("sequential program results with %d steps\n", numSteps);
        System.out.printf("computed pi = %22.20f\n" , piClass.getPiValue());
        System.out.printf("difference between estimated pi and Math.PI = %22.20f\n", Math.abs(piClass.getPiValue() - Math.PI));
        System.out.printf("time to compute = %f seconds\n", (double) (endTime - start) / 1000);
	}

}

class ServerThread extends Thread {
	private Socket pipe;
	private ObjectInputStream serverInputStream;
	private ObjectOutputStream serverOutputStream;
	private PiClass piClass;
	private int numThreads;
	private int numSteps;
	private int threadId;

	public ServerThread(Socket socket, PiClass piClass, int threadId, int numThreads, int numSteps) {
		pipe = socket;
		this.numThreads = numThreads;
		this.numSteps = numSteps;
		this.threadId = threadId;

		try {
			serverInputStream = new ObjectInputStream(pipe.getInputStream());
			serverOutputStream = new ObjectOutputStream(pipe.getOutputStream());
			this.piClass = piClass;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		Reply rep = new Reply();
		Request req;

		try {

			req = new Request(threadId, numThreads, numSteps);
			//send the request
			serverOutputStream.writeObject(req);
			
			//receive the reply.
			rep = (Reply) serverInputStream.readObject();
			
			//process the reply.
			ServerProtocol serverProt = new ServerProtocol(piClass);
			serverProt.processReply(rep);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

class ServerProtocol {
	private PiClass piClass;

	public ServerProtocol(PiClass piClass) {
		this.piClass = piClass;
	}
	public double processReply(Reply theInput) {
		double rep = piClass.executeSum(theInput);
		return rep;
	}
}

