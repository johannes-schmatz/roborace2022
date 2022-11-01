package team.kallisto.task;

import lejos.hardware.Sound;
import team.kallisto.Logger;
import team.kallisto.calibration.Calibration;

public class CalibrateTask implements Task {
	private final boolean recalibrate;
	public CalibrateTask(boolean recalibrate) {
		this.recalibrate = recalibrate;
	}

	@Override
	public void run() {
		if (Calibration.getCalibration() == null || recalibrate) {
			if (recalibrate)
				Logger.println("there was already a calibration, overwriting...");
			Calibration.calibrate();
			Sound.beep();
		} else {
			Logger.println("can't calibrate again because there's already a calibration");
		}
	}

	@Override
	public String getName() {
		return recalibrate ? "recalibrate" : "calibrate";
	}
}
