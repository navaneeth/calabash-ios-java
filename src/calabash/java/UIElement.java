/**
 * 
 */
package calabash.java;

import org.json.JSONException;
import org.json.JSONObject;
import static calabash.java.Utils.*;

/**
 * 
 *
 */
public final class UIElement {

	private final JSONObject data;
	private final String query;

	public UIElement(JSONObject data, String query) {
		this.data = data;
		this.query = query;
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

	public void touch() throws CalabashException {
		Http http = new Http(Config.endPoint());
		CalabashServerVersion version = http.getServerVersion();

		String uiaGesture = null;
		if (version.getiOSVersion().major().equals("7"))
			uiaGesture = "tap";

		String touchEventData = Utils.loadPlaybackData("touch");
		JSONObject postData = new JSONObject();
		postData.put("events", touchEventData);
		postData.put("query", query);
		if (uiaGesture != null)
			postData.put("uia_gesture", uiaGesture);
		
		http.post("play", postData.toString());
	}

}
