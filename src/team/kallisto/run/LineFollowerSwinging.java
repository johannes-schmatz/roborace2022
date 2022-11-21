package team.kallisto.run;

import lejos.hardware.Sound;
import team.kallisto.Logger;
import team.kallisto.Motors;
import team.kallisto.Sensors;
import team.kallisto.calibration.Calibration;


public class LineFollowerSwinging implements LineFollower {
	/*
	TODO: line following with -25° as MEASURE, follow line based on side of line
	 */
	private static final int tps = 20;
	private static final int mspt = 1000 / tps;

	private final Calibration calibration;
	private int lineAngle = 0;
	private int blackAngleSum = 0;
	private int blackAngleCount = 0;
	private int whiteAngleCount = 0;

	public LineFollowerSwinging(Calibration calibration) {
		this.calibration = calibration;

		Motors.startSwinging(true);
		Motors.STEER.setSpeedRelativeToMax(100);
		Motors.DRIVE.setSpeedRelativeToMax(15);
		Sound.beepSequenceUp();
		Motors.DRIVE.forward();
	}

	@Override
	public void tick() {
		long end = System.currentTimeMillis() + mspt;

		int i = 0;
		while (System.currentTimeMillis() < end) {
			measure();
			i++;
		}
		Logger.println(String.valueOf(i));

		drive();
	}

	private void drive() {
		Motors.STEER.rotateTo((int) (-lineAngle * 1.0), true);
	}

	private void measure() {
		int currentAngle = Motors.MEASURE.getTachoCount();
		int value = Sensors.getBrightness();

		int switchedDirection = Motors.swingAroundTick();

		if (switchedDirection != 0 && blackAngleCount > 5 && whiteAngleCount > 5) {
			lineAngle = (blackAngleSum / blackAngleCount) +
					(switchedDirection > 0 ? -calibration.angleOffset : calibration.angleOffset);
			blackAngleSum = 0;
			blackAngleCount = 0;
			whiteAngleCount = 0;
		}

		boolean isBlack = value >= calibration.triggerBrightness;

		if (isBlack) {
			whiteAngleCount++;
		} else {
			blackAngleSum += currentAngle /*+ (Motors.MEASURE.isMovingForward() ? -1 : 1)*/;
			blackAngleCount++;
		}
	}
}
