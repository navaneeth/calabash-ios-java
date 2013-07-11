/**
 * 
 */
package calabash.java;

/**
 * 
 *
 */
public enum SpecialKeys {
	Shift("Shift"), Delete("Delete"), Return("Return"), International(
			"International"), More("More"), Exclamation("!"), QuestionMark("?"), Dictation(
			"Dictation");

	private String keyName;

	SpecialKeys(String keyName) {
		this.keyName = keyName;
	}

	public String getKeyName() {
		return keyName;
	}

}
