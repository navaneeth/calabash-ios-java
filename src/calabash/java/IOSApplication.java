/**
 * 
 */
package calabash.java;

import java.io.File;

import org.jruby.RubyArray;
import org.jruby.RubyHash;

/**
 * Represents an iOS application
 * 
 */
public class IOSApplication {

	private final CalabashWrapper calabashWrapper;

	/**
	 * Initializes a new instance of Application
	 * 
	 * @param calabashWrapper
	 */
	public IOSApplication(CalabashWrapper calabashWrapper) {
		this.calabashWrapper = calabashWrapper;
	}

	/**
	 * Runs a query on the remote iOS application and returns a list of
	 * UIElement
	 * <p>
	 * Eg:
	 * <pre>
	 * iosApplication.query(&quot;button index:0&quot;)
	 * </pre>
	 * 
	 * @param query
	 *            Calabash iOS supported query
	 * @return
	 * @throws CalabashException
	 */
	public UIElements query(String query) throws CalabashException {
		RubyArray array = calabashWrapper.query(query);
		return new UIElements(array, query, calabashWrapper);
	}

	/**
	 * Runs a query on the remote iOS application and returns a list of
	 * UIElement
	 * <p>
	 * Eg:
	 * <pre>
	 * String label = &quot;Foo&quot;;
	 * iosApplication.query(&quot;label marked:'%s'&quot;, label);
	 * </pre>
	 * 
	 * @param query
	 *            Calabash iOS supported query
	 * @param args
	 *            Arguments referenced by the format specifiers in the format
	 *            string. If there are more arguments than format specifiers,
	 *            the extra arguments are ignored. The number of arguments is
	 *            variable and may be zero
	 * @return
	 * @throws CalabashException
	 */
	public UIElements query(String query, Object... args)
			throws CalabashException {
		return query(String.format(query, args));
	}

	/**
	 * Records a sequence of events and saves them to disk.
	 * 
	 * @throws CalabashException
	 */
	public void startRecording() throws CalabashException {
		calabashWrapper.startRecording();
	}

	/**
	 * Stops the recording and saves the data to the specified file
	 * 
	 * @param filename
	 *            File name
	 * @throws CalabashException
	 */
	public void stopRecording(String filename) throws CalabashException {
		calabashWrapper.stopRecording(filename);
	}

	/**
	 * Plays a pre-recorded sequence of events on the application
	 * 
	 * @param recording
	 *            Name of the recording
	 * @throws CalabashException
	 */
	public void playback(String recording) throws CalabashException {
		calabashWrapper.playback(recording, null, null);
	}
	
	/**
	 * Plays a pre-recorded sequence of events on the application
	 * 
	 * @param recording
	 *            Name of the recording
	 * @param query Query to identify which view to playback the recorded touch-events on
	 * @throws CalabashException
	 */
	public void playback(String recording, String query, Offset offset) throws CalabashException {
		calabashWrapper.playback(recording, query, offset);
	}

	/**
	 * Kills the application
	 * 
	 * @throws CalabashException
	 * 
	 */
	public void exit() throws CalabashException {
		calabashWrapper.exit();
	}

	/**
	 * Restarts this application
	 * 
	 * @throws CalabashException
	 */
	public void restart() throws CalabashException {
		exit();
		calabashWrapper.start();
	}

	/**
	 * Returns a value indicating whether the application is running
	 * 
	 * @return true if the application is running, false otherwise
	 */
	public boolean isRunning() {
		try {
			calabashWrapper.serverVersion();
			return true;
		} catch (CalabashException e) {
			return false;
		}
	}
	
	/**
	 * Escapes single quotes in the calabash query
	 * See <a href="https://github.com/calabash/calabash-ios/wiki/08-Tips-and-Tricks#handling-quotes">Handling quotes</a>
	 * @param source
	 * @return
	 * @throws CalabashException
	 */
	public String escapeQuotes(String source) throws CalabashException {
		return calabashWrapper.escapeQuotes(source);
	}

	/**
	 * Gets the calabash server & client details
	 * 
	 * @return
	 * @throws CalabashException
	 */
	public CalabashInfo getCalabashInfo() throws CalabashException {
		Object serverVersion = calabashWrapper.serverVersion();
		if (serverVersion instanceof RubyHash) {
			Object clientVersion = calabashWrapper.clientVersion();
			if (clientVersion != null)
				((RubyHash) serverVersion).put("client_version",
						clientVersion.toString());

			return new CalabashInfo((RubyHash) serverVersion);
		}

		return null;
	}

	/**
	 * Takes a screenshot
	 * 
	 * @return
	 * @throws CalabashException
	 */
	public void takeScreenshot(File dir, String fileName)
			throws CalabashException {
		if (dir == null)
			throw new CalabashException("Empty directory name");
		if (fileName == null)
			throw new CalabashException("Empty file name");

		if (!dir.isDirectory())
			throw new CalabashException(dir.getAbsolutePath()
					+ " is not a directory");
		if (!dir.canWrite())
			throw new CalabashException(dir.getAbsolutePath()
					+ " is not writeable");

		calabashWrapper.takeScreenShot(dir, fileName);
	}

