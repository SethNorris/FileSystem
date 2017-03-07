import java.nio.ByteBuffer;

public class Main {
	
	public static void main(String[] args) {
		FileController fc = new FileController();
		
		
		
		fc.writeFile("testFile.txt", intToByteArray(2015), 1);
		fc.writeFile("testFile2.txt", intToByteArray(2669), 1);
		fc.writeFile("testFil.txt", intToByteArray(2669), 1);
		fc.writeFile("testFilehosadsad2.txt", intToByteArray(2669), 1);
		fc.writeFile("testFilesadfsadfjasdjfsadifasd2.txt", intToByteArray(2669), 1);
		
//		fc.printInfo();
		fc.readFile("testFile.txt");
		fc.readFile("testFile2.txt");
		fc.readFile("testFilehosadsad2.txt");
//		fc.readFile("testFile.txt");
	}
	
	public static byte[] intToByteArray(int i){
		byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
		return bytes;
	}

}
