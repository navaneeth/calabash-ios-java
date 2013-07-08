/**
 * 
 */
package calabash.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONObject;

/**
 * 
 *
 */
public final class Http {

	private final String endPoint;

	public Http(String endPoint) {
		this.endPoint = endPoint;
	}

	public boolean tryPing() {
		try {
			get("version", null);
			return true;
		} catch (CalabashException e) {
			return false;
		}
	}

	public CalabashServerVersion getServerVersion() throws CalabashException {
		JSONObject version = new JSONObject(get("version", null));
		return new CalabashServerVersion(version);

	}

	public String get(String path, Map<String, String> args)
			throws CalabashException {
		StringBuilder query = new StringBuilder(endPoint);
		query.append(path);
		if (args != null && !args.isEmpty()) {
			query.append("?");
			boolean firstItem = true;
			for (String key : args.keySet()) {
				String value = args.get(key);
				query.append(String.format("%s%s=%s", firstItem ? "" : "&",
						utf8URLEncode(key), utf8URLEncode(value)));
				firstItem = false;
			}
		}

		HttpURLConnection connection = null;
		try {
			URL url = new URL(query.toString());
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				InputStream errorStream = connection.getErrorStream();
				String message = convertStreamToString(errorStream);
				errorStream.close();
				throw new CalabashException(message);
			} else {
				InputStream stream = connection.getInputStream();
				String message = convertStreamToString(stream);
				stream.close();
				return message;
			}

		} catch (MalformedURLException e) {
			throw new CalabashException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CalabashException("Can't connect to " + endPoint, e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	public String post(String path, String data) throws CalabashException {
		HttpURLConnection connection = null;
		try {
			URL url = new URL(endPoint + path);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Length",
					Integer.toString(data.length()));
			connection.setDoOutput(true);
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(data.getBytes("UTF-8"));
			outputStream.close();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				InputStream errorStream = connection.getErrorStream();
				String message = convertStreamToString(errorStream);
				errorStream.close();
				throw new CalabashException(message);
			} else {
				InputStream stream = connection.getInputStream();
				String message = convertStreamToString(stream);
				stream.close();
				return message;
			}

		} catch (MalformedURLException e) {
			throw new CalabashException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CalabashException("Can't connect to " + endPoint, e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	private String convertStreamToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder builder = new StringBuilder();
		for (String line; (line = reader.readLine()) != null;) {
			builder.append(line);
		}

		return builder.toString();
	}

	private String utf8URLEncode(String text) {
		try {
			return URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// never happens
			return text;
		}
	}

}
