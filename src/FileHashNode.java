import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FileHashNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String filePath = null;
	private String hash = null;
	private List<FileHashNode> children = new ArrayList<>();
	private FileHashNode parent = null;

	public FileHashNode(String filePath) {
		this.filePath = filePath;
	}

	public FileHashNode addChild(FileHashNode child) {
		child.setParent(this);
		this.children.add(child);
		return child;
	}

	public void addChildren(List<FileHashNode> children) {
		children.forEach(each -> each.setParent(this));
		this.children.addAll(children);
	}

	public List<FileHashNode> getChildren() {
		return children;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	private void setParent(FileHashNode parent) {
		this.parent = parent;
	}

	public FileHashNode getParent() {
		return parent;
	}

	public String toString() {
		return filePath + "->" + hash;
	}

}