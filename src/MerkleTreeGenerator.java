import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MerkleTreeGenerator extends SimpleFileVisitor<Path> {
	private static final String HASHING_ALGORITHM = "SHA-256";

	private int nodesVisited;
	private FileHashNode rootNode;
	private FileHashNode currentNode;
	private Path rootDir;
	private MessageDigest digest;

	public MerkleTreeGenerator(final Path rootDir) throws NoSuchAlgorithmException {
		this.rootDir = rootDir;
		rootNode = new FileHashNode(rootDir.toString());
		currentNode = rootNode;
		digest = MessageDigest.getInstance(HASHING_ALGORITHM);
	}

	// Prints the total number of
	// matches to standard out.
	void done() {
		System.out.println("Total nodes visited: " + nodesVisited);
		printTree(rootNode, " ");
	}

	// Invoke the pattern matching
	// method on each file.
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		FileHashNode newNode = new FileHashNode(file.toString());

		// create hash for this file
		String fileHash = getHashForFileContent(file);
		newNode.setHash(fileHash);
		currentNode.addChild(newNode);
		nodesVisited++;
		return CONTINUE;
	}

	// Invoke the pattern matching
	// method on each directory.
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
		if (!dir.equals(rootDir)) {
			FileHashNode newNode = new FileHashNode(dir.toString());
			FileHashNode child = currentNode.addChild(newNode);
			currentNode = child;
		}
		nodesVisited++;
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		List<FileHashNode> children = currentNode.getChildren();
		if (children.isEmpty()) {
			currentNode.setHash(getHashForString(currentNode.getFilePath()));
		} else {
			List<String> hashList = new ArrayList<String>();
			for (FileHashNode child : children) {
				String hash = child.getHash();
				hashList.add(hash);
			}
			Collections.sort(hashList);
			String combinedHash = hashList.stream().map(hash -> hash.toString()).collect(Collectors.joining(""));
			currentNode.setHash(getHashForString(combinedHash));
		}
		currentNode = currentNode.getParent();
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return CONTINUE;
	}

	private String getHashForString(String strObject) {
		byte[] bytes = digest.digest(strObject.getBytes());

		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		// return complete hash
		return sb.toString();
	}

	private String getHashForFileContent(Path path) throws IOException {
		File file = path.toFile();
		// Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);

		// Create byte array to read data in chunks
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;

		// Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}
		;

		// close the stream; We don't need it now.
		fis.close();

		// Get the hash's bytes
		byte[] bytes = digest.digest();

		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		// return complete hash
		return sb.toString();
	}

	public void exportResult(Path exportFilePath) {
		try {
			// Saving of object in a file
			FileOutputStream fos = new FileOutputStream(exportFilePath.toFile());
			ObjectOutputStream out = new ObjectOutputStream(fos);

			// Method for serialization of object
			out.writeObject(rootNode);

			out.close();
			fos.close();
		}

		catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static void printTree(FileHashNode node, String appender) {
		System.out.println(appender + node);
		node.getChildren().forEach(each -> printTree(each, appender + "  "));
	}
	
	public static void main(String[] args) throws Exception {
		
		//TODO: remove this
		args = new String[2];
		args[0] = "D:\\test3";
		args[1] = "D:\\output";
		
		if (args.length != 2) {
			System.out.println("Usages: MerkleTreeGenerator <root dir> <export dir>");
			System.exit(0);
		}

		String rootDir = args[0];
		String exportDir = args[1];
		Path exportFilePath = Paths.get(exportDir, "merkle_hash_tree_" + System.currentTimeMillis() + ".ser");
		System.out.println("Generating merkle tree for root dir: " + rootDir);

		Path startingDir = Paths.get(rootDir);
		MerkleTreeGenerator generator = new MerkleTreeGenerator(startingDir);
		Files.walkFileTree(startingDir, generator);
		generator.done();
		generator.exportResult(exportFilePath);
		System.out.println("Exported the result to: " + exportFilePath);
	}
}