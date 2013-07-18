/**
 * 
 */
package calabash.java;

import org.jruby.RubyArray;

/**
 * Represents an iOS application
 * 
 */
public final class Application {

	private enum HomeButtonPosition {
		DOWN, UP, RIGHT, LEFT;
	}

	private HomeButtonPosition homeButtonPosition = HomeButtonPosition.DOWN;
	private final CalabashWrapper calabashWrapper;

	/**
	 * Initializes a new instance of Application
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
//		return http.tryPing();
		return true;
	}

	/**
	 * Takes a screenshot
	 * 
	 * @return
	 * @throws CalabashException
	 */
	public byte[] takeScreenshot() throws CalabashException {
//		return http.getBytes("screenshot", null);
		return null;
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
		return new Keyboard();
	}
}
