/**
 * 
 */
package calabash.java;

import java.io.File;
import java.io.FileFilter;
import java.io.StringWriter;
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

	public CalabashWrapper(File rbScriptsDir, File projectDir)
			throws CalabashException {
		if (!rbScriptsDir.isDirectory())
			throw new CalabashException("Invalid ruby scripts directory");
		if (!projectDir.isDirectory())
			throw new CalabashException("Invalid project directory");

		this.rbScriptsDir = rbScriptsDir;
		this.gemsDir = new File(rbScriptsDir, "gems");
		this.projectDir = projectDir;
		this.initializeScriptingContainer();
	}

	public void setup() throws CalabashException {
		try {
			container.setArgv(new String[] { "setup",
					projectDir.getAbsolutePath() });
			container.runScriptlet(PathType.ABSOLUTE, new File(
					getCalabashGemDirectory(), "bin/calabash-ios")
					.getAbsolutePath());
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to setup calabash. %s", e.getMessage()));
		}
	}

	public void start() throws CalabashException {
		try {
			container.clear();
			String launcherScript = new File(rbScriptsDir, "launcher.rb")
					.getAbsolutePath();
			container.runScriptlet(PathType.ABSOLUTE, launcherScript);
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to start simulator. %s", e.getMessage()), e);
		}
	}

	public RubyArray query(String query, String... args)
			throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");

			container.put("cjQueryString", query);
			container.put("cjQueryArgs", args);

			RubyArray queryResults = null;
			if (args != null && args.length > 0)
				queryResults = (RubyArray) container
						.runScriptlet("query(cjQueryString, cjQueryArgs)");
			else
				queryResults = (RubyArray) container
						.runScriptlet("query(cjQueryString)");

			return queryResults;
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to execute '%s'. %s", query, e.getMessage()), e);
		}
	}

	public void touch(String query) throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core",
					"Calabash::Cucumber::Operations");
			container.put("cjQueryString", query);
			container.runScriptlet("touch(cjQueryString)");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to touch '%s'. %s", query, e.getMessage()), e);
		}
	}

	public void flash(String query) throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.put("cjQueryString", query);
			container.runScriptlet("flash(cjQueryString)");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to flash '%s'. %s", query, e.getMessage()), e);
		}
	}

	public void scroll(String query, ScrollDirection direction)
			throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.put("cjQueryString", query);
			container.put("cjDirection", direction.getDirection());
			container.runScriptlet("scroll(cjQueryString, cjDirection)");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to scroll '%s'. %s", query, e.getMessage()), e);
		}
	}

	public void rotate(String direction) throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core",
					"Calabash::Cucumber::TestsHelpers");
			container.put("cjDirection", direction);
			container.runScriptlet("rotate(cjDirection.to_sym)");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to rotate application. %s", e.getMessage()), e);
		}
	}

	public void exit() throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.runScriptlet("calabash_exit");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to exit application. %s", e.getMessage()), e);
		}
	}

	public void serverVersion() throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.runScriptlet("server_version");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to check server version. %s", e.getMessage()), e);
		}
	}

	public void takeScreenShot(File dir, String fileName)
			throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.put("cjPrefix", dir.getAbsolutePath() + "/");
			container.put("cjFileName", fileName);
			container
					.runScriptlet("screenshot(options={:prefix => cjPrefix, :name => cjFileName})");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to take screenshot. %s", e.getMessage()), e);
		}
	}

	public void enterText(String text) throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.put("cjTextToEnter", text);
			container.runScriptlet("keyboard_enter_text(cjTextToEnter)");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to enter the text '%s'. %s", text, e.getMessage()),
					e);
		}
	}

	public void enterChar(String text) throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.put("cjCharToEnter", text);
			container.runScriptlet("keyboard_enter_char(cjCharToEnter)");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to enter the text '%s'. %s", text, e.getMessage()),
					e);
		}
	}

	public void done() throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core");
			container.runScriptlet("done");
		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to press 'done'. %s", e.getMessage()), e);
		}
	}

	public void waitFor(ICondition condition, WaitOptions options)
			throws CalabashException, OperationTimedoutException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core",
					"Calabash::Cucumber::WaitHelpers");
			container.put("cjWaitCondition", condition);
			if (options == null)
				container.runScriptlet("wait_for { cjWaitCondition.test }");
			else {
				container.put("cjWaitTimeout", options.getTimeoutInSec());
				container.put("cjWaitRetryFreq", options.getRetryFreqInSec());
				container.put("cjWaitPostTimeout",
						options.getPostTimeoutInSec());
				container.put("cjWaitTimeoutMessage",
						options.getTimeoutMessage());
				container.put("cjWaitShouldTakeScreenshot",
						options.shouldScreenshotOnError());
				container
						.runScriptlet("wait_for({:timeout => cjWaitTimeout, :retry_frequency => cjWaitRetryFreq, :post_timeout => cjWaitPostTimeout, :timeout_message => cjWaitTimeoutMessage, :screenshot_on_error => cjWaitShouldTakeScreenshot}) { cjWaitCondition.test }");
			}
		} catch (Exception e) {
			if (e.toString().contains(
					"Calabash::Cucumber::WaitHelpers::WaitError")) {
				String message = null;
				if (options != null)
					message = options.getTimeoutMessage();

				throw new OperationTimedoutException(
						message == null ? "Timed out waiting..." : message);
			}
			else
				throw new CalabashException(String.format(
						"Failed to press wait for condition. %s",
						e.getMessage()), e);
		}
	}

	public void waitForNoneAnimating() throws CalabashException {
		try {
			container.clear();
			addRequiresAndIncludes("Calabash::Cucumber::Core",
					"Calabash::Cucumber::WaitHelpers");
			container.runScriptlet("wait_for_none_animating");
		} catch (Exception e) {
			throw new CalabashException(String.format("Failed to wait. %s",
					e.getMessage()), e);
		}
	}

	private void addRequiresAndIncludes(String... modules) {
		StringBuilder script = new StringBuilder(
				"require 'calabash-cucumber'\n");
		for (String module : modules) {
			script.append("include " + module);
			script.append("\n");
		}

		// HACK - Calabash ruby calls embed method when there is a error.
		// This is from cucumber and won't be available in the Jruby
		// environment. So just defining a function to suppress the error
		script.append("def embed(a,b,c)\nend\n");

		container.runScriptlet(script.toString());
	}

	private final void initializeScriptingContainer() throws CalabashException {
		HashMap<String, String> environmentVariables = new HashMap<String, String>();
		environmentVariables.put("PROJECT_DIR", projectDir.getAbsolutePath());
		environmentVariables.put("HOME", System.getProperty("user.home"));
		container.setEnvironment(environmentVariables);

		// Load paths points to the gem directory
		container.getLoadPaths().addAll(getLoadPaths());

		// No stderr
		container.setErrorWriter(new StringWriter());
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
			throw new CalabashException("Couldn't find any gems inside "
					+ gemsDir.getAbsolutePath());

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
