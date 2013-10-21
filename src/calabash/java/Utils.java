/**
 * 
 */
package calabash.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jruby.RubyArray;
import org.jruby.RubyHash;
import org.jruby.RubyObject;
import org.jruby.RubySymbol;
import org.jruby.embed.PathType;

import calabash.java.CalabashScriptExecutor.ExceptionResponse;
import calabash.java.CalabashScriptExecutor.GetLoadPathsResponse;
import calabash.java.CalabashScriptExecutor.Response;
import calabash.java.CalabashScriptExecutor.RunScriptletResponse;
import calabash.java.RemoteScriptingContainer.AddLoadPathRequest;
import calabash.java.RemoteScriptingContainer.ClearRequest;
import calabash.java.RemoteScriptingContainer.EnableLoggingRequest;
import calabash.java.RemoteScriptingContainer.GetLoadPathRequest;
import calabash.java.RemoteScriptingContainer.PutRequest;
import calabash.java.RemoteScriptingContainer.Request;
import calabash.java.RemoteScriptingContainer.RunScriptletRequest;
import calabash.java.RemoteScriptingContainer.SetEnvironmentVariablesRequest;
import calabash.java.RemoteScriptingContainer.SetHomeDirectoryRequest;
import calabash.java.RemoteScriptingContainer.TerminateRequest;

import com.esotericsoftware.kryo.Kryo;

final class Utils {

	public static String getStringFromHash(Map<Object, Object> target,
			String key) {
		try {
			Object value = target.get(key);
			if (value != null)
				return value.toString();
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static Integer getIntFromHash(Map<Object, Object> target, String key) {
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
		if (rubyObject instanceof RubySymbol)
			return rubyObject.toString();
		if (rubyObject instanceof RubyObject)
			return ((RubyObject) rubyObject).toJava(Object.class);

		return rubyObject.toString();
	}

	public static HashMap<Object, Object> toJavaHash(RubyHash rubyHash) {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		Set<?> keySet = rubyHash.keySet();
		for (Object rubyKey : keySet) {
			Object rubyValue = rubyHash.get(rubyKey);
			Object javaKey = toJavaObject(rubyKey);
			Object javaValue = toJavaObject(rubyValue);
			map.put(javaKey, javaValue);
		}
		return map;
	}

	public static void inspectElement(UIElement element, int nestingLevel,
			InspectCallback callback) throws CalabashException {
		callback.onEachElement(element, nestingLevel);
		UIElements children = element.children();
		for (UIElement child : children) {
			inspectElement(child, nestingLevel + 1, callback);
		}
	}

	public static void runCommand(String[] command, String onExceptionMessage)
			throws CalabashException {
		int exitCode;
		try {
			Process Process = Runtime.getRuntime().exec(command);
			exitCode = Process.waitFor();
			if (exitCode == 0)
				return;
			else
				throw new CalabashException(onExceptionMessage);
		} catch (Exception e) {
			throw new CalabashException(onExceptionMessage);
		}
	}

	public static String toString(InputStream in) throws CalabashException {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();

		String read;
		try {
			while ((read = br.readLine()) != null) {
				sb.append(read);
			}
		} catch (IOException e) {
			throw new CalabashException("Error reading from stream.", e);
		}

		return sb.toString();
	}

	public static void registerClasses(Kryo kryo) {
		kryo.register(Object[].class);
		kryo.register(String[].class);
		kryo.register(HashMap.class);
		kryo.register(Object.class);
		kryo.register(Request.class);
		kryo.register(PathType.class);
		kryo.register(org.jruby.embed.EvalFailedException.class);
		kryo.register(List.class);
		kryo.register(ArrayList.class);

		kryo.register(PutRequest.class);
		kryo.register(ClearRequest.class);
		kryo.register(TerminateRequest.class);
		kryo.register(SetHomeDirectoryRequest.class);
		kryo.register(RunScriptletRequest.class);
		kryo.register(SetEnvironmentVariablesRequest.class);
		kryo.register(AddLoadPathRequest.class);
		kryo.register(GetLoadPathRequest.class);
		kryo.register(EnableLoggingRequest.class);

		kryo.register(GetLoadPathsResponse.class);
		kryo.register(Response.class);
		kryo.register(RunScriptletResponse.class);
		kryo.register(ExceptionResponse.class);
	}

}