	/**
	 * Rotate the screen to the left
	 * 
	 * @throws CalabashException
	 */
	public void rotateLeft() throws CalabashException {
		calabashWrapper.rotate("left");
	}

	/**
	 * Rotate the screen to the right
	 * 
	 * @throws CalabashException
	 */
	public void rotateRight() throws CalabashException {
		calabashWrapper.rotate("right");
	}

	/**
	 * Gets the current keyboard
	 * 
	 * @return
	 * @throws CalabashException
	 *             If no visible keyboards are available.
	 */
	public Keyboard getKeyboard() throws CalabashException {
		return new Keyboard(calabashWrapper);
	}

	/**
	 * Waits for the specified condition. This uses default timeout period and
	 * throws an exception when timeout reaches
	 * 
	 * @param condition
	 *            Condition to wait for
	 * @throws CalabashException
	 *             When any calabash operations fails
	 * @throws OperationTimedoutException
	 *             When the operation elapsed the timeout period
	 */
	public void waitFor(ICondition condition) throws CalabashException,
			OperationTimedoutException {
		waitFor(condition, null);
	}

	/**
	 * Waits for the specified condition with the options specified
	 * 
	 * @param condition
	 *            Condition to wait for
	 * @param options
	 *            Wait options
	 * @throws CalabashException
	 *             When any calabash operations fails
	 * @throws OperationTimedoutException
	 *             When the operation elapsed the timeout period
	 */
	public void waitFor(ICondition condition, WaitOptions options)
			throws CalabashException, OperationTimedoutException {
		calabashWrapper.waitFor(condition, options);
	}

	/**
	 * Wait for all the elements to exist in the specified array
	 * 
	 * @param queries
	 *            Queries to perform
	 * @throws OperationTimedoutException
	 *             When the operation elapsed the timeout period
	 * @throws CalabashException
	 *             When any calabash operations fails
	 */
	public void waitForElementsExist(String[] queries)
			throws OperationTimedoutException, CalabashException {
		waitForElementsExist(queries, null);
	}

	public void waitForElementsExist(String[] queries, WaitOptions options)
			throws OperationTimedoutException, CalabashException {
		calabashWrapper.waitForElementsExist(queries, options);
	}

	public void waitForElementsToNotExist(String[] queries)
			throws OperationTimedoutException, CalabashException {
		waitForElementsToNotExist(queries, null);
	}

	public void waitForElementsToNotExist(String[] queries, WaitOptions options)
			throws OperationTimedoutException, CalabashException {
		calabashWrapper.waitForElementsToNotExist(queries, options);
	}

	/**
	 * Waits till all the animations finishes
	 * 
	 * @throws CalabashException
	 *             When any calabash operation fails
	 */
	public void waitForNoneAnimating() throws CalabashException {
		calabashWrapper.waitForNoneAnimating();
	}

	/**
	 * Fetches all elements in this application and executes callback for each
	 * of them
	 * 
	 * @param callback
	 *            Callback to be executed for each element
	 * @throws CalabashException
	 */
	public void inspect(InspectCallback callback) throws CalabashException {
		UIElements rootElements = getRootElements();
		if (rootElements == null)
			return;

		for (UIElement root : rootElements) {
			Utils.inspectElement(root, 0, callback);
		}
	}

	/**
	 * Gets all the root elements available This can be used to make a tree view
	 * of all the elements available in the view currently
	 * 
	 * @return list of root elements if available, null otherwise
	 * @throws CalabashException
	 */
	public UIElements getRootElements() throws CalabashException {
		RubyArray allElements = calabashWrapper.query("*");
		if (allElements.size() == 0)
			return null;

		UIElements rootElements = new UIElements();
		for (int i = 0; i < allElements.size(); i++) {
			String query = String.format("* index:%d", i);
			UIElement rootElement = getRootElement(query);
			if (rootElement != null && !rootElements.contains(rootElement))
				rootElements.add(rootElement);
		}

		return rootElements;
	}

	/**
	 * Checks if the element exists
	 * 
	 * @param query
	 *            Query to check
	 * @return true if the element is available
	 * @throws CalabashException
	 */
	public boolean elementExists(String query) throws CalabashException {
		return calabashWrapper.elementExists(query);
	}

	/**
	 * Sends the application to background for specified seconds. Application
	 * will be activated after the specified seconds This method blocks for
	 * specified seconds
	 * 
	 * @param seconds
	 *            Seconds to put the application in the background
	 * @throws CalabashException
	 */
	public void sendToBackground(int seconds) throws CalabashException {
		calabashWrapper.sendAppToBackground(seconds);
	}

	/**
	 * Waits for keyboard to appear
	 * 
	 * @throws CalabashException
	 */
	public void awaitKeyboard() throws CalabashException {
		calabashWrapper.awaitKeyboard();
	}

	private UIElement getRootElement(String query) throws CalabashException {
		UIElement rootElement = null;
		RubyArray result = calabashWrapper.query(query);
		if (result.size() == 0)
			return null;
		else {
			rootElement = new UIElements(result, query, calabashWrapper).get(0);
			UIElement element = getRootElement(query + " parent * index:0");
			if (element != null)
				rootElement = element;
		}

		return rootElement;
	}
}
