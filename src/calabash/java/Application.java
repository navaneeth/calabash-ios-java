/**
 * 
 */
package calabash.java;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an iOS application
 *
 */
public final class Application {

	private final Http http;

	public Application() {
		this.http = new Http(Config.endPoint());
	}

	public UIElements query(String query) throws CalabashException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("query", query);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("method_name", "query");
		map.put("arguments", new String[0]);
		jsonObject.put("operation", map);

		String result = http.post("map", jsonObject.toString());
		JSONArray results;
		try {
			results = new JSONObject(result).getJSONArray("results");
		} catch (JSONException e) {
			throw new CalabashException("Result is not in expected format.\n"
					+ result, e);
		}

		return new UIElements(results, query);
	}

	/**
	 * Kills the application
	 * 
	 */
	public void exit() {
		try {
			http.post("exit", "");
		} catch (CalabashException e) {
		}
	}
}
