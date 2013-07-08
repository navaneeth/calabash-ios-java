/**
 * 
 */
package calabash.java;

/**
 * 
 *
 */
public final class Version {

	private String majorVersion;
	private String minorVersion;
	private String patchVersion;

	public Version(String versionString) throws CalabashException {
		String[] parts = versionString.split("[,.]");
		switch (parts.length) {
		case 1:
			this.majorVersion = parts[0];
			break;
		case 2:
			this.majorVersion = parts[0];
			this.minorVersion = parts[1];
			break;
		case 3:
			this.majorVersion = parts[0];
			this.minorVersion = parts[1];
			this.patchVersion = parts[2];
			break;
		default:
			throw new CalabashException(String.format(
					"'%s' is not a valid version string", versionString));
		}
	}

	public String major() {
		return majorVersion;
	}

	public String minor() {
		return minorVersion;
	}

	public String patch() {
		return patchVersion;
	}

}
