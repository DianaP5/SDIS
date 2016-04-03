package testApp;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;

import javax.imageio.ImageIO;

import logic.Chunks;
import logic.FileSys;
import logic.Message;
import service.MDBackup;
import service.MessageHandler;
import util.HashFile;

public class Start {

	private static String peerId = "1";
	private static String msgType = "PUTCHUNK";
	private static String version = "1.0";
	private static String op1 = "C:\\Users\\Ricardo\\Desktop\\cars.txt";
	private static String op2 = "1";

	public static void main(String[] args) throws IOException,
			NoSuchAlgorithmException, InterruptedException {

		/*
		 * if (args.length < 3 || args.length > 4)
		 * System.out.println("Error: Ivalid number of arguments [3,4] :"
		 * +args.length); else{ peerId=args[0]; msgType=args[1]; op1=args[2];
		 * op2=args[3]; }
		 */

		// String
		// header=msgType+" "+peerId+" "+op1+" "+op2+" "+Message.CRLF+Message.CRLF;
		String header = "120.210.02:8888 BACKUP C:\\Users\\Ricardo\\Desktop\\cars.txt 1";
		//String header = "120.210.02:8888 RESTORE C:\\Users\\Ricardo\\Desktop\\cars.txt";
		//String header = "120.210.02:8888 DELETE C:\\Users\\Ricardo\\Desktop\\cars.txt";
		//TODO receber o nome do ficheiro originar-> guardar em logs
		
		//java TestApp 1923 RESTORE test1.pdf
		//java TestApp 1923 DELETE test1.pdf
		//[B@566950f9
		
		ClientTCP c1=new ClientTCP(header);
		//MessageHandler.ImageSplitTest();
		// java TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>
		//BACKUP, RESTORE, DELETE, RECLAIM
		//restore();
	}

	public static void splitFile(FileSys file) throws IOException {
		int counter = 0;
		int eachFileSize = 1000 * 64; // 64Kb

		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(op1))) {

			int tmp = 0;
			File f1 = new File(op1);
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

	public static void copyFile(String source, String destination)
			throws IOException {

		Path src = Paths.get(source);
		Path dest = Paths.get(destination);

		// src.getFileName();
		// System.out.println(s.getFileName());
		// relative
		// Path
		// src=Paths.get(System.getProperty("user.dir")+"\\Files\\cars.txt");

		Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);

	}

	public static void readFiles() {

		Charset charset = Charset.forName("UTF-8 ");// "ISO-8859-1");//"US-ASCII");
		// Path path=(Path) FileSystems.getFileSystem(URI.create(op1));
		Path path = Paths.get(op1);

		try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
	}

	// merge splited files
	public static void restore() throws IOException {
		int nParts = getNumberParts();
		System.out.println(nParts);

		File f1 = new File(System.getProperty("user.dir")
				+ "\\Resources\\Restored\\restaured.png");

		BufferedOutputStream out = new BufferedOutputStream(
				new FileOutputStream(f1));
		for (int part = 0; part < nParts; part++) {
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(System.getProperty("user.dir")
							+ "\\Resources\\Backup\\" + "new " + part + ".bak"));
			int b;
			while ((b = in.read()) != -1)
				out.write(b);

			in.close();
		}
		out.close();
	}

	private static int getNumberParts() throws IOException {
		File directory = new File(System.getProperty("user.dir")
				+ "\\Resources\\Backup");

		String[] matchingFiles = directory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.matches("new \\d+\\.bak");
			}
		});

		return matchingFiles.length;
	}

	public void mergeImg() throws IOException {
		int rows = 2; // we assume the no. of rows and cols are known and each
						// chunk has equal width and height
		int cols = 2;
		int chunks = rows * cols;

		int chunkWidth, chunkHeight;
		int type;
		// fetching image files
		File[] imgFiles = new File[chunks];
		for (int i = 0; i < chunks; i++) {
			imgFiles[i] = new File("archi" + i + ".jpg");
		}

		// creating a bufferd image array from image files
		BufferedImage[] buffImages = new BufferedImage[chunks];
		for (int i = 0; i < chunks; i++) {
			buffImages[i] = ImageIO.read(imgFiles[i]);
		}
		type = buffImages[0].getType();
		chunkWidth = buffImages[0].getWidth();
		chunkHeight = buffImages[0].getHeight();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(chunkWidth * cols,
				chunkHeight * rows, type);

		int num = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				finalImg.createGraphics().drawImage(buffImages[num],
						chunkWidth * j, chunkHeight * i, null);
				num++;
			}
		}
		System.out.println("Image concatenated.....");
		ImageIO.write(finalImg, "jpeg", new File("finalImg.jpg"));
	}
}
