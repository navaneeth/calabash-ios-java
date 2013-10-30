package calabash.java;

/**
 * Options for Swipe action
 * 
 */
public class SwipeOptions {

	private Force force;
	private SwipeDelta swipeDelta;

	public SwipeOptions(Force force, SwipeDelta swipeDelta) {
		this.force = force;
		this.swipeDelta = swipeDelta;
	}

	public SwipeOptions(Force force) {
		this.force = force;
	}

	public SwipeOptions(SwipeDelta swipeDelta) {
		this.swipeDelta = swipeDelta;
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer("{");
		if (force != null)
			result.append(force.toString());

		if (swipeDelta != null) {
			if (!result.toString().endsWith("{"))
				result.append(", ");
			result.append(swipeDelta.toString());
		}

		result.append("}");

		return result.toString();
	}

	public static class SwipeDelta {
		private Offset horizontal;
		private Offset vertical;

		public SwipeDelta(Offset horizontal, Offset vertical) {
			this.horizontal = horizontal;
			this.vertical = vertical;
		}

		@Override
		public String toString() {
			StringBuffer result = new StringBuffer();
			result.append("'swipe-delta' => {");
			if (horizontal != null) {
				result.append(String.format(
						":horizontal => {:dx => %d, :dy => %d}",
						horizontal.getX(), horizontal.getY()));
			}

			if (vertical != null) {
				if (horizontal != null)
					result.append(", ");
				result.append(String.format(
						":vertical => {:dx => %d, :dy => %d}", vertical.getX(),
						vertical.getY()));
			}
			result.append("}");
			return result.toString();
		}
	}

	public static enum Force {

		Strong("strong"), Normal("normal"), Light("light");

		private String name;

		Force(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return String.format(":force => :%s", name);
		}
	}

}
