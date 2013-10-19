package calabash.java;

import java.io.Serializable;
import java.util.UUID;

public abstract class Request implements Serializable {
	private static final long serialVersionUID = 867883264433337973L;
	public String requestId;

	public Request() {
		this.requestId = UUID.randomUUID().toString();
	}
}