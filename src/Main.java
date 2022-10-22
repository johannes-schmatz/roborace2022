import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello world!");

		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		lcd.drawString("Hello World", 4, 4);
		keys.waitForAnyPress();


		Sensors.close();
		Motors.close();
	}
}