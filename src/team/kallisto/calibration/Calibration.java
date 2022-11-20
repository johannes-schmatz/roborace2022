package team.kallisto.calibration;

import lejos.hardware.Button;
import lejos.utility.Delay;
import team.kallisto.Logger;
import team.kallisto.Motors;
import team.kallisto.Sensors;
import team.kallisto.task.Task;

public class Calibration {
	private static final Calibration DEFAULT = new Calibration(20, 2, 6, 28);
	private static Calibration calibration = DEFAULT; // TODO: maybe change back to null to require user to run it again

	public static class DefaultCalibrationSetter implements Task {
		@Override
		public void run() {
			calibration = DEFAULT;
		}

		@Override
		public String getName() {
			return "default calibration";
		}
	}

	public static void calibrate() {
		Logger./*out.*/println("set robot on the drive line and press any button");
		Button.waitForAnyPress();

		// TODO: calibrate everything here, you can also drive
		Motors.MEASURE.rotateToNegativeLimit(false);

		Delay.msDelay(400);

		Motors.MEASURE.rotateToPositiveLimit(true);

		int maximumBrightness = Integer.MIN_VALUE;
		int minimumBrightness = Integer.MAX_VALUE;
		int numberOfMeasurements = 0;
		int sum = 0;
		int angle;
		do {
			angle = Motors.MEASURE.getTachoCount();
			int brightness = Sensors.getBrightness();

			maximumBrightness = Math.max(maximumBrightness, brightness);
			minimumBrightness = Math.min(minimumBrightness, brightness);

			Logger.println("angle: %3s,\tbrightness: %3s", angle, brightness);
			sum += brightness;
			numberOfMeasurements++;
		} while (!Motors.MEASURE.hasReachedPositiveLimit(angle));

		int averageBrightness = sum / numberOfMeasurements;

		Logger.println("brightness: minimum: %s, average: %s, maximum: %s", minimumBrightness, averageBrightness, maximumBrightness);

		calibration = new Calibration(averageBrightness, 3, minimumBrightness, maximumBrightness);
	}

	public static Calibration getCalibration() {
		return calibration;
	}

	private Calibration(int triggerBrightness, int angleOffset, int minimumBrightness, int maximumBrightness) {
		this.triggerBrightness = triggerBrightness;
		this.angleOffset = angleOffset;
		this.minimumBrightness = minimumBrightness;
		this.maximumBrightness = maximumBrightness;
	}

	public final int triggerBrightness;
	public final int angleOffset;
	public final int minimumBrightness;
	public final int maximumBrightness;
}
