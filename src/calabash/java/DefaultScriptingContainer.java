package calabash.java;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import org.jruby.embed.PathType;
import org.jruby.embed.ScriptingContainer;

class DefaultScriptingContainer implements IScriptingContainer {

	private final ScriptingContainer container = new ScriptingContainer();

	@Override
	public void put(String key, Object value) {
		container.put(key, value);
	}

	@Override
	public Object runScriptlet(String script) {
		Object result = container.runScriptlet(script);
		if (result != null) {
			return Utils.toJavaObject(result);
		}
		
		return null;
	}

	@Override
	public void runScriptlet(PathType pathType, String filePath) {
		container.runScriptlet(pathType, filePath);
	}

	@Override
	public void clear() {
		container.clear();
	}

	@Override
	public void terminate() {
		container.terminate();
	}

	@Override
	public void setHomeDirectory(String absolutePath) {
		container.setHomeDirectory(absolutePath);
	}

	@Override
	public void setEnvironment(HashMap<String, String> environmentVariables) {
		container.setEnvironment(environmentVariables);
	}

	@Override
	public List<String> getLoadPaths() {
		return container.getLoadPaths();
	}

	@Override
	public void setErrorWriter(Writer writer) {
		container.setErrorWriter(writer);
	}

}
