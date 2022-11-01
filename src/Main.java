public class Main {
	public static void main(String[] args) {
		try {
			new team.kallisto.Main(args).main();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			throw throwable;
		}
	}
}
