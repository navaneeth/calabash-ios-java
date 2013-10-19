package calabash.java;

class RunScriptletResponse extends Response {
	private static final long serialVersionUID = -1340817688151164183L;
	public Object data;
	
	public RunScriptletResponse() {
	}

	public RunScriptletResponse(String requestId, Object data) {
		super(requestId);
		this.data = data;
	}
}