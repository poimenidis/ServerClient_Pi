import java.io.Serializable;
//Reply is the object that client sends to server as a response to server's request.
public class Reply implements Serializable {
	private double piValue;
	
	public Reply() {
		piValue = 0;
	}

	public double getPiValue() {
		return piValue;
	}

	public void setPiValue(double piValue) {
		this.piValue = piValue;
	}

	
	
}
