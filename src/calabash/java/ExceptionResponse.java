package calabash.java;

class ExceptionResponse extends Response {
	private static final long serialVersionUID = 8949922243139635015L;
	public String error;

	public ExceptionResponse(String requestId, Throwable error) {
		super(requestId);
		this.error = error.getMessage();
	}
}