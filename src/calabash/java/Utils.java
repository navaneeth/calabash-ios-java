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
public final class Utils {
	
	public static String getStringFromJSON(JSONObject target, String key) {
		try {
			return target.getString(key);
		} catch (JSONException e) {
			return null;
		}
	}

	public static Integer getIntFromJSON(JSONObject target, String key) {
		try {
			return target.getInt(key);
		} catch (JSONException e) {
			return null;
		}
	}

}
