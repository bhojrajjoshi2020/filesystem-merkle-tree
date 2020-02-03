import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileHashNode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String filePath = null;
	private String hash = null;
	private List<FileHashNode> children = new ArrayList<>();
	private FileHashNode parent = null;

	private FileHashNode() {
	}

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

	@XmlAttribute
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@XmlElement
	public List<FileHashNode> getChildren() {
		return children;
	}

	@XmlAttribute
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public FileHashNode getParent() {
		return parent;
	}

	private void setParent(FileHashNode parent) {
		this.parent = parent;
	}

	public String toString() {
		return filePath + "->" + hash;
	}

}