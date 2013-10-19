package calabash.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class AddLoadPathRequest extends Request {
	private static final long serialVersionUID = -8572541727990523058L;
	public List<String> loadPath;

	public AddLoadPathRequest(Collection<? extends String> c) {
		this.loadPath = new ArrayList<String>(c);
	}
}