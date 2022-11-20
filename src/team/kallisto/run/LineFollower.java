package team.kallisto.run;

import lejos.hardware.Sound;
import team.kallisto.Logger;
import team.kallisto.Motors;
import team.kallisto.Sensors;
import team.kallisto.calibration.Calibration;
import team.kallisto.pid.Pid;


public class LineFollower {
	/*
	TODO: line following with -25° as MEASURE, follow line based on side of line
	 */
	private static final int tps = 20;
	private static final int mspt = 1000 / tps;
	public static void tick(Calibration calibration) {
		long end = System.currentTimeMillis() + mspt;

		int i = 0;
		while (System.currentTimeMillis() < end) {
			tickDrive(calibration);
			i++;
		}
		Logger.println(String.valueOf(i));
	}

	public static void reset(Calibration calibration) {
		int average = (calibration.minimumBrightness + calibration.maximumBrightness) / 2;
		pid = new Pid(1, 0.01, -0.5, average);
		Motors.MEASURE.rotateTo(25, false);

		Motors.STEER.setSpeedRelativeToMax(100);
		Motors.DRIVE.setSpeedRelativeToMax(15);
		Sound.beepSequenceUp();
		Motors.DRIVE.forward();
	}

	private static void tickDrive(Calibration calibration) {
		int measurement = Sensors.getBrightness();

		double correctionValue = pid.pid(measurement);

		Logger.println("measurement: " + measurement + " correctionValue: " + correctionValue);

		Motors.STEER.rotateTo((int) correctionValue, true);
	}

	private static Pid pid;
}
