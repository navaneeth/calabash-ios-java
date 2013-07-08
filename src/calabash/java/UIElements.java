/**
 * 
 */
package calabash.java;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 *
 */
public final class UIElements extends ArrayList<UIElement> {

	private static final long serialVersionUID = 3506802535880079938L;

	public UIElements(JSONArray elements, String query)
			throws CalabashException {
		query = query.trim();
		Pattern pattern = Pattern.compile("^.+index:[0-9]+$");
		Matcher matcher = pattern.matcher(query);
		boolean indexedQuery = matcher.matches();

		for (int i = 0; i < elements.length(); i++) {
			try {
				JSONObject jsonObject = elements.getJSONObject(i);
				String q = query;
				if (!indexedQuery)
					q += " index:" + i;
				this.add(new UIElement(jsonObject, q));
			} catch (Exception e) {
				throw new CalabashException("Unsupported JSON format.\n"
						+ elements.toString(), e);
			}
		}
	}

}
