/**
 * 
 */
package calabash.java;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 *
 */
public final class UIElement {

	private final JSONObject data;

	public UIElement(JSONObject data) {
		this.data = data;
	}

	public String getElementClass() {
		return getStringFromJSON(data, "class");
	}

	public String getId() {
		return getStringFromJSON(data, "id");
	}

	public String getLabel() {
		return getStringFromJSON(data, "label");
	}

	public String getDescription() {
		return getStringFromJSON(data, "description");
	}

	public Rect getRect() {
		JSONObject rect;
		try {
			rect = data.getJSONObject("rect");
		} catch (JSONException e) {
			return null;
		}

		return new Rect(getIntFromJSON(rect, "x"), getIntFromJSON(rect, "y"),
				getIntFromJSON(rect, "width"), getIntFromJSON(rect, "height"),
				getIntFromJSON(rect, "center_x"), getIntFromJSON(rect,
						"center_y"));
	}
	
	public Rect getFrame() {
		JSONObject rect;
		try {
			rect = data.getJSONObject("frame");
		} catch (JSONException e) {
			return null;
		}

		return new Rect(getIntFromJSON(rect, "x"), getIntFromJSON(rect, "y"),
				getIntFromJSON(rect, "width"), getIntFromJSON(rect, "height"),
				null, null);
	}

	private String getStringFromJSON(JSONObject target, String key) {
		try {
			return target.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	private Integer getIntFromJSON(JSONObject target, String key) {
		try {
			return target.getInt(key);
		} catch (JSONException e) {
			return null;
		}
	}

}
