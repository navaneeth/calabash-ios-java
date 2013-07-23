/**
 * 
 */
package calabash.java;

import java.io.File;
import java.net.URI;

/**
 * Configure calabash with various configuration options
 * 
 */
public final class CalabashConfiguration {

	private File screenshotsDirectory;
	private String device;
	private String appBundlePath;
	private ScreenshotListener listener;
	private URI deviceEndPoint;

	/**
	 * Gets the screenshots directory. If not set, this returns the current
	 * working directory
	 * 
	 * @return
	 */
	public File getScreenshotsDirectory() {
		if (screenshotsDirectory == null)
			return new File(System.getProperty("user.dir"));
		return screenshotsDirectory;
	}

	/**
	 * Sets the screenshot directory. Screenshots will be written to this
	 * directory
	 * 
	 * @param screenshotsDirectory
	 *            Directory
	 * @throws CalabashException
	 */
	public void setScreenshotsDirectory(File screenshotsDirectory)
			throws CalabashException {
		if (screenshotsDirectory.isDirectory())
			this.screenshotsDirectory = screenshotsDirectory;
		else
			throw new CalabashException("Invalid screenshots directory");
	}

	/**
	 * Gets the device if set
	 * 
	 * @return
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * Sets the device which has to be used when running tests
	 * 
	 * @param device
	 *            Valid values are ios, ipad, iphone
	 */
	public void setDevice(String device) {
		this.device = device;
	}

	/**
	 * Gets the APP bundle path
	 * 
	 * @return path if set, null otherwise
	 */
	public String getAppBundlePath() {
		return appBundlePath;
	}

	/**
	 * Sets the APP bundle path. This is required only when calabash fails to
	 * auto detect the app bundle path.
	 * 
	 * @param appBundlePath
	 *            Path to the bundle
	 */
	public void setAppBundlePath(String appBundlePath) {
		this.appBundlePath = appBundlePath;
	}

	/**
	 * Sets the screenshot listener. Listener will be invoked whenever calabash
	 * takes a screenshot.
	 * 
	 * @param listener
	 *            ScreenshotListener instance
	 */
	public void setScreenshotListener(ScreenshotListener listener) {
		this.listener = listener;
	}

	/**
	 * Gets the current screenshot listener
	 * 
	 * @return ScreenshotListener if set, null otherwise
	 */
	public ScreenshotListener getScreenshotListener() {
		return listener;
	}

	/**
	 * Gets the device endpoint.
	 * 
	 * @return URI if set, null otherwise
	 */
	public URI getDeviceEndPoint() {
		return deviceEndPoint;
	}

	/**
	 * Sets the device IP address. This is required only when the device that
	 * needs to run the test is not in localhost. By default calabash uses
	 * http://localhost:37265
	 * 
	 * @param deviceEndPoint
	 *            A valid device endpoint
	 */
	public void setDeviceEndPoint(URI deviceEndPoint) {
		this.deviceEndPoint = deviceEndPoint;
	}
}
