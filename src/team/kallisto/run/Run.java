package team.kallisto.run;

import lejos.hardware.Button;
import team.kallisto.Motors;
import team.kallisto.Sensors;
import team.kallisto.calibration.Calibration;

public class Run {
	public void run(Calibration calibration) {
		int state = 0; // 0 = on line, 1 = passing between two

		Motors.startSwinging();
		LineFollower.reset();
		while (!Button.ESCAPE.isDown()) {
			int frontDistance = Sensors.getDistance();
			//if (frontDistance <= 50)
			//	state = 1;

			if (state == 0) {
				LineFollower.run(calibration);
			} else if (state == 1) {
				Run.switchLanes();
			}
		}

		Motors.DRIVE.flt();
	}

	private static void switchLanes() {
		//TODO: code
	}
}
