package team.kallisto.task;

public interface Task {
	/**
	 * runs the task
	 */
	void run();

	/**
	 * the name of the task
	 * @return the name of the task
	 */
	String getName();
}
