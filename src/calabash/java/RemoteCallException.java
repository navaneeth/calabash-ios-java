/**
 * 
 */
package calabash.java;

/**
 * 
 *
 */
public class RemoteCallException extends Exception {

	private static final long serialVersionUID = 412460839485235495L;

	public RemoteCallException(String action, Throwable cause) {
		super(String.format("Failed to execute '%s' command", action), cause);
	}

	public RemoteCallException(String action) {
		this(action, null);
	}

}
