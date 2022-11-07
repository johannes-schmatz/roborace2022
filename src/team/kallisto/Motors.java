package team.kallisto;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;

public class Motors {
	private static void close() {
		rotateTo0AndFltAndReset();

		DRIVE.close();
		MEASURE.close();
		STEER.close();
	}

	public static void rotateTo0AndFltAndReset() {
		MEASURE.rotateTo(0);
		MEASURE.flt();

		STEER.rotateTo(0);
		STEER.flt();

		reset();
	}

	public static void reset() {
		MEASURE.setSpeedRelativeToMax(100);

		DRIVE.resetTachoCount();
		MEASURE.resetTachoCount();
		STEER.resetTachoCount();
	}

	public static final LimitedScaledMotor DRIVE = new LimitedScaledMotor(new EV3LargeRegulatedMotor(MotorPort.D));
	public static final LimitedScaledMotor MEASURE = new LimitedScaledMotor(new EV3MediumRegulatedMotor(MotorPort.B),
			73, 6);
	public static final LimitedScaledMotor STEER = new LimitedScaledMotor(new EV3LargeRegulatedMotor(MotorPort.A), 45, 15);

	static {
		reset();
	}

	public static void slowDrivingDown() {
		// TODO: code, multiple calls should keep the speed the same

	}

	public static void speedDrivingUp() {
		// TODO: code, multiple calls should keep the speed the same
	}

	public static int swingAroundTick() {
		int currentAngle = MEASURE.getTachoCount();
		if (MEASURE.hasReachedNegativeLimit(currentAngle)) {
			MEASURE.rotateTo(MEASURE.positiveLimit, true);
			return 1;
		} else if (MEASURE.hasReachedPositiveLimit(currentAngle)) {
			MEASURE.rotateTo(MEASURE.negativeLimit, true);
			return -1;
		}
		return 0;
	}

	public static void startSwinging() {
		MEASURE.rotateTo(MEASURE.positiveLimit, true);
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(Motors::close));
	}
}
