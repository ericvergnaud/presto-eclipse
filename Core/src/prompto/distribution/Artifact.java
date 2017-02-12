package prompto.distribution;

public class Artifact {
	String groupId;
	String artifactId;
	String version;
	String type;
	
	public Artifact(String groupId, String artifactId, String version, String type) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.type = type;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getVersion() {
		return version;
	}

	public String getType() {
		return type;
	}

	public Artifact withType(String type) {
		return new Artifact(groupId, artifactId, version, type);
	}
	
	

}
