/**
 * 
 */
package calabash.java;

/**
 * 
 *
 */
public final class Rect {

	private final Integer x;
	private final Integer y;
	private final Integer width;
	private final Integer height;
	private final Integer center_x;
	private final Integer center_y;

	public Rect(Integer x, Integer y, Integer width, Integer height,
			Integer center_x, Integer center_y) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.center_x = center_x;
		this.center_y = center_y;
	}

	public Integer getX() {
		return x;
	}

	public Integer getY() {
		return y;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public Integer getCenter_x() {
		return center_x;
	}

	public Integer getCenter_y() {
		return center_y;
	}

	public String toString() {
		return String
				.format("x: %s, y: %s, width: %s, height: %s, center_x: %s, center_y: %s",
						getX(), getY(), getWidth(), getHeight(), getCenter_x(),
						getCenter_y());
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rect rect = (Rect) o;

        if (center_x != null ? !center_x.equals(rect.center_x) : rect.center_x != null) return false;
        if (center_y != null ? !center_y.equals(rect.center_y) : rect.center_y != null) return false;
        if (height != null ? !height.equals(rect.height) : rect.height != null) return false;
        if (width != null ? !width.equals(rect.width) : rect.width != null) return false;
        if (x != null ? !x.equals(rect.x) : rect.x != null) return false;
        if (y != null ? !y.equals(rect.y) : rect.y != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (width != null ? width.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (center_x != null ? center_x.hashCode() : 0);
        result = 31 * result + (center_y != null ? center_y.hashCode() : 0);
        return result;
    }
}
