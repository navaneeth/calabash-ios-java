package calabash.java;

import org.jruby.embed.PathType;


public class RunScriptletRequest extends Request {
	private static final long serialVersionUID = -5771451238821585307L;
	public String script;
	public String fileName;
	public PathType pathType;
	
	public RunScriptletRequest() {
	}

	public RunScriptletRequest(String script) {
		this.script = script;
	}

	public RunScriptletRequest(PathType pathType, String fileName) {
		this.pathType = pathType;
		this.fileName = fileName;
	}
}