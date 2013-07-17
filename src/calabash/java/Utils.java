/**
 * 
 */
package calabash.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyObject;

/**
 * 
 *
 */
final class Utils {

	public static String getStringFromHash(RubyHash target, String key) {
		try {
			Object value = target.get(key);
			if (value != null)
				return value.toString();
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static Integer getIntFromHash(RubyHash target, String key) {
		String value = getStringFromHash(target, key);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		return null;
	}

	public static String capitalize(String string) {
		if (string == null || string.length() == 0)
			return string;

		return String.format("%c%s", Character.toUpperCase(string.charAt(0)),
				string.substring(1, string.length()));
	}
	
	public static void unzip(File zipFile, File destination)
			throws CalabashException {
		if (!zipFile.exists())
			throw new CalabashException("Zip file does not exists. "
					+ zipFile.getAbsolutePath());
		if (!zipFile.isFile())
			throw new CalabashException("Zip file should be a file. "
					+ zipFile.getAbsolutePath());

		if (!destination.exists())
			throw new CalabashException(
					"Destination directory does not exists. "
							+ destination.getAbsolutePath());
		if (!destination.isDirectory())
			throw new CalabashException("Destination is not a directory. "
					+ destination.getAbsolutePath());
		if (!destination.canWrite())
			throw new CalabashException("Destination is readonly. "
					+ destination.getAbsolutePath());

		final String[] command = { "unzip", "-uo", "-qq",
				zipFile.getAbsolutePath(), "-d", destination.getAbsolutePath() };
		try {
			Process process = Runtime.getRuntime().exec(command);
			int exitCode = process.waitFor();
			if (exitCode == 0 || exitCode == 1)
				return;
			else
				throw new CalabashException(String.format(
						"Failed to unzip %s to %s", zipFile.getAbsolutePath(),
						destination.getAbsolutePath()));

		} catch (Exception e) {
			throw new CalabashException(String.format(
					"Failed to unzip %s to %s. %s", zipFile.getAbsolutePath(),
					destination.getAbsolutePath(), e.getMessage()), e);
		}
	}

	public static void playback(String recordingName, String query)
			throws CalabashException {
		// CalabashServerVersion version = CalabashRunner.getServerVersion();
		// String uiaGesture = null;
		// if (version.getiOSVersion().major().equals("7"))
		// uiaGesture = "tap";
		//
		// String playbackData = loadPlaybackData(recordingName);
		// JSONObject postData = new JSONObject();
		// postData.put("events", playbackData);
		//
		// if (query != null)
		// postData.put("query", query);
		//
		// if (uiaGesture != null)
		// postData.put("uia_gesture", uiaGesture);
		//
		// new Http(Config.endPoint()).post("play", postData.toString());
	}

	public static String loadPlaybackData(String recordingName)
			throws CalabashException {
		// String os = Config.getOS();
		// if (os == null) {
		// os = "ios"
		// + CalabashRunner.getServerVersion().getiOSVersion().major();
		// }
		//
		// File file = getEventFile(recordingName, os);
		// if (!file.exists() && "ios6".equals(os))
		// file = getEventFile(recordingName, "ios5");
		//
		// if (!file.exists())
		// throw new CalabashException(String.format(
		// "Can't load playback data. %s does not exists",
		// file.getAbsolutePath()));
		//
		// try {
		// return readFileAsString(file);
		// } catch (IOException e) {
		// throw new CalabashException(String.format(
		// "Can't load playback data from %s. %s",
		// file.getAbsolutePath(), e.getMessage()), e);
		// }
		return null;
	}

	private static File getEventFile(String recordingName, String os) {
		// TODO: Remove hardcoded paths
		File eventsDir = new File(
				"/Users/navaneeth/projects/calabash/calabash-ios-java/events");
		return new File(eventsDir, String.format("%s_%s_%s.base64",
				recordingName, os, Config.getDevice()));
	}

	private static String readFileAsString(File f) throws IOException {
		if (!f.exists())
			return null;

		FileInputStream stream = new FileInputStream(f);
		byte[] b = new byte[stream.available()];
		stream.read(b);
		stream.close();

		return new String(b, "UTF-8");
	}

	public static void sleep(int ms) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
		}
	}
	
	public static Object[] toJavaArray(RubyArray array) {
		ArrayList<Object> result = new ArrayList<Object>();
		for (int i = 0; i < array.size(); i++) {
			Object rubyObject = array.get(i);
			Object javaObject = toJavaObject(rubyObject);
			result.add(javaObject);
		}
		
		return result.toArray();
	}
	
	public static Object toJavaObject(Object rubyObject) {
		if (rubyObject == null)
			return rubyObject;
		
		if (rubyObject instanceof RubyArray)
			return toJavaArray((RubyArray) rubyObject);
		if (rubyObject instanceof RubyHash)
			return toJavaHash((RubyHash) rubyObject);
		if (rubyObject instanceof RubyObject)
			return ((RubyObject) rubyObject).toJava(Object.class);
		
		return rubyObject.toString();
	}

	public static Map<?,?> toJavaHash(RubyHash rubyHash) {
		HashMap<Object,Object> map = new HashMap<Object, Object>();
		Set<?> keySet = rubyHash.keySet();
		for (Object rubyKey : keySet) {
			Object rubyValue = rubyHash.get(rubyKey);
			Object javaKey = toJavaObject(rubyKey);
			Object javaValue = toJavaObject(rubyValue);
			map.put(javaKey, javaValue);
		}
		return map;
	}

}
