package team.kallisto.run;

import lejos.hardware.Sound;
import team.kallisto.Logger;
import team.kallisto.Motors;
import team.kallisto.Sensors;
import team.kallisto.calibration.Calibration;


public class LineFollowerSwinging {
	/*
	TODO: line following with -25° as MEASURE, follow line based on side of line
	 */
	private static final int tps = 20;
	private static final int mspt = 1000 / tps;
	public static void tick(Calibration calibration) {
		long end = System.currentTimeMillis() + mspt;

		int i = 0;
		while (System.currentTimeMillis() < end) {
			measure(calibration);
			i++;
		}
		Logger.println("" + i);

		drive();
	}

	private static int lineAngle = 0;

	public static void reset(Calibration calibration) {
		Motors.startSwinging(true);
		lineAngle = 0;
		blackAngleSum = 0;
		blackAngleCount = 0;
		whiteAngleCount = 0;
		Motors.STEER.setSpeedRelativeToMax(100);
		Motors.DRIVE.setSpeedRelativeToMax(15);
		Sound.beepSequenceUp();
		Motors.DRIVE.forward();
	}

	private static void drive() {
		Motors.STEER.rotateTo((int) (-lineAngle * 1.0), true);
		// TODO: drive code
		//Logger.println("drive(): lineAngle: %s", lineAngle);
	}

	private static int blackAngleSum = 0;
	private static int blackAngleCount = 0;
	private static int whiteAngleCount = 0;
	private static int lineAngle0;

	private static void measure(Calibration calibration) {
		int currentAngle = Motors.MEASURE.getTachoCount();
		int value = Sensors.getBrightness();

		int switchedDirection = Motors.swingAroundTick();

		if (switchedDirection != 0 && blackAngleCount > 5 && whiteAngleCount > 5) {
			lineAngle0 = (blackAngleSum / blackAngleCount);
			lineAngle = (blackAngleSum / blackAngleCount) + (switchedDirection > 0 ? -calibration.angleOffset : calibration.angleOffset);
			blackAngleSum = 0;
			blackAngleCount = 0;
			whiteAngleCount = 0;
		}

		boolean isBlack = value >= calibration.triggerBrightness;

		//Logger.println("currentAngle: %4s, value: %4s, lineAngle: %4s, sum: %6s, count: %6s, isBlack: %6s, lA0:
		// %6s, swD: %6s, wAC: %5s", currentAngle, value, lineAngle, blackAngleSum, blackAngleCount, !isBlack, lineAngle0, switchedDirection, whiteAngleCount);

		if (isBlack) {
			whiteAngleCount++;
		} else {
			blackAngleSum += currentAngle /*+ (Motors.MEASURE.isMovingForward() ? -1 : 1)*/;
			blackAngleCount++;
		}
	}
}
