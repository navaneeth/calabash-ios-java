package calabash.java;

public final class ScrollOptions {

	private int row;
	private int section;
	private ScrollDirection direction;
	private boolean animate;
	private int postScrollWaitInsec;

	public ScrollOptions() {
		this.row = 0;
		this.section = 0;
		this.direction = ScrollDirection.DOWN;
		this.animate = true;
		this.postScrollWaitInsec = 1;
	}

	public int getRow() {
		return row;
	}

	public int getSection() {
		return section;
	}

	public ScrollDirection getDirection() {
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

	public void setDirection(ScrollDirection direction) {
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
