package calabash.java;

class PutRequest extends Request {
	private static final long serialVersionUID = 3337038131308201283L;
	public String key;
	public Object value;

	public PutRequest(String key, Object value) {
		this.key = key;
		this.value = value;
	}
}