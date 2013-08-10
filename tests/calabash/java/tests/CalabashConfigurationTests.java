package calabash.java.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import calabash.java.CalabashConfiguration;
import calabash.java.CalabashException;
import calabash.java.ScreenshotListener;

public class CalabashConfigurationTests {

	private CalabashConfiguration configuration;

	@Before
	public void setup() {
		this.configuration = new CalabashConfiguration();
	}

	@Test(expected = CalabashException.class)
	public void testSettingInvalidScreenshotsDirectory()
			throws CalabashException {
		configuration.setScreenshotsDirectory(new File("/tmpp"));
	}

	@Test(expected = CalabashException.class)
	public void testSettingNotWritableScreenshotsDirectory()
			throws CalabashException {
		configuration.setScreenshotsDirectory(new File("/usr"));
	}

	@Test
	public void testSetScreenshotsDirectory() throws CalabashException {
		File screenshotsDirectory = new File("/tmp");
		configuration.setScreenshotsDirectory(screenshotsDirectory);
		assertEquals(screenshotsDirectory,
				configuration.getScreenshotsDirectory());
	}

	@Test
	public void testGetScreenshotReturnsUserHomeIfNotSet() {
		File screenshotsDirectory = configuration.getScreenshotsDirectory();
		assertEquals(screenshotsDirectory.getAbsolutePath(),
				System.getProperty("user.dir"));
	}

	@Test
	public void testSetDevice() {
		assertNull(configuration.getDevice());
		configuration.setDevice("ipad");
		assertEquals("ipad", configuration.getDevice());
	}

	@Test
	public void testSetAppBundlePath() {
		assertNull(configuration.getAppBundlePath());
		configuration.setAppBundlePath("/tmp");
		assertEquals("/tmp", configuration.getAppBundlePath());
	}

	@Test
	public void testSetScreenshotListener() {
		assertNull(configuration.getScreenshotListener());
		configuration.setScreenshotListener(new ScreenshotListener() {
			@Override
			public void screenshotTaken(String path, String imageType,
					String fileName) {
			}
		});
		assertNotNull(configuration.getScreenshotListener());
	}

	@Test
	public void testSetDeviceEndPoint() throws URISyntaxException {
		assertNull(configuration.getDeviceEndPoint());
		configuration.setDeviceEndPoint(new URI("http://localhost:4059"));
		assertNotNull(configuration.getDeviceEndPoint());
	}

	@Test
	public void testSetLogsDirectory() throws CalabashException {
		File logsDirectory = new File("/tmp");
		configuration.setLogsDirectory(logsDirectory);
		assertTrue(configuration.isLoggingEnabled());
		assertEquals(logsDirectory, configuration.getLogsDirectory());
	}

	@Test(expected = CalabashException.class)
	public void testLogsDirectoryToInvalidDirectory() throws CalabashException {
		configuration.setLogsDirectory(new File("/tmm"));
	}

	@Test(expected = CalabashException.class)
	public void testLogsDirectoryToNotWritableDirectory()
			throws CalabashException {
		configuration.setLogsDirectory(new File("/usr/"));
	}
}
