package team.kallisto;

import org.jetbrains.annotations.Nullable;

import java.io.*;

public class Logger {
	private static final String FIFO = "/home/root/log.txt";
	private static final boolean enabled = true;
	@Nullable
	private static FileWriter writer = null;
	private static final PrintStream INSTANCE = new PrintStream(new LoggerOutputStream(), true);
	public static final PrintStream out;
	public static final PrintStream err;

	static {
		out = System.out;
		err = System.err;
		Runtime.getRuntime().addShutdownHook(new Thread(Logger::close));
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

	public static void println(String line) {
		if (!enabled || writer == null) return;
		System.out.println(System.currentTimeMillis() + " " + line);
	}

	public static void println(String format, Object... args) {
		if (!enabled || writer == null) return;
		System.out.format(System.currentTimeMillis() + " " + format + "%n", args);
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
