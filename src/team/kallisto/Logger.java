package team.kallisto;

import lejos.internal.ev3.EV3Wrapper;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.lang.reflect.Field;

/**
 * Use {@link System#out} and {@link System#err} for output, this class provides two methods for more efficient output,
 * with added timestamps.
 */
public class Logger {
	private static final String FIFO = "/home/root/log.txt";
	private static final boolean enabled = true;
	@Nullable
	private static FileWriter writer = null;
	private static final PrintStream INSTANCE = new PrintStream(new LoggerOutputStream(), true);
	/**
	 * The value of {@link System#out}. This is set by {@link EV3Wrapper} at some point.
	 */
	public static final PrintStream out;
	/**
	 * The value of {@link System#err}. This is set by {@link EV3Wrapper} at some point.
	 */
	public static final PrintStream err;
	/**
	 * The value of {@link EV3Wrapper#origErr}. This is the value {@link EV3Wrapper} gets from {@link System#err}.
	 */
	public static final PrintStream origErr;

	static {
		out = System.out;
		err = System.err;
		Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));

		// this removes the need to have a try catch around it, as the EV3Wrapper calls the Throwable#printStackTrace
		// on "origErr", so if we overwrite that to our stream, we print it to the file.
		try {
			Field field = EV3Wrapper.class.getDeclaredField("origErr");
			field.setAccessible(true);
			origErr = (PrintStream) field.get(null);
			field.set(null, INSTANCE);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void init() {
		if (!enabled) return;
		launchFifoThread();
		System.setOut(INSTANCE);
		System.setErr(INSTANCE);
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static void launchFifoThread() {
		Thread thread = new Thread(Logger::fifoThread);
		thread.setDaemon(true);
		thread.start();
	}

	private static void fifoThread() {
		if (writer == null) {
			try {
				writer = new FileWriter(FIFO, true);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void close() {
		if (!enabled || writer == null) return;
		try {
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Print something to the log file. This adds the {@link System#currentTimeMillis()} and caller class to the output.
	 * @param line The line to print.
	 */
	public static void println(String line) {
		if (!enabled || writer == null) return;
		System.out.println(System.currentTimeMillis() + " " + getCallerCaller() + " " + line);
	}

	/**
	 * Formats some string to the output. At the start of it, there is the output of
	 * {@link System#currentTimeMillis()} and the caller class name.
	 * @param format The format to print int
	 * @param args The arguments to {@link PrintStream#format(String, Object...)}.
	 */
	@SuppressWarnings("OverloadedVarargsMethod")
	public static void println(String format, Object... args) {
		if (!enabled || writer == null) return;
		System.out.print(System.currentTimeMillis() + " " + getCallerCaller() + " ");
		System.out.format(format, args);
		System.out.println();
	}

	private static String getCallerCaller() {
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		int n = 3;
		if (trace.length <= n) return "<none>";
		String fullName = trace[n].getClassName();
		int i = fullName.lastIndexOf('.');
		return fullName.substring(i + 1);
	}

	private static void handleIOException(IOException e) {
		if ("java.io.IOException: Broken pipe".equals(e.toString())) {
			writer = null;
			launchFifoThread();
		} else {
			throw new RuntimeException(e);
		}
	}

	private static class LoggerOutputStream extends OutputStream {
		@Override
		public void write(int b) {
			if (writer != null) {
				try {
					writer.write(b);
				} catch (IOException e) {
					handleIOException(e);
				}
			}
		}

		@Override
		public void flush() {
			if (writer != null) {
				try {
					writer.flush();
				} catch (IOException e) {
					handleIOException(e);
				}
			}
		}

		@Override
		public void close() {
			flush();
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
