/**
 * 
 */
package calabash.java;

import java.util.Map;

/**
 * Provides information about calabash
 * 
 */
public final class CalabashInfo {

	private final Map<Object, Object> hash;

	public CalabashInfo(Map<Object, Object> hash) {
		this.hash = hash;
	}

	public String getApplicationName() {
		return getValue("app_name");
	}

	public String getSimulatorDevice() {
		return getValue("simulator_device");
	}

	public String getIOSVersion() {
		return getValue("iOS_version");
	}

	public String getApplicationVersion() {
		return getValue("app_version");
	}

	public String getSystem() {
		return getValue("system");
	}

	public String getApplicationId() {
		return getValue("app_id");
	}

	public String getServerVersion() {
		return getValue("version");
	}
	
	public String getClientVersion() {
		return getValue("client_version");
	}

	public String getSimulator() {
		return getValue("simulator");
	}

	public String getValue(String key) {
		if (hash.containsKey(key)) {
			return hash.get(key).toString();
		}

		return null;
	}

	@Override
	public String toString() {
		return String
				.format("ServerInfo [ApplicationName = %s, SimulatorDevice = %s, IOSVersion = %s, ApplicationVersion = %s, System = %s, ApplicationId = %s, ServerVersion = %s, ClientVersion = %s, Simulator = %s]",
						getApplicationName(), getSimulatorDevice(),
						getIOSVersion(), getApplicationVersion(), getSystem(),
						getApplicationId(), getServerVersion(), getClientVersion(), getSimulator());
	}

}
