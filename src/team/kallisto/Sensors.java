package team.kallisto;

import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;

public class Sensors {
	private static void close() {
		ULTRASONIC_SENSOR.close();
		COLOR_SENSOR.close();
	}

	private static final CustomUltrasonicSensor ULTRASONIC_SENSOR = new CustomUltrasonicSensor(SensorPort.S1);
	private static final CustomColorSensor COLOR_SENSOR = new CustomColorSensor(SensorPort.S2);

	public static void init() {
		ULTRASONIC_SENSOR.enable();
		COLOR_SENSOR.init();
	}

	/**
	 * gets the current brightness from the sensor
	 * @return brightness, range 0..=100
	 */
	public static int getBrightness() {
		return COLOR_SENSOR.getBrightness();
	}

	/**
	 * gets the current distance from the sensor
	 * @return distance, in mm, or {@code Integer.MAX_VALUE} if it's infinity
	 */
	public static int getDistance() {
		return ULTRASONIC_SENSOR.getDistance();
	}

	private static class CustomColorSensor extends EV3ColorSensor {
		public CustomColorSensor(Port port) {
			super(port);
		}

		private void init() {
			// we get some sensor data here, this initialises the sensor
			SensorMode mode = getRedMode();
			float[] sample = new float[mode.sampleSize()];
			mode.fetchSample(sample, 0);
		}

		/**
		 * @return the brightness in 0..=100
		 */
		private int getBrightness() {
			if (currentMode != COL_REFLECT)
				throw new IllegalStateException("mode is not COL_REFLECT (0)");

			return port.getByte() & 0xff;
		}
	}

	private static class CustomUltrasonicSensor extends EV3UltrasonicSensor {
		public CustomUltrasonicSensor(Port port) {
			super(port);
		}

		/**
		 * @return the distance in mm, {@code Integer.MAX_VALUE} means infinity
		 */
		private int getDistance() {
			if (currentMode != 0)
				throw new IllegalStateException("mode is not DISTANCE_MODE (0)");

			int raw = port.getShort(); // in mm
			return (raw == 2550) ? Integer.MAX_VALUE : raw;
		}
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(Sensors::close));
	}
}
