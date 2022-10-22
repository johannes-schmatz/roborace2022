import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;

public class Sensors {
	public static void close() {
		ULTRASONIC_SENSOR.close();
		COLOR_SENSOR.close();
	}

	public static final EV3UltrasonicSensor ULTRASONIC_SENSOR = new EV3UltrasonicSensor(SensorPort.S1);
	public static final EV3ColorSensor COLOR_SENSOR = new EV3ColorSensor(SensorPort.S2);
}
