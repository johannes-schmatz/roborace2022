import java.io.IOException;

@SuppressWarnings("ClassUnconnectedToPackage")
public class HotswapMain {
	public static void main(String[] args) {
		String[] classPaths = {
				"hotswap-agent.jar",
				"/home/root/classes",
				"/home/root/lejos/lib/ev3classes.jar",
				"/home/root/lejos/lib/dbusjava.jar",
				"/home/root/lejos/lib/opencv-2411.jar",
				"/home/root/lejos/libjna/usr/share/java/jna.jar",
				"."
		};
		String classPath = String.join(":", classPaths);

		ProcessBuilder builder = new ProcessBuilder();
		builder.command(
				"/home/root/lejos/ejre-jdk-custom/bin/java",
				"-classpath",
				classPath,
				"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005",
				"lejos.internal.ev3.EV3Wrapper",
				"Main"
		);
		builder.inheritIO();
		try {
			builder.start().waitFor();
		} catch (InterruptedException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
