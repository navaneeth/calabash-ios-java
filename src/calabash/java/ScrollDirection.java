/**
 * 
 */
package calabash.java;

public enum ScrollDirection {

	LEFT("left"), RIGHT("right"), UP("up"), DOWN("down");

	private final String direction;

	private ScrollDirection(String direction) {
		this.direction = direction;
	}

	public String getDirection() {
		return direction;
	}

}
