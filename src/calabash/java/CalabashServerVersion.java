/**
 * 
 */
package calabash.java;

import org.json.JSONObject;
import static calabash.java.Utils.*;

/**
 * 
 *
 */
public final class CalabashServerVersion {

	private final String applicationName;
	private final String iOSVersion;
	private final String appVersion;
	private final String system;
	private final String appId;
	private final String simulator;

	public CalabashServerVersion(JSONObject version) {
		this.applicationName = getStringFromJSON(version, "app_name");
		this.iOSVersion = getStringFromJSON(version, "iOS_version");
		this.appVersion = getStringFromJSON(version, "app_version");
		this.system = getStringFromJSON(version, "system");
		this.appId = getStringFromJSON(version, "app_id");
		this.simulator = getStringFromJSON(version, "simulator");
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getiOSVersion() {
		return iOSVersion;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public String getSystem() {
		return system;
	}

	public String getAppId() {
		return appId;
	}

	public String getSimulator() {
		return simulator;
	}

}
