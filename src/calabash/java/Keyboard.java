/**
 * 
 */
package calabash.java;

/**
 * Represents a Keyboard and key actions
 * 
 */
public final class Keyboard {

	private final CalabashWrapper calabashWrapper;

	/**
	 * Initializes a Keyboard instance
	 * 
	 * @param calabashWrapper
	 * 
	 * @throws CalabashException
	 *             If no visible keyboard detected
	 */
	public Keyboard(CalabashWrapper calabashWrapper) throws CalabashException {
		this.calabashWrapper = calabashWrapper;
	}

	/**
	 * Enter the supplied text. Keyboard should be visible for this method to
	 * work
	 * 
	 * @param text
	 *            Text to input
	 * @throws CalabashException
	 */
	public void enterText(String text) throws CalabashException {
		calabashWrapper.enterText(text);
	}

	/**
	 * Presses the supplied special key
	 * 
	 * @param key
	 *            Key to press
	 * @throws CalabashException
	 */
	public void pressSpecialKey(SpecialKeys key) throws CalabashException {
		calabashWrapper.enterChar(key.getKeyName());
	}

	/**
	 * Presses the done/search button or the default action
	 * 
	 * @throws CalabashException
	 */
	public void done() throws CalabashException {
		calabashWrapper.done();
	}

}
