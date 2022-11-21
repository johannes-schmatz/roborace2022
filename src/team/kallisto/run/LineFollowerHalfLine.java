package team.kallisto.run;

import lejos.hardware.Sound;
import team.kallisto.Logger;
import team.kallisto.Motors;
import team.kallisto.Sensors;
import team.kallisto.calibration.Calibration;
import team.kallisto.pid.Pid;


public class LineFollowerHalfLine implements LineFollower {
	/*
	TODO: line following with -25° as MEASURE, follow line based on side of line
	 */
	private static final int tps = 20;
	private static final int mspt = 1000 / tps;
	private final Pid pid;

	public LineFollowerHalfLine(Calibration calibration) {

		pid = new Pid(1, /*0.01*/0, -0.5, calibration.averageBrightness, calibration.averageBrightness);
		Motors.MEASURE.rotateTo(15, false);

		Motors.STEER.setSpeedRelativeToMax(100);
		Motors.DRIVE.setSpeedRelativeToMax(50);
		Sound.beepSequenceUp();
		Motors.DRIVE.forward();
	}

	@Override
	public void tick() {
		long end = System.currentTimeMillis() + mspt;

		int i = 0;
		while (System.currentTimeMillis() < end) {
			tickDrive();
			i++;
		}
		Logger.println(String.valueOf(i));
	}

	private void tickDrive() {
		int measurement = Sensors.getBrightness();

		double correctionValue = pid.pid(measurement);

		Logger.println("measurement: " + measurement + " correctionValue: " + correctionValue);

		Motors.STEER.rotateTo((int) correctionValue, true);
	}
}
