/**
 * 
 */
package calabash.java;

/**
 * Wait options to be specified for waitFor
 *
 */
public final class WaitOptions {

	private final int timeoutInSec;
	private final int retryFreqInSec;
	private final int postTimeoutInSec;
	private final String timeoutMessage;
	private final boolean screenshotOnError;

    /**
     *
     * @param timeoutInSec maximum number of seconds to wait
     * @param retryFreqInSec wait this long before retrying the block
     * @param postTimeoutInSec wait this long after the block returns true
     * @param timeoutMessage error message
     * @param screenshotOnError take a screenshot in case of error
     */
	public WaitOptions(int timeoutInSec, int retryFreqInSec,
			int postTimeoutInSec, String timeoutMessage,
			boolean screenshotOnError) {
		this.timeoutInSec = timeoutInSec;
		this.retryFreqInSec = retryFreqInSec;
		this.postTimeoutInSec = postTimeoutInSec;
		this.timeoutMessage = timeoutMessage;
		this.screenshotOnError = screenshotOnError;
	}

	public int getTimeoutInSec() {
		return timeoutInSec;
	}

	public int getRetryFreqInSec() {
		return retryFreqInSec;
	}

	public int getPostTimeoutInSec() {
		return postTimeoutInSec;
	}

	public String getTimeoutMessage() {
		return timeoutMessage;
	}

	public boolean shouldScreenshotOnError() {
		return screenshotOnError;
	}

}
