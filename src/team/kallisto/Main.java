package team.kallisto;

import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.LCD;
import lejos.hardware.lcd.TextLCD;
import lejos.utility.TextMenu;
import team.kallisto.task.CalibrateTask;
import team.kallisto.task.RunTask;
import team.kallisto.task.Task;

import java.util.ArrayList;
import java.util.List;

public class Main {
	static final List<Task> tasks = new ArrayList<>();
	TextMenu menu = createMenu();
	public Main(String[] args) {

	}
	public void main() {
		Task task = selectTask();
		while (task != null) {
			try {
				task.run();
			} catch (Throwable throwable) {
				displayCrashScreen(throwable);
			}
			task = selectTask();
		}

		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		lcd.drawString("Hello World", 4, 4);
		Keys keys = ev3.getKeys();
		keys.waitForAnyPress();
	}

	/**
	 * close all the open sensors stuff
	 */
	private static void close() {
		Sensors.close();
		Motors.close();
	}

	/**
	 * open a menu and ask user for a task, also clears the display
	 * @return null if user wants to exit, otherwise the task to run
	 */
	private Task selectTask() {
		LCD.clear();
		int response = menu.select();
		LCD.clear();

		if (response == -3) // timeout
			throw new IllegalStateException();
		if (response == -2 || response == -1) // quit by other thread, escape
			return null;

		return tasks.get(response);
	}

	/**
	 * create the menu to be used to display the tasks
	 * @return the menu
	 */
	private TextMenu createMenu() {
		String[] list = new String[tasks.size()];
		for (int i = 0; i < list.length; i++) {
			list[i] = tasks.get(i).getName();
		}
		return new TextMenu(list);
	}

	/**
	 * displays the stacktrace on the display of the ev3, wait for user input
	 * @param throwable the throwable to display
	 */
	private void displayCrashScreen(Throwable throwable) {
		LCD.clear();
		printPos = 0;
		printStackTrace(throwable);
		Button.ENTER.waitForPressAndRelease();
		LCD.clear();
	}

	/**
	 * print the stack trace, recursive for caused by
	 * @param throwable the throwable to print
	 */
	private void printStackTrace(Throwable throwable) {
		writeStringToLcd(throwable.toString());

		for (StackTraceElement traceElement: throwable.getStackTrace())
			writeStringToLcd("\tat " + traceElement);

		Throwable causedBy = throwable.getCause();
		if (causedBy != null)
			printStackTrace(causedBy);
	}

	private static int printPos = 0; // used for printing at the correct position

	/**
	 * writes text
	 * @param text the text to print in the next line
	 */
	private void writeStringToLcd(String text) {
		// TODO: improve this, maybe move to utility class
		Font defaultFont = Font.getDefaultFont();
		LCD.drawString(text, printPos, 0);
		printPos += defaultFont.height;
	}

	static {
		tasks.add(new RunTask());
		tasks.add(new CalibrateTask());

		Thread cleanup = new Thread(Main::close);
		Runtime.getRuntime().addShutdownHook(cleanup);
	}
}