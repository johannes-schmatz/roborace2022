package team.kallisto;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;
import lejos.utility.TextMenu;
import org.jetbrains.annotations.Nullable;
import team.kallisto.calibration.Calibration;
import team.kallisto.task.CalibrateTask;
import team.kallisto.task.RunTask;
import team.kallisto.task.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
	static final List<Task> tasks = new ArrayList<>();
	private final TextMenu menu = createMenu();
	public Main(String[] args) {
		Logger.init();
		Sensors.init();
		Logger.println("starting up, args were" + Arrays.toString(args));
	}
	public void run() {
		Task task = selectTask();
		while (task != null) { // task == null means it's quit

			// don't catch it here, because leJOS displays a nice screen
			task.run();

			// move to the middle again
			Motors.rotateTo0AndFltAndReset();

			// to prevent exiting the menu too
			Delay.msDelay(100);

			task = selectTask();
		}

		Logger.println("stopping it all");
	}

	/**
	 * open a menu and ask user for a task, also clears the display
	 * @return null if user wants to exit, otherwise the task to run
	 */
	private @Nullable Task selectTask() {
		while (Button.ESCAPE.isDown()) {
			Sound.beep();
		}
		Delay.msDelay(100);
		LCD.clear();
		Sound.buzz();
		int response = menu.select();
		LCD.clear();

		if (response == -3) // timeout
			throw new IllegalStateException("timeout should not be possible");
		if (response == -2 || response == -1) // quit by other thread, escape
			return null;

		Task task = tasks.get(response);
		Logger.println("starting task: %s", task.getName());
		return task;
	}

	/**
	 * create the menu to be used to display the tasks
	 * @return the menu
	 */
	private static TextMenu createMenu() {
		String[] list = new String[tasks.size()];
		for (int i = 0; i < list.length; i++) {
			list[i] = tasks.get(i).getName();
		}
		return new TextMenu(list);
	}

	static {
		tasks.add(new RunTask());
		tasks.add(new CalibrateTask(false));
		tasks.add(new CalibrateTask(true));
		tasks.add(new Calibration.DefaultCalibrationSetter());
		tasks.add(new Motors.ResetTachoCountTask());
	}
}