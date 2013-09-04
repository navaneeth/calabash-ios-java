package calabash.java;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import xmlwise.Plist;

/**
 * Manages setting up calabash framework and launching the simulator
 * 
 */
public class CalabashRunner {

	private final File pbxprojFile;
	private final File projectDir;
	private final CalabashWrapper calabashWrapper;

	/**
	 * Initializes CalabashRunner
	 * 
	 * @param path
	 *            full path to the project
	 * @throws CalabashException
	 */
	public CalabashRunner(String path) throws CalabashException {
		this(path, null);
	}

	/**
	 * Initializes CalabashRunner
	 * 
	 * @param path
	 *            full path to the project
	 * @throws CalabashException
	 */
	public CalabashRunner(String path, CalabashConfiguration configuration)
			throws CalabashException {
		File projectPath = new File(path);
		if (!projectPath.exists())
			throw new CalabashException(String.format("'%s' doesn't exists",
					path));
		if (!projectPath.isDirectory())
			throw new CalabashException(String.format(
					"'%s' is not a directory", path));

		// Finding the xcode project directory and the pbxproj file
		File[] dirs = projectPath.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() && f.getName().contains(".xcodeproj");
			}

		});
		if (dirs == null || dirs.length == 0)
			throw new CalabashException(
					String.format(
							"'%s' is not a valid project path. Can't find .xcodeproj directory",
							path));
		File xcodeProjectDir = dirs[0];

		// Ensuring pbxproj file exists
		File projectFile = new File(xcodeProjectDir, "project.pbxproj");
		if (!projectFile.exists())
			throw new CalabashException(
					String.format(
							"'%s' is not a Xcode project directory. Unable to locate 'project.pbxproj'",
							path));

		this.projectDir = projectPath;
		this.pbxprojFile = projectFile;
		xcodeProjectDir.getName().replace(".xcodeproj", "");

		File gemPath = extractGemsFromBundle();
		calabashWrapper = new CalabashWrapper(gemPath, projectDir,
				configuration);

		CalabashLogger.initialize(configuration);
	}

	/**
	 * Gets the targets available in the XCode project
	 * 
	 * @return Collection of target names
	 * @throws CalabashException
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> getTargets() throws CalabashException {
		ArrayList<String> targets = new ArrayList<String>();

		// Converting pbxproj file to XML so that we can make sense of it
		File pbxProjXML = convertPbxProjectFileToXML();
		Map<String, Object> pbxProjMap = null;
		try {
			pbxProjMap = Plist.load(pbxProjXML);
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to parse the plist content: %s", pbxProjXML), e);
		}

		if (pbxProjMap.get("objects") == null
				|| !(pbxProjMap.get("objects") instanceof Map)) {
			return targets;
		}

		// rootObject defines all the targets which will be an array of target
		// IDs
		// We pick each target ID and look that up in objects map to get the
		// target name

		Map<String, Object> objects = (Map<String, Object>) pbxProjMap
				.get("objects");
		String rootObjectId = (String) pbxProjMap.get("rootObject");
		if (rootObjectId == null) {
			return targets;
		}

		Map<String, Object> rootObject = (Map<String, Object>) objects
				.get(rootObjectId);
		Object object = rootObject.get("targets");
		if (object == null || !(object instanceof ArrayList<?>)) {
			return targets;
		}

		ArrayList<?> targetIds = (ArrayList<?>) object;
		for (Object targetId : targetIds) {
			if (objects.get(targetId) != null
					&& objects.get(targetId) instanceof Map<?, ?>) {
				Map<String, Object> target = (Map<String, Object>) objects
						.get(targetId);
				if (target.get("name") != null) {
					targets.add(target.get("name").toString());
				}
			}
		}

		return targets;
	}

	private File convertPbxProjectFileToXML() throws CalabashException {
		File xmlFile = null;
		try {
			xmlFile = File.createTempFile("calabash-ios", "temp");
		} catch (IOException e) {
			throw new CalabashException("Failed to make a temporay file", e);
		}
		String[] cmd = { "plutil", "-convert", "xml1", "-o",
				xmlFile.getAbsolutePath(), pbxprojFile.getAbsolutePath() };
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				String message = "";
				InputStream errorStream = null;
				try {
					errorStream = process.getErrorStream();
					message = Utils.toString(errorStream);
				} finally {
					if (errorStream != null)
						errorStream.close();
				}
				throw new CalabashException(String.format(
						"Failed to convert pbxproj file: %s. %s",
						pbxprojFile.getAbsolutePath(), message));
			}
		} catch (IOException e) {
			throw new CalabashException(String.format(
					"Failed to convert pbxproj file: %s. %s",
					pbxprojFile.getAbsolutePath(), e.getMessage()), e);
		} catch (InterruptedException e) {
			throw new CalabashException(String.format(
					"Failed to convert pbxproj file: %s. %s",
					pbxprojFile.getAbsolutePath(), e.getMessage()), e);
		}

		return xmlFile;
	}

	private File extractGemsFromBundle() throws CalabashException {
		File dir = getGemsExtractionDir();
		File extracted = new File(dir, "extracted");
		if (extracted.exists()) {
			// Already extracted
			return dir;
		}

		copyFileFromBundleTo("scripts", "launcher.rb", dir);
		copyFileFromBundleTo("scripts", "gems.zip", dir);
		try {
			File gemszip = new File(dir, "gems.zip");
			Utils.unzip(gemszip, dir);
			gemszip.delete();
			extracted.createNewFile();
		} catch (Exception e) {
			throw new CalabashException("Failed to unzip gems", e);
		}

		return dir;
	}

	private void copyFileFromBundleTo(String sourceDir, String fileName,
			File outDir) throws CalabashException {
		final ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		InputStream stream = classLoader.getResourceAsStream(sourceDir + "/"
				+ fileName);
		if (stream == null)
			throw new CalabashException(
					String.format(
							"Can't copy %s from the bundle. Make sure you are using the correct JAR file",
							fileName), null);

		try {
			File file = new File(outDir, fileName);
			file.createNewFile();
			FileOutputStream outFile = new FileOutputStream(file);
			byte[] buffer = new byte[10240];
			int len;
			while ((len = stream.read(buffer)) != -1) {
				outFile.write(buffer, 0, len);
			}
			outFile.close();
		} catch (IOException e) {
			throw new CalabashException(
					String.format(
							"Can't copy %s from the bundle to %s. Failed to create destination file",
							fileName, outDir.getAbsolutePath()), e);
		}
	}

	private File getGemsExtractionDir() throws CalabashException {
		try {
			File tempFile = File.createTempFile("foo", "bar");
			tempFile.delete();

			File gemsDir = new File(tempFile.getParentFile(),
					"calabash-ios-gems-" + getCurrentVersion());
			if (!gemsDir.exists()) {
				boolean created = gemsDir.mkdir();
				if (!created)
					throw new CalabashException(
							"Can't create gems extraction directory. "
									+ gemsDir.getAbsolutePath());
			}

			if (!gemsDir.isDirectory())
				throw new CalabashException(String.format(
						"Gems directory is invalid. %s is not a directory",
						gemsDir.getAbsolutePath()));

			return gemsDir;
		} catch (IOException e) {
			throw new CalabashException(
					"Can't create gems extraction directory.", e);
		}
	}

	public void setup() throws CalabashException {
		setup(null);
	}

	/**
	 * Setup calabash for the current project. This needs to be called only
	 * once.
	 * 
	 * @param targetToDuplicate
	 *            XCode target which will be duplicated to embed calabash
	 *            framework
	 * @throws CalabashException
	 */
	public void setup(String targetToDuplicate) throws CalabashException {
		if (this.pbxprojFile == null)
			throw new CalabashException("Project path is not set");

		if (isCalabashSetup())
			return;

		calabashWrapper.setup(targetToDuplicate);
	}

	/**
	 * Starts the test environment. This launches the simulator and starts the
	 * process in the simulator
	 * 
	 * @return
	 * @throws CalabashException
	 */
	public IOSApplication start() throws CalabashException {
		if (!isCalabashSetup()) {
			String message = String.format("Calabash is not setup for %s",
					this.pbxprojFile.getAbsolutePath());
			CalabashLogger.error(message);
			throw new CalabashException(message);
		}

		calabashWrapper.start();
		return launchApplication();
	}

	/**
	 * Creates an iOS application. This can be overridden if custom Application
	 * instances needs to be created
	 * 
	 * @return
	 */
	protected IOSApplication launchApplication() {
		return new IOSApplication(calabashWrapper);
	}

	/**
	 * Gets the temporary directory used for keeping the gems required to run
	 * calabash ruby client
	 * 
	 * @return Absolute path to the gems directory
	 */
	public String getGemsDir() {
		return calabashWrapper.getGemsDir();
	}

	private boolean isCalabashSetup() throws CalabashException {
		String[] cmd = { "grep", "calabash.framework",
				pbxprojFile.getAbsolutePath() };
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			int exitCode = process.waitFor();
			if (exitCode == 0)
				return true;
		} catch (IOException e) {
			throw new CalabashException("Error checking calabash setup status",
					e);
		} catch (InterruptedException e) {
			throw new CalabashException("Error checking calabash setup status",
					e);
		}

		return false;
	}

	private String getCurrentVersion() {
		try {
			Enumeration<URL> resources = getClass().getClassLoader()
					.getResources("META-INF/MANIFEST.MF");
			while (resources.hasMoreElements()) {
				try {
					Manifest manifest = new Manifest(resources.nextElement()
							.openStream());
					Attributes mainAttributes = manifest.getMainAttributes();
					if (mainAttributes != null
							&& "calabash-ios-java".equals(mainAttributes
									.getValue("Project-Name"))) {
						String value = mainAttributes
								.getValue("Implementation-Version");
						if (value != null)
							return value;
					}
				} catch (Exception E) {
					// ignore
				}
			}
		} catch (Exception e) {
			// ignore
		}

		return "";
	}

	/**
	 * Gets the underlying wrapper
	 * 
	 * @return the calabashWrapper
	 */
	protected CalabashWrapper getCalabashWrapper() {
		return calabashWrapper;
	}
}
