
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MerkleTreeComparator {

	public MerkleTreeComparator() {

	}

	public FileHashNode deserialize(String filePath) {

		FileHashNode fileHashNode = null;

		// Deserialization
		try {
			// Reading the object from a file
			FileInputStream file = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(file);

			// Method for deserialization of object
			fileHashNode = (FileHashNode) in.readObject();

			in.close();
			file.close();
			System.out.println(filePath + " has been deserialized.");
		}

		catch (IOException ex) {
			ex.printStackTrace();
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
			ex.printStackTrace();
		}

		return fileHashNode;
	}

	public List<FileHashNode> compareTrees(FileHashNode baseNode, FileHashNode anotherNode, List<FileHashNode> faultyNodes) {
		if (!baseNode.getHash().equals(anotherNode.getHash())) {
			Iterator<FileHashNode> baseItr = baseNode.getChildren().iterator();
			Iterator<FileHashNode> anotherItr = anotherNode.getChildren().iterator();

			while (baseItr.hasNext() && anotherItr.hasNext()) {
				FileHashNode baseNext = baseItr.next();
				FileHashNode anotherNext = anotherItr.next();
				if (!baseNext.getHash().equals(anotherNext.getHash())) {
					faultyNodes.add(baseNext);
				}
			}
		}

		return faultyNodes;
	}

	public static void main(String[] args) {
		
		//TODO: remove this
//		args = new String[2];
//		args[0] = "D:\\output\\merkle_hash_tree_1580619092037.ser";
//		args[1] = "D:\\output\\merkle_hash_tree_1580622712427.ser";
		
		if (args.length != 2) {
			System.out.println("Usage: MerkleTreeComparator <base object file path> <another object file path>");
			System.exit(0);
		}

		String baseFilePath = args[0];
		String anotherFilePath = args[1];

		MerkleTreeComparator comparator = new MerkleTreeComparator();
		FileHashNode baseTree = comparator.deserialize(baseFilePath);
		FileHashNode anotherNode = comparator.deserialize(anotherFilePath);
		List<FileHashNode> faultyNodes = new ArrayList<FileHashNode>();
		comparator.compareTrees(baseTree, anotherNode, faultyNodes);

		if (faultyNodes.isEmpty()) {
			System.out.println("Both objects are equal!");
		} else {
			System.out.println("Below are the faulty nodes:");
			for (FileHashNode faultyNode : faultyNodes) {
				System.out.println(faultyNode.toString());
			}
		}
	}
}
