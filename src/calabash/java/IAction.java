/**
 * 
 */
package calabash.java;

import calabash.java.SwipeOptions.Force;

/**
 * Interface to be implemented by all elements which supports actions
 * 
 */
public interface IAction {

	/**
	 * Touch this element
	 * 
	 * @throws CalabashException
	 */
	void touch() throws CalabashException;

	/**
	 * Flash this UIElement
	 * 
	 * @throws CalabashException
	 */
	void flash() throws CalabashException;

	/**
	 * Scroll the element to the direction
	 * 
	 * @param direction
	 *            Direction to scroll
	 * @throws CalabashException
	 */
	void scroll(Direction direction) throws CalabashException;

	/**
	 * Swipe the element to the specified direction
	 * 
	 * @param direction
	 *            Direction to swipe
	 * @throws CalabashException
	 */
	void swipe(Direction direction) throws CalabashException;

	/**
	 * Swipe the element to the specified direction with the specified force
	 * 
	 * @param direction
	 *            Direction to swipe
	 * @param force
	 *            Swipe force
	 * @throws CalabashException
	 */
	void swipe(Direction direction, Force force) throws CalabashException;

	/**
	 * Swipe the element to the specified direction with the specified options
	 * 
	 * @param direction
	 *            Direction to swipe
	 * @param options
	 *            Options to use
	 * @throws CalabashException
	 */
	void swipe(Direction direction, SwipeOptions options)
			throws CalabashException;

	/**
	 * Pinch the element in
	 * 
	 * @throws CalabashException
	 */
	void pinchIn() throws CalabashException;

	/**
	 * Pinch the element out
	 * 
	 * @throws CalabashException
	 */
	void pinchOut() throws CalabashException;

}
