package team.kallisto.run;

import lejos.hardware.Button;
import lejos.utility.Delay;
import team.kallisto.Logger;
import team.kallisto.Motors;
import team.kallisto.Sensors;
import team.kallisto.calibration.Calibration;

public class Run {
	public static void run(Calibration calibration) {
		int state = 0; // 0 = on line, 1 = passing between two
		int lane = 1; // 0 means rightmost lane, 1 means center, 2 leftmost
		int switchOffCountdown = 0;

		LineFollower follower = new LineFollowerHalfLine(calibration);
		while (!Button.ESCAPE.isDown()) {
			int newLane = lane == 0 ? 1 : 0;
			if (state == 0 && switchOffCountdown == 0) {
				int frontDistance = Sensors.getDistance();
				int angle = Motors.MEASURE.getTachoCount();
				Logger.println("angle: " + angle + ", frontDistance: " + frontDistance);
				if (frontDistance <= 350 /*&& angle <= 5 && angle >= -5*/) {
					state = 1;
					beforeSwitchingLanes(lane, newLane);
					//Motors.DRIVE.stop();
					Logger.println("switch lanes start " + angle + " from: " + lane + " to: " + newLane);
					lane = newLane;
				}
			}

			if (switchOffCountdown > 0) switchOffCountdown--;

			if (state == 0) {
				follower.tick();
			} else {
				boolean finishedSwitching = Run.switchLanesTick(calibration);
				if (finishedSwitching) {
					state = 0;
					Logger.println("switch lines end");
					switchOffCountdown = 100;
				}
			}
		}

		Motors.DRIVE.flt();
	}

	private static boolean switchLanesTick(Calibration calibration) {
		int count = Motors.STEER.getTachoCount();
		if (count + 2 > -switchLaneAngle || count - 2 < switchLaneAngle) {
			Motors.STEER.rotateTo(0, true);
		}

		if (Sensors.getBrightness() < calibration.triggerBrightness) {
			//Delay.msDelay(500);
			//Motors.startSwinging(laneNumberRose);
			//TODO: invert later on, make compatible with both directions
			Motors.STEER.rotateTo(switchLaneAngle, false);
			Motors.STEER.rotateTo(0, true);
			Motors.speedDrivingUp();
			return true;
		}
		//TODO: code
		// return true for finished switching
		// also launch motors

		return false;
	}

	private static final int switchLaneAngle = 45;
	//TODO: turns out the line angles can be max. 30° (in front)
	private static void beforeSwitchingLanes(int oldLane, int newLane) {
		Motors.slowDrivingDown();
		if (newLane > oldLane) {
			Motors.STEER.rotateTo(switchLaneAngle, true);
			laneNumberRose = true;
		} else if (newLane < oldLane) {
			laneNumberRose = false;
			Motors.STEER.rotateTo(-switchLaneAngle, true);
		}
		//Delay.msDelay(2000);
		Delay.msDelay(400);
		Motors.MEASURE.rotateTo(0, false);
		Motors.STEER.rotateTo(0, true);
	}

	private static boolean laneNumberRose = false;
}
