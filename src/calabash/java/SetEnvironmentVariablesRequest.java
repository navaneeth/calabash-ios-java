package calabash.java;

import java.util.HashMap;

class SetEnvironmentVariablesRequest extends Request {
	public HashMap<String, String> environmentVariables;

	public SetEnvironmentVariablesRequest(
			HashMap<String, String> environmentVariables) {
		this.environmentVariables = environmentVariables;
	}

	private static final long serialVersionUID = -8587548120543298977L;
}