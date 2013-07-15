/**
 * 
 */
package calabash.java;

/**
 * 
 *
 */
final class Config {

	private static final String defaultEndPoint = "http://localhost:37265/";

	/**
	 * Gets the remote endpoint where commands will be send
	 * 
	 * @return
	 */
	public static String endPoint() {
		String value = getEnv("DEVICE_ENDPOINT");
		if (value == null)
			return defaultEndPoint;

		return value;
	}

	/**
	 * Gets the device where application is running. This can be changed by
	 * setting DEVICE env variable. This returns "iphone" if no value is set for
	 * DEVICE env variable
	 * 
	 * @return
	 */
	public static String getDevice() {
		String value = getEnv("DEVICE");
		if (value == null)
			return "iphone";

		return null;
	}

	/**
	 * Returns the OS value set on the env variable
	 * 
	 * @return
	 */
	public static String getOS() {
		return getEnv("OS");
	}

	public static int pauseMs() {
		final int defaultValue = 1000;
		String pause = getEnv("PAUSE_MS");
		if (pause != null) {
			try {
				return Integer.parseInt(pause);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}

		return defaultValue;
	}

	private static String getEnv(String name) {
		try {
			String value = System.getenv(name);
			return value;
		} catch (SecurityException e) {
			return null;
		}
	}

}
