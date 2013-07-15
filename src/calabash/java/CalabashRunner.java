package calabash.java;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

/**
 * Manages setting up calabash framework and launching the simulator
 * 
 */
public final class CalabashRunner {

	private final File pbxprojFile;
	private final File projectDir;
	private final ScriptingContainer container;
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
		xcodeProjectDir.getName().replace(".xcodeproj", "");
		
		this.container = new ScriptingContainer();
		initializeScriptingContainer();
	}
	
	private final void initializeScriptingContainer() {
		HashMap<String, String> environmentVariables = new HashMap<String, String>();
		environmentVariables.put("PROJECT_DIR", projectDir.getAbsolutePath());
		environmentVariables.put("HOME", System.getProperty("user.home"));
		container.setEnvironment(environmentVariables);
		
		// Load paths points to the gem directory
		container.getLoadPaths().addAll(getLoadPaths());
	}

	private List<String> getLoadPaths() {
		ArrayList<String> loadPaths = new ArrayList<String>();
		File basePath = new File("/Users/navaneeth/.rvm/gems/ruby-1.9.3-p194@tmp/gems");
		File[] gems = basePath.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}
		});
		
		for (File gem : gems) {
			File libPath = new File(gem, "lib");
			loadPaths.add(libPath.getAbsolutePath());
		}
		
		return loadPaths;
	}

	/**
	 * Setup calabash for the current project. This needs to be called only
	 * once.
	 * 
	 * @throws CalabashException
	 */
	public void setupCalabash() throws CalabashException {
		if (this.pbxprojFile == null)
			throw new CalabashException("Project path is not set");
		
		if (isCalabashSetup())
			return;
		
		container.setArgv(new String[]{"setup", projectDir.getAbsolutePath()});
		container.runScriptlet(PathType.ABSOLUTE, "/Users/navaneeth/.rvm/gems/ruby-1.9.3-p194@tmp/gems/calabash-cucumber-0.9.151/bin/calabash-ios");
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
		
		container.clear();
		container.runScriptlet(PathType.ABSOLUTE, "/Users/navaneeth/projects/calabash/calabash-ios-java/scripts/launcher.rb");
		serverVersion = new Http(Config.endPoint()).getServerVersion();
		return new Application();
	}

	public static CalabashServerVersion getServerVersion() {
		return serverVersion;
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
}
