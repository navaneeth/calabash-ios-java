/**
 * 
 */
package calabash.java;

/**
 * Thrown when any error happened during calabash execution
 *
 */
public final class CalabashException extends Exception {

	private static final long serialVersionUID = 6284886162437215058L;

	public CalabashException(String message) {
		super(message);
	}

	public CalabashException(String message, Throwable cause) {
		super(message, cause);
	}

}
