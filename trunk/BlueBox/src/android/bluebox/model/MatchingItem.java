package android.bluebox.model;

public class MatchingItem {
	
	private int id;
	private String name = "";
	private String identity = "";
	private String value = "";
	
	private String encryptedName = "";
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setEncryptedName(String encryptedName) {
		this.encryptedName = encryptedName;
	}
	
	public String getEncryptedName() {
		return encryptedName;
	}
}
