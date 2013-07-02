package calabash.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 *
 */
public final class CalabashRunner {

	private File pbxprojFile;

	/**
	 * Sets the project path
	 * 
	 * @param path
	 * @throws CalabashException
	 */
	public void setProjectPath(String path) throws CalabashException {
		File projectPath = new File(path);

		if (!projectPath.exists())
			throw new CalabashException(String.format("'%s' doesn't exists",
					path));

		if (!projectPath.isDirectory())
			throw new CalabashException(String.format(
					"'%s' is not a directory", path));

		final String xCodeProjectFile = "project.pbxproj";
		File projectFile = new File(projectPath, xCodeProjectFile);
		if (!projectFile.exists())
			throw new CalabashException(
					String.format(
							"'%s' is not a Xcode project directory. Unable to locate '%s'",
							path, xCodeProjectFile));

		this.pbxprojFile = projectFile;
	}

	public void setupCalabash() throws CalabashException {
		if (this.pbxprojFile == null)
			throw new CalabashException("Project path is not set");

		if (isCalabashSetup()) {
			System.out.println("Already setup");
			return;
		}

		String projectName = findProjectName();
		ensureXCodeIsNotRunning();
		backupProjectFile();
		extractCalabashFramework();
		injectCalabashFramework(projectName);
	}

	private String findProjectName() throws CalabashException {
		String name = pbxprojFile.getParentFile().getName();
		if (name != null && !name.isEmpty())
			return name.replace(".xcodeproj", "");

		throw new CalabashException(String.format(
				"Can't find project name from '%s'", pbxprojFile
						.getParentFile().getAbsolutePath()));
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
		String parentDir = pbxprojFile.getParentFile().getParentFile()
				.getAbsolutePath();
		// TODO: remove full path and read from bundle
		String[] cmd = {
				"unzip",
				"/Users/navaneeth/projects/calabash/calabash-ios-java/deps/calabash.framework.zip",
				"-d", parentDir };
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

	private void injectCalabashFramework(String projectName)
			throws CalabashException {
		String xcodeprojDir = pbxprojFile.getParentFile().getAbsolutePath();
		String[] cmd = {
				"/Users/navaneeth/projects/calabash/calabash-ios-java/deps/CalabashSetup",
				xcodeprojDir, projectName };

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

}
