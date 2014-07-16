/**
 * @author ariel
 *	Implements an iterative logistic map.
 */
public class LogisticMap {
	private double R;
	private double x;
	
	public double next() {
		x = R * x * (1-x);
		return x;
	}
	
	public void iterate(int n) {
		for (int i=0; i<n; i++) {
			x = R * x * (1-x);
		}
	}

	public LogisticMap(double r, double x0) {
		R = r;
		x = x0;
	}
	
	public void setR(double r) {
		if (0<=r && r<=4) {
			R = r;
		}
		else {
			System.out.println("ERROR: r must be between 0 and 4");//ERROR
		}
	}
		
	public void setX(double x0) {
		if (0<=x0 && x0<=1) {
			x = x0;
		}
		else {
			System.out.println("ERROR: x0 must be between 0 and 1");//ERROR
		}
	}
	
	public double getX() {
		return x;
	}
}
