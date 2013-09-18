package calabash.java;

/**
 * Provides the scroll options
 *
 */
public final class ScrollOptions {

	private int row;
	private int section;
	private Direction direction;
	private boolean animate;
	private int postScrollWaitInsec;

	public ScrollOptions() {
		this.row = 0;
		this.section = 0;
		this.direction = Direction.DOWN;
		this.animate = true;
		this.postScrollWaitInsec = 1;
	}

	public ScrollOptions(int row, int section) {
		this();
		this.row = row;
		this.section = section;
	}

	public int getRow() {
		return row;
	}

	public int getSection() {
		return section;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean shouldAnimate() {
		return animate;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public void setAnimate(boolean animate) {
		this.animate = animate;
	}

	public int getPostScrollWaitInsec() {
		return postScrollWaitInsec;
	}

	public void setPostScrollWaitInsec(int postScrollWaitInsec) {
		this.postScrollWaitInsec = postScrollWaitInsec;
	}

}
