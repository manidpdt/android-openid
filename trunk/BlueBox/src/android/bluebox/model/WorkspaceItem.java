package android.bluebox.model;

public class WorkspaceItem {
	
	private int id;
	private String name = "";
	private String accuracy = "";
	private String lastVisit = "";
	
	private String encryptedName = "";
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	
	public String getAccuracy() {
		return accuracy;
	}
	
	public void setLastVisit(String lastVisit) {
		this.lastVisit = lastVisit;
	}
	
	public String getLastVisit() {
		return lastVisit;
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
