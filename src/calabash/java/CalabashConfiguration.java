/**
 * 
 */
package calabash.java;

import java.io.File;

/**
 * Configure calabash with various configuration options
 *
 */
public final class CalabashConfiguration {

	private File screenshotsDirectory;
	private String device;
	private String appBundlePath;

	public CalabashConfiguration() {
	}

	public File getScreenshotsDirectory() {
		if (screenshotsDirectory == null)
			return new File(System.getProperty("user.dir"));
		return screenshotsDirectory;
	}

	public void setScreenshotsDirectory(File screenshotsDirectory)
			throws CalabashException {
		if (screenshotsDirectory.isDirectory())
			this.screenshotsDirectory = screenshotsDirectory;
		else
			throw new CalabashException("Invalid screenshots directory");
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getAppBundlePath() {
		return appBundlePath;
	}

	public void setAppBundlePath(String appBundlePath) {
		this.appBundlePath = appBundlePath;
	}

}
