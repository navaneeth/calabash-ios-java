package calabash.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import xmlwise.Plist;

/**
 * Manages setting up calabash framework and launching the simulator
 * 
 */
public final class CalabashRunner {

	private final File pbxprojFile;
	private final File projectDir;
	private final File xcodeProjectDir;
	private final String projectName;
	private static CalabashServerVersion serverVersion;

	/**
	 * Initializes CalabashRunner
	 * 
	 * @param path
	 *            full path to the project
	 * @throws CalabashException
	 */
	public CalabashRunner(String path) throws CalabashException {
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
		this.xcodeProjectDir = xcodeProjectDir;
		this.projectName = xcodeProjectDir.getName().replace(".xcodeproj", "");
	}

	/**
	 * Setup calabash for the current project. This needs to be called only
	 * once. If project is already setup, this function is a no-op
	 * 
	 * @throws CalabashException
	 */
	public void setupCalabash() throws CalabashException {
		if (this.pbxprojFile == null)
			throw new CalabashException("Project path is not set");

		if (isCalabashSetup()) {
			return;
		}

		ensureXCodeIsNotRunning();
		backupProjectFile();
		extractCalabashFramework();
		injectCalabashFramework();
	}

	/**
	 * Starts the test environment. This launches the simulator and starts the
	 * process in the simulator
	 * 
	 * @return
	 * @throws CalabashException
	 */
	public Application start() throws CalabashException {
		if (!isCalabashSetup())
			setupCalabash();

		String appPath = null;
		try {
			appPath = findAppBundlePath();
		} catch (AutoDetectAppBundlePathException e) {
			throw new CalabashException(
					String.format(
							"Can't find the application bundle path. Please build '%s-cal' target from Xcode.\nIf your Xcode build points to non-standard location, set APP_BUNDLE_PATH environment variable to the application path",
							projectName));
		}

		// Checking if application is already running. If yes, kill it before we
		// launch again
		// This is required because Calabash server need to be restarted after
		// each run
		Application existing = new Application();
		if (existing.isRunning())
			existing.exit();

		final String[] cmd = {
				"/Users/navaneeth/projects/calabash/calabash-ios-java/deps/ios-sim",
				"launch", appPath, "--sdk", "6.1", "--family", "iphone",
				"--exit" };
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			throw new CalabashException(String.format(
					"Unable to launch simulator. %s", e.getMessage()), e);
		}

		ensureConnectivity();
		serverVersion = new Http(Config.endPoint()).getServerVersion();

