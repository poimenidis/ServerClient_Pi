
public class PiClass {
	private double piValue;
	
	public PiClass() {
		piValue = 0;
	}
	
	public synchronized double executeSum(Reply theInput) {
		double repliedPi = theInput.getPiValue();
		piValue += repliedPi;
		this.notifyAll();
		return piValue;
	}

	public double getPiValue() {
		return piValue;
	}
	
	
}
