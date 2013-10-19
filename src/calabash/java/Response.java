package calabash.java;

import java.io.Serializable;
import java.util.UUID;

class Response implements Serializable {
	private static final long serialVersionUID = 1654428568551188101L;
	public String responseId;
	public String requestId;
	
	public Response() {
	}

	public Response(String requestId) {
		this.requestId = requestId;
		responseId = UUID.randomUUID().toString();
	}
}