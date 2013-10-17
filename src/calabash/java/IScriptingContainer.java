package calabash.java;

import java.io.Writer;
import java.util.HashMap;
import java.util.List;

import org.jruby.embed.PathType;

interface IScriptingContainer {

	void put(String key, Object value);

	Object runScriptlet(String script);

	void runScriptlet(PathType pathType, String filePath);

	void clear();

	void terminate();

	void setHomeDirectory(String absolutePath);

	void setEnvironment(HashMap<String, String> environmentVariables);

	List<java.lang.String> getLoadPaths();

	void setErrorWriter(Writer writer);

}
