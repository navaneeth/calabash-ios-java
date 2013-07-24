/**
 * 
 */
package calabash.java;

import java.io.File;

import org.jruby.RubyArray;

/**
 * Represents an iOS application
 * 
 */
public final class Application {

	private final CalabashWrapper calabashWrapper;

	/**
	 * Initializes a new instance of Application
	 * 
	 * @param calabashWrapper
	 */
	public Application(CalabashWrapper calabashWrapper) {
		this.calabashWrapper = calabashWrapper;
	}

	/**
	 * Runs a query on the remote iOS application and returns a list of
	 * UIElement
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
	 * Runs a query on the remote iOS application and returns results as JSON
	 * 
	 * @param query
	 *            Calabash iOS supported query
	 * @param filter
	 *            Properties to be fetched
	 * @return
	 * @throws CalabashException
	 */
	public Object[] query(String query, String... filter)
			throws CalabashException {
		RubyArray rubyArray = calabashWrapper.query(query, filter);
		return Utils.toJavaArray(rubyArray);
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

	public void waitFor(ICondition condition) throws CalabashException,
			OperationTimedoutException {
		waitFor(condition, null);
	}

	public void waitFor(ICondition condition, WaitOptions options)
			throws CalabashException, OperationTimedoutException {
		calabashWrapper.waitFor(condition, options);
	}

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
