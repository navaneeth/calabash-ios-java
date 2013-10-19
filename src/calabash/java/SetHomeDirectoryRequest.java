package calabash.java;

class SetHomeDirectoryRequest extends Request {
	private static final long serialVersionUID = -3877240401760381576L;
	public String homeDirectory;
	
	public SetHomeDirectoryRequest() {
	}

	public SetHomeDirectoryRequest(String homeDirectory) {
		this.homeDirectory = homeDirectory;
	}
}