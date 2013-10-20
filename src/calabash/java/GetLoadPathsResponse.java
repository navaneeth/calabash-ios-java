package calabash.java;

import java.util.List;

class GetLoadPathsResponse extends Response {
	private static final long serialVersionUID = -7001653582480568128L;
	public List<String> loadPaths;

	public GetLoadPathsResponse() {
	}

	public GetLoadPathsResponse(String requestId, List<String> loadPaths) {
		super(requestId);
		this.loadPaths = loadPaths;
	}
}