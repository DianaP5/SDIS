package service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;

import util.HashFile;
import logic.Chunks;
import logic.FileSys;
import logic.Message;

public class MessageHandler {
	// java TestApps <peer_ap> <sub_protocol> <opnd_1> <opnd_2>
	private String peerId;
	private String msgType;
	private String filePath;
	private String degree;
	private String version = "1.0";

	public MessageHandler(String message) throws IOException,
			NoSuchAlgorithmException, InterruptedException {

		this.peerId = message.split(" ")[0].split(":")[1];
		this.msgType = message.split(" ")[1];
		this.filePath = message.split(" ")[2];
		this.degree = message.split(" ")[3];

		switch (msgType) {
		case "PUTCHUNK":
			putChunkHandler();
			break;
		case "GETCHUNK":
			System.out.println("1");
			break;
		case "STORED":
			// storedChunkHandler(msg);
			break;
		case "CHUNK":
			System.out.println("1");
			break;
		case "DELETE":
			System.out.println("1");
			break;
		case "REMOVED":
			System.out.println("1");
			break;
		default:
			System.out.println("Error: Wrong MessageType argument:" + msgType);
			break;
		}
	}

	private void storedChunkHandler(Message msg) throws IOException {
		MessageControl mc1 = new MessageControl(msg);
		new Thread(mc1).start();
	}

	private void putChunkHandler() throws IOException, InterruptedException,
			NoSuchAlgorithmException {
		FileSys f1 = createFile(peerId, filePath, degree);
		splitFile(f1);

		int numberChunks = f1.getChunksList().size();
		int i = 0;

		while (i < numberChunks) {
			// PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo>
			// <ReplicationDeg> <CRLF><CRLF><Body>
			Chunks c1 = f1.getChunksList().get(i);
			String header = msgType + " " + version + " " + peerId + " "
					+ f1.getId() + " " + c1.getNumber() + " " + degree + " ";
			Message m1 = new Message(header, c1.getContent());

			MDBackup b1 = new MDBackup(m1, Integer.parseInt(degree));
			 System.out.println("Nova thread chunk " + c1.getNumber() +
			 " "
			 + c1.getContent().toString());
			new Thread(b1).start();
			Thread.sleep(1000);

			i++;
		}
	}

	private static FileSys createFile(String peerId, String path, String degree)
			throws IOException, NoSuchAlgorithmException {

		Path filePath = Paths.get(path);

		String lastModified = Long.toString(new File(path).lastModified());
		String fileName = filePath.getFileName().toString();
		String fileCreationTime = Files
				.readAttributes(filePath, BasicFileAttributes.class)
				.creationTime().toString();

		HashFile h1 = new HashFile(fileName + " " + path + " "
				+ fileCreationTime + " " + lastModified);
		String hash = h1.getHash().toString();

		FileSys f1 = new FileSys(Integer.parseInt(peerId), hash,
				Integer.parseInt(degree));

		return f1;
	}

	public void splitFile(FileSys file) throws IOException {
		int counter = 0;
		int eachFileSize = 1000 * 64; // 64Kb

		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(filePath))) {

			@SuppressWarnings("unused")
			int tmp = 0;
			File f1 = new File(filePath);
			long actualFileSize = f1.length();
			int nChunks = 0;

			if (actualFileSize < eachFileSize)
				eachFileSize = (int) actualFileSize;
			else {
				double times = actualFileSize % eachFileSize;
				nChunks = (int) Math.floor(times);
			}

			byte[] buffer = new byte[eachFileSize];

			while ((tmp = bis.read(buffer)) > 0) {
				// File newFile = new File(f1.getParent(), "new" +
				// "."+counter+".txt");
				String s1 = new String(buffer, 0, buffer.length);

				if (nChunks > 0 && (counter + 1 >= nChunks)) {
					int lastChunkSize = (int) (actualFileSize - ((nChunks - 1) * eachFileSize));
					s1 = new String(buffer, 0, lastChunkSize);
				}

				Chunks c1 = new Chunks(file.getId(), counter, s1);
				file.addChunk(c1);
				counter++;
				System.out.println("File part " + counter + ": " + s1);
				/*
				 * try (FileOutputStream out = new FileOutputStream(newFile)) {
				 * out.write(s1.getBytes(), 0,10);//tmp is chunk size }
				 */
			}
		}
	}

}
