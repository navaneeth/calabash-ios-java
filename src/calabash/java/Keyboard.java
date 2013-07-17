/**
 * 
 */
package calabash.java;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a Keyboard and key actions
 * 
 */
public final class Keyboard {

	private final Map<String, String> keyplaneNames = new HashMap<String, String>();

	/**
	 * Initializes a Keyboard instance
	 * 
	 * @throws CalabashException
	 *             If no visible keyboard detected
	 */
	public Keyboard() throws CalabashException {
		ensureKeyboardIsVisible();
		keyplaneNames.put("small_letters", "small-letters");
		keyplaneNames.put("capital_letters", "capital-letters");
		keyplaneNames.put("numbers_and_punctuation", "numbers-and-punctuation");
		keyplaneNames.put("first_alternate", "first-alternate");
		keyplaneNames.put("numbers_and_punctuation_alternate",
				"numbers-and-punctuation-alternate");
	}

	/**
	 * Enter the supplied text. Keyboard should be visible for
	 * this method to work
	 * 
	 * @param text
	 *            Text to input
	 * @throws CalabashException
	 */
	public void enterText(String text) throws CalabashException {
		ensureKeyboardIsVisible();
		for (int i = 0; i < text.length(); i++) {
			String toEnter = String.format("%c", text.charAt(i));
			try {
				enterSingleCharacterOrSpecialStrings(toEnter);
			} catch (CalabashException e) {
				boolean entered = enterTextBySearchingKeyplanes(toEnter, null);
				if (!entered)
					throw new CalabashException("Could not enter " + toEnter);
			}
		}
	}

	/**
	 * Presses the supplied special key
	 * 
	 * @param key
	 *            Key to press
	 * @throws CalabashException
	 */
	public void pressSpecialKey(SpecialKeys key) throws CalabashException {
		if (key == null)
			throw new CalabashException("Key should not be null");

		ensureKeyboardIsVisible();
		enterSingleCharacterOrSpecialStrings(key.getKeyName());
	}

	/**
	 * Presses the done/search button or the default action
	 * 
	 * @throws CalabashException
	 */
	public void done() throws CalabashException {
		pressSpecialKey(SpecialKeys.Return);
	}

	private void enterSingleCharacterOrSpecialStrings(String text)
			throws CalabashException {
		JSONObject data = new JSONObject();
		data.put("key", text);
		data.put("events", Utils.loadPlaybackData("touch_done"));
		Utils.sleep(10);
	}

	private void ensureKeyboardIsVisible() throws CalabashException {
		UIElements keyboard = null;// Utils.query("view:'UIKBKeyplaneView'");
		if (keyboard.size() == 0)
			throw new CalabashException(
					"Failed to get keyboard. No visible keyboard detected");
	}

	private enum KeyplaneType {
		UIKBTree, UIKBKeyPlane
	}

	private boolean enterTextBySearchingKeyplanes(String text,
			HashSet<String> visited) throws CalabashException {
		String currentKeyplane = getCurrentKeyplane();

		if (visited == null)
			visited = new HashSet<String>();

		try {
			enterSingleCharacterOrSpecialStrings(text);
			return true;
		} catch (CalabashException e) {
			visited.add(currentKeyplane);

			JSONArray result = null;// Utils.query("view:'UIKBKeyplaneView'",
//					findKeyplaneType() == KeyplaneType.UIKBTree ? new String[] {
//							"keyplane", "properties" } : new String[] {
//							"keyplane", "attributes", "dict" });
			if (result.length() == 0)
				throw new CalabashException(
						"Can't get alternative keyplane. view:'UIKBKeyplaneView' is empty");

			JSONObject properties = result.getJSONObject(0);
			Collection<String> known = keyplaneNames.values();
			String[] shiftMore = { "shift", "more" };
			for (String key : shiftMore) {
				String plane = properties.getString(String.format(
						"%s-alternate", key));
				if (known.contains(plane) && !visited.contains(plane)) {
					enterSingleCharacterOrSpecialStrings(Utils.capitalize(key));
					boolean done = enterTextBySearchingKeyplanes(text, visited);
					if (done)
						return true;
					enterSingleCharacterOrSpecialStrings(Utils.capitalize(key));

				}
			}
			return false;
		}
	}

	private String getCurrentKeyplane() throws CalabashException {
		KeyplaneType keyplaneType = findKeyplaneType();
		if (keyplaneType == KeyplaneType.UIKBTree) {
			JSONArray result = null;// Utils.query("view:'UIKBKeyplaneView'",
//					"keyplane", "componentName");
			if (result.length() == 0)
				throw new CalabashException(
						"Can't get current keyplane. view:'UIKBKeyplaneView' is empty");
			return result.getString(0).toLowerCase();
		} else if (keyplaneType == KeyplaneType.UIKBKeyPlane) {
			JSONArray result = null;// Utils.query("view:'UIKBKeyplaneView'",
//					"keyplane", "name");
			if (result.length() == 0)
				throw new CalabashException(
						"Can't get current keyplane. view:'UIKBKeyplaneView' is empty");
			return result.getString(0).toLowerCase();
		}

		throw new CalabashException("Can't get current keyplane");
	}

	private KeyplaneType findKeyplaneType() throws CalabashException {
		JSONArray keyplanes = null; 
		if (keyplanes.length() == 0)
			throw new CalabashException("No keyplane available");
		if (keyplanes.length() > 1)
			throw new CalabashException("Too many keyplanes");

		String keyplane = keyplanes.getString(0);
		if (keyplane.startsWith("<UIKBTree"))
			return KeyplaneType.UIKBTree;
		else if (keyplane.startsWith("<UIKBKeyplane"))
			return KeyplaneType.UIKBKeyPlane;

		throw new CalabashException("No match for keyplane: " + keyplane);

	}

}
