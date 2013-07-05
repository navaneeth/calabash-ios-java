/**
 * 
 */
package calabash.java;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *
 */
public final class UIElements extends ArrayList<UIElement> {

	private static final long serialVersionUID = 3506802535880079938L;

	public UIElements(JSONArray elements) throws CalabashException {
		for (int i = 0; i < elements.length(); i++) {
			try {
				JSONObject jsonObject = elements.getJSONObject(i);
				this.add(new UIElement(jsonObject));
			} catch (Exception e) {
				throw new CalabashException("Unsupported JSON format.\n"
						+ elements.toString(), e);
			}
		}
	}

}
