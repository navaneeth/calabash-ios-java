/**
 * 
 */
package calabash.java;

/**
 * Provides an interface to test for a condition
 * 
 */
public interface ICondition {

	/**
	 * Implement this method and perform any tests required.
	 * 
	 * @return true if the test is successful, false otherwise
	 * @throws CalabashException
	 */
	boolean test() throws CalabashException;
}