		return new Application();
	}

	public static CalabashServerVersion getServerVersion() {
		return serverVersion;
	}

	private void ensureConnectivity() throws CalabashException {
		final int MAXIMUM_RETRIES = 30;
		int tries = 0;
		boolean connected = false;

		Http http = new Http(Config.endPoint());
		while (!connected) {
			if (++tries == MAXIMUM_RETRIES)
				throw new CalabashException(
						String.format(
								"Can't establish connection to '%s'.\nMake sure you don't have a firewall blocking the traffic",
								Config.endPoint()));

			try {
				connected = http.tryPing();
			} catch (Exception ex) {
				// We don't care this
			} finally {
				if (!connected)
					sleepQuietly(1000);
			}
		}
	}

	private void sleepQuietly(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	private void ensureXCodeIsNotRunning() throws CalabashException {
		String[] cmd = { "sh", "-c",
				"ps x -o command | grep 'Contents/MacOS/Xcode' | grep -v grep" };

		try {
			Process process = Runtime.getRuntime().exec(cmd);
			int exitcode = process.waitFor();
			if (exitcode == 0)
				// Above command exits with 0 when there is a match non zero
				// where there is no match
				throw new CalabashException(
						"Xcode is running. Please close Xcode before doing the setup");
		} catch (IOException e) {
			throw new CalabashException("Error checking Xcode status", e);
		} catch (InterruptedException e) {
			throw new CalabashException("Error checking Xcode status", e);
		}
	}

	private void backupProjectFile() throws CalabashException {
		File backupfile = new File(pbxprojFile.getAbsolutePath() + "."
				+ System.currentTimeMillis() + ".bak");
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			backupfile.createNewFile();
			in = new FileInputStream(pbxprojFile);
			out = new FileOutputStream(backupfile);
			int c;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			throw new CalabashException("Can't make a backup of project file",
					e);
		}
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

	private void extractCalabashFramework() throws CalabashException {
		// TODO: remove full path and read from bundle
		String[] cmd = {
				"unzip",
				"/Users/navaneeth/projects/calabash/calabash-ios-java/deps/calabash.framework.zip",
				"-d", projectDir.getAbsolutePath() };
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			int exitcode = process.waitFor();

			if (exitcode != 0) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(process.getErrorStream()));
				String line = null;
				StringBuilder builder = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				reader.close();
				throw new CalabashException(
						String.format(
								"Error extracting calabash framework. %s. Process returned %d",
								builder.toString(), exitcode));
			}
		} catch (IOException e) {
			throw new CalabashException("Error extracting calabash framework",
					e);
		} catch (InterruptedException e) {
			throw new CalabashException("Error extracting calabash framework",
					e);
		}
	}

	private void injectCalabashFramework() throws CalabashException {
		String[] cmd = {
				"/Users/navaneeth/projects/calabash/calabash-ios-java/deps/CalabashSetup",
				xcodeProjectDir.getAbsolutePath(), projectName };

		try {
			Process process = Runtime.getRuntime().exec(cmd);
			int exitcode = process.waitFor();
			if (exitcode != 0) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(process.getErrorStream()));
				String line = null;
				StringBuilder builder = new StringBuilder();
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				reader.close();
				throw new CalabashException(
						String.format(
								"Error injecting calabash framework. %s. Process returned %d",
								builder.toString(), exitcode));
			}
		} catch (IOException e) {
			throw new CalabashException("Error injecting calabash framework", e);
		} catch (InterruptedException e) {
			throw new CalabashException("Error injecting calabash framework", e);
		}
	}

	// Tries to detect the app bundle path. Usually Xcode builds to
	// ~Library/Developer/Xcode/DerivedData
	// Throws exception when fails
	private String findAppBundlePath() throws AutoDetectAppBundlePathException {
		File xcodeBuildDir = new File(
				new File(System.getProperty("user.home")),
				"Library/Developer/Xcode/DerivedData");
		Collection<File> plistFiles = getDirectoryContentsRecursive(
				xcodeBuildDir, new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.contains("info.plist");
					}
				});

		File buildDir = null;
		for (File file : plistFiles) {
			if (file.isDirectory())
				continue;

			try {
				Map<String, Object> map = Plist.load(file);
				Object workspacePath = map.get("WorkspacePath");
				if (workspacePath != null
						&& workspacePath.toString().contains(
								projectDir.getAbsolutePath())) {
					buildDir = file.getParentFile();
					break;
				}
			} catch (Exception e) {
			}
		}

		if (buildDir == null)
			throw new AutoDetectAppBundlePathException();

		ArrayList<File> allFilesInBuildDir = getDirectoryContentsRecursive(
				buildDir, new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.equals(String.format("%s-cal.app",
								projectName));
					}
				});

		if (allFilesInBuildDir.size() != 1)
			throw new AutoDetectAppBundlePathException();

		File candidate = allFilesInBuildDir.get(0);
		if (!candidate.isDirectory())
			throw new AutoDetectAppBundlePathException();

		return candidate.getAbsolutePath();
	}

	private static ArrayList<File> getDirectoryContentsRecursive(File dir,
			FilenameFilter filter) {
		ArrayList<File> files = new ArrayList<File>();
		if (!dir.isDirectory())
			return files;

		File[] allFiles = dir.listFiles();
		for (File file : allFiles) {
			if (filter != null) {
				boolean shouldAccept = filter.accept(dir, file.getName());
				if (shouldAccept)
					files.add(file);
			} else
				files.add(file);

			if (file.isDirectory()) {
				files.addAll(getDirectoryContentsRecursive(file, filter));
			}
		}

		return files;
	}

	private class AutoDetectAppBundlePathException extends Exception {
		private static final long serialVersionUID = 4983508335141990708L;
	}

}
