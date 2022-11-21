package team.kallisto.task;

import team.kallisto.calibration.Calibration;
import team.kallisto.run.Run;

public class RunTask implements Task {
	@Override
	public void run() {
		Run.run(Calibration.getCalibration());
	}

	@Override
	public String getName() {
		return "run";
	}
}
