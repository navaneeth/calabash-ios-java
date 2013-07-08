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
	private final Version iOSVersion;
	private final Version appVersion;
	private final Version calabashServerVersion;
	private final String system;
	private final String appId;
	private final String simulator;

	public CalabashServerVersion(JSONObject version) throws CalabashException {
		this.applicationName = getStringFromJSON(version, "app_name");
		this.iOSVersion = new Version(getStringFromJSON(version, "iOS_version"));
		this.appVersion = new Version(getStringFromJSON(version, "app_version"));
		this.system = getStringFromJSON(version, "system");
		this.appId = getStringFromJSON(version, "app_id");
		this.simulator = getStringFromJSON(version, "simulator");
		this.calabashServerVersion = new Version(getStringFromJSON(version, "version"));
	}

	public String getApplicationName() {
		return applicationName;
	}

	public Version getiOSVersion() {
		return iOSVersion;
	}

	public Version getAppVersion() {
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

	public Version getCalabashServerVersion() {
		return calabashServerVersion;
	}

}
