/**
 * 
 */
package calabash.java;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jruby.RubyArray;
import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

/**
 * 
 *
 */
public final class CalabashWrapper {

	private final ScriptingContainer container = new ScriptingContainer();
	private final File rbScriptsDir;
	private final File projectDir;
	private final File gemsDir;
	private final File binDir;

	public CalabashWrapper(File rbScriptsDir, File projectDir)
			throws CalabashException {
		if (!rbScriptsDir.isDirectory())
			throw new CalabashException("Invalid ruby scripts directory");
		if (!projectDir.isDirectory())
			throw new CalabashException("Invalid project directory");

		this.rbScriptsDir = rbScriptsDir;
		this.gemsDir = new File(rbScriptsDir, "gems");
		this.binDir = new File(rbScriptsDir, "bin");
		this.projectDir = projectDir;
		this.initializeScriptingContainer();
	}

	public void setup() throws CalabashException {
		try {
			container.setArgv(new String[] { "setup",
					projectDir.getAbsolutePath() });
			container.runScriptlet(PathType.ABSOLUTE, new File(
					getCalabashGemDirectory(), "bin/calabash-ios").getAbsolutePath());
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to setup calabash. %s", e.getMessage()));
		}
	}

	public void start() throws CalabashException {
		try {
			container.clear();
			String launcherScript = new File(rbScriptsDir,
					"launcher.rb").getAbsolutePath();
			container.runScriptlet(PathType.ABSOLUTE, launcherScript);
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to start simulator. %s", e.getMessage()), e);
		}
	}
	
	public RubyArray query(String query, String... args) throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			
			container.put("cjQueryString", query);
			container.put("cjQueryArgs", args);
			
			RubyArray queryResults = null;
			if (args != null && args.length > 0)
				queryResults = (RubyArray) container.runScriptlet("query(cjQueryString, cjQueryArgs)");
			else
				queryResults = (RubyArray) container.runScriptlet("puts cjQueryString\nquery(cjQueryString)");
			
			return queryResults;
		}
		catch (Exception e) {
			throw new CalabashException(String.format("Failed to execute '%s'. %s", query, e.getMessage()), e);
		}
	}
	
	private void addRequiresAndIncludes(String... modules) {
		StringBuilder script = new StringBuilder("require 'calabash-cucumber'\n");
		for (String module : modules) {
			script.append("include " + module);
			script.append("\n");
		}
		
		container.runScriptlet(script.toString());
	}

	private final void initializeScriptingContainer() throws CalabashException {
		HashMap<String, String> environmentVariables = new HashMap<String, String>();
		environmentVariables.put("PROJECT_DIR", projectDir.getAbsolutePath());
		environmentVariables.put("HOME", System.getProperty("user.home"));
		container.setEnvironment(environmentVariables);

		// Load paths points to the gem directory
		container.getLoadPaths().addAll(getLoadPaths());
	}

	private List<String> getLoadPaths() throws CalabashException {
		ArrayList<String> loadPaths = new ArrayList<String>();
		File[] gems = gemsDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File arg0) {
				return arg0.isDirectory();
			}
		});
		
		if (gems == null || gems.length == 0)
			throw new CalabashException("Couldn't find any gems inside " + gemsDir.getAbsolutePath());

		for (File gem : gems) {
			File libPath = new File(gem, "lib");
			loadPaths.add(libPath.getAbsolutePath());
		}

		return loadPaths;
	}

	private File getCalabashGemDirectory() throws CalabashException {
		File[] calabashGemPath = gemsDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						&& pathname.getName().startsWith("calabash-cucumber");
			}
		});

		if (calabashGemPath.length == 0)
			throw new CalabashException(String.format(
					"Error finding 'calabash-cucumber' in the gempath : %s",
					gemsDir.getAbsolutePath()));

		if (calabashGemPath.length > 1)
			throw new CalabashException(
					String.format(
							"Multiple matches for 'calabash-cucumber' in the gempath : %s",
							gemsDir.getAbsolutePath()));

		return calabashGemPath[0];
	}

}
