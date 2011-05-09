package android.bluebox.model;

public class IdentityItem {
	
	private int id;
	private String name = "";
	private String tagList = "";
	private String workspaceList = "";
	
	private String encryptedName = "";
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setTagList(String TagList) {
		this.tagList = TagList;
	}
	
	public String getTagList() {
		return tagList;
	}
	
	public void setWorkspaceList(String workspaceList) {
		this.workspaceList = workspaceList;
	}
	
	public String getWorkspaceList() {
		return workspaceList;
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
