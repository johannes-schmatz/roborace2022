package team.kallisto.pid;

public class Pid {
	private double k_p;
	private double k_i;
	private double k_d;

	private double lastValue = 0;
	private double integral = 0;

	public Pid(double k_p, double k_i, double k_d) {
		this.k_p = k_p;
		this.k_i = k_i;
		this.k_d = k_d;
	}

	public Pid(double k_p, double k_i, double k_d, double lastValue) {
		this(k_p, k_i, k_d);
		this.lastValue = lastValue;
	}

	public double pid(double value) {
		double d = value - lastValue;
		lastValue = value;

		integral += value;

		return k_p * value + k_i * integral + k_d * d;
	}
}
