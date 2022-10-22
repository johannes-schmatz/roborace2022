import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.Port;
import lejos.robotics.RegulatedMotor;

public class Motors {
	public static void close() {
		A.close();
		B.close();
		C.close();
		D.close();
	}

	private enum MotorType {
		LARGE,
		MEDIUM
	}

	private static RegulatedMotor motor(Port port, MotorType type) {
		switch (type) {
			case LARGE:
				return new EV3LargeRegulatedMotor(port);
			case MEDIUM:
				return new EV3MediumRegulatedMotor(port);
			default:
				throw new RuntimeException("MotorType: " + type + " not found!");
		}
	}

	public static final RegulatedMotor A = motor(MotorPort.A, MotorType.MEDIUM);
	public static final RegulatedMotor B = motor(MotorPort.B, MotorType.LARGE);
	public static final RegulatedMotor C = motor(MotorPort.C, MotorType.LARGE);
	public static final RegulatedMotor D = motor(MotorPort.D, MotorType.MEDIUM);
}
