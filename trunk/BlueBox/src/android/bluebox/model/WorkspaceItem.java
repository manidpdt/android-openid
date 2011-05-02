package android.bluebox.model;

public class WorkspaceItem {
	
	private String name = "";
	private String accuracy = "";
	private String lastVisit = "";
	
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
}
