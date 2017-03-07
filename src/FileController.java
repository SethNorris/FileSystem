import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FileController {

	final int FILE_METADATA_SIZE = 20;
	RandomAccessFile raf; 
	int firstOpenPapa;
	int firstOpenByte;

	public FileController(){
		try {
			raf = new RandomAccessFile("filesystem.txt", "rw");
			if(raf.length() == 0){
				raf.write(ByteConverter.intToByteArray(4));
				raf.write(ByteConverter.intToByteArray(4));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	};

	public void writeToPapa(int key){
		adjustStuffUnderPapa();
		byte[] tempArray = new byte[2];
		try {
			raf.seek(0);
			raf.read(tempArray, 0, 2);
			firstOpenPapa = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			raf.seek(2);
			raf.read(tempArray,0,2);
			firstOpenByte = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			byte[] papa = new byte[firstOpenPapa];
			byte[] restOfFile = new byte[(int) (raf.length() - firstOpenPapa)];
			raf.seek(0);
			raf.read(papa, 0, firstOpenPapa);
			raf.seek(firstOpenPapa);
			raf.readFully(restOfFile);
			raf.setLength(0);
			raf.write(papa);
			raf.write(intToByteArray(key));
			raf.write(ByteConverter.intToByteArray(firstOpenByte + 6));
			raf.write(restOfFile);
			raf.seek(0);
			byte[] newFirstOpenPapa = ByteConverter.intToByteArray(firstOpenPapa + 6);
			byte[] newFirstOpenByte = ByteConverter.intToByteArray(firstOpenByte + 6);
			raf.write(newFirstOpenPapa);
			raf.seek(2);
			raf.write(newFirstOpenByte);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeFile(String name,byte[] contents, int security){
		String[] nameAndExtension = name.split(Pattern.quote("."));
		byte[] nameByte = nameAndExtension[0].getBytes();
		byte[] extensionByte = nameAndExtension[1].getBytes();
		byte[] tempArray = new byte[2];
		int metaDataLength = FILE_METADATA_SIZE + nameByte.length + extensionByte.length;

		try {
			writeToPapa(name.hashCode());
			raf.seek(2);
			raf.read(tempArray,0,2);
			firstOpenByte = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			raf.seek(firstOpenByte);
			raf.write(intToByteArray(metaDataLength));
			raf.write(intToByteArray(nameByte.length));
			raf.write(nameByte);
			raf.write(intToByteArray(extensionByte.length));
			raf.write(extensionByte);
			raf.write(ByteConverter.intToByteArray(contents.length));
			int start = (int)raf.getFilePointer();
			raf.write(ByteConverter.intToByteArray(start));
			raf.write(ByteConverter.intToByteArray(start + contents.length));
			raf.write(ByteConverter.intToByteArray(security));
			raf.write(contents);
			raf.seek(2);
			raf.write(ByteConverter.intToByteArray(firstOpenByte + metaDataLength + contents.length));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeFolder(){

	}

	public void readFile(String name){
		byte[] tempArray = new byte[2];

		try {
			raf.seek(0);
			raf.read(tempArray, 0, 2);
			firstOpenPapa = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			System.out.println("READ FILE FIRST OPEN PAPA: " + firstOpenPapa);
			raf.seek(4);
			byte[] papa = new byte[firstOpenPapa - 4];
			raf.read(papa, 0, firstOpenPapa - 4);
			Map<Integer,Integer> dictionary = new HashMap<Integer,Integer>();
			int i =  0;
			while(i < papa.length){
				byte[] key = new byte[4];
				byte[] value = new byte[2];
				key[0] = papa[i];
				key[1] = papa[i + 1];
				key[2] = papa[i + 2];
				key[3] = papa[i + 3];
				value[0] = papa[i + 4];
				value[1] = papa[i + 5];
				dictionary.put(byteArrayToInt(key), ByteConverter.byteArrayToInt(value[0], value[1]));
				i += 6;
			}
			for(int k : dictionary.keySet()){
				System.out.println(k);
			}
			retrieveFile(dictionary.get(name.hashCode()));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void adjustStuffUnderPapa(){
		byte[] tempArray = new byte[2];

		try {
			raf.seek(0);
			raf.read(tempArray, 0, 2);
			firstOpenPapa = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			raf.seek(4);
			byte[] papa = new byte[firstOpenPapa - 4];
			raf.read(papa, 0, firstOpenPapa - 4);
			Map<Integer,Integer> dictionary = new HashMap<Integer,Integer>();
			int i =  0;
			while(i < papa.length){
				byte[] key = new byte[4];
				byte[] value = new byte[2];
				key[0] = papa[i];
				key[1] = papa[i + 1];
				key[2] = papa[i + 2];
				key[3] = papa[i + 3];
				value[0] = papa[i + 4];
				value[1] = papa[i + 5];
				dictionary.put(byteArrayToInt(key), ByteConverter.byteArrayToInt(value[0], value[1]));
				if(i < papa.length - 5){
					System.out.println("CALLING THIS THING");
					byte[] temptemp = ByteConverter.intToByteArray((ByteConverter.byteArrayToInt(value[0], value[1])) + 6);
					papa[i + 4] = temptemp[0];
					papa[i + 5] = temptemp[1];
				}
				i += 6;
				raf.seek(4);
				raf.write(papa);
			}
		for(int key : dictionary.keySet()){
			int loc = dictionary.get(key);
			raf.seek(loc);
			byte[] metaLengthArray = new byte[4];
			raf.readFully(metaLengthArray, 0, 4);
			int metaLength = byteArrayToInt(metaLengthArray);
			raf.seek((loc+metaLength-4));
			raf.readFully(tempArray,0,2);
			int end = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			raf.seek(loc+metaLength-6);
			raf.readFully(tempArray,0,2);
			int start = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			raf.seek(loc+metaLength-4);
			raf.write(ByteConverter.intToByteArray(end + 6), 0, 2);
			raf.seek(loc+metaLength-6);
			raf.write(ByteConverter.intToByteArray(start + 6), 0, 2);
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void retrieveFile(int loc) throws IOException{
		raf.seek(loc);
		System.out.println(loc);
		byte[] tempArray = new byte[4];
		raf.readFully(tempArray, 0, 4);
		int metaLength = byteArrayToInt(tempArray);
		System.out.println("metLength: " + metaLength);
		byte[] meta = new byte[metaLength - 4];
		raf.read(meta,0,metaLength - 4);
		int nameLength = byteArrayToInt(new byte[]{meta[0],meta[1],meta[2],meta[3]});
		System.out.println(nameLength);
	}

	public void readFolder(){

	}

	public void printInfo(){
		byte[] tempArray = new byte[2];
		try {
			raf.seek(0);
			raf.read(tempArray, 0, 2);
			firstOpenPapa = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
			raf.seek(2);
			raf.read(tempArray,0,2);
			firstOpenByte = ByteConverter.byteArrayToInt(tempArray[0], tempArray[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("papa: " + firstOpenPapa);
		System.out.println("byte: " + firstOpenByte);

	}

	public static int byteArrayToInt(byte[] b) {
	    if (b.length == 4)
	      return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
	          | (b[3] & 0xff);
	    else if (b.length == 2)
	      return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);

	    return 0;
	  }
	
	public static byte[] intToByteArray(int i){
		byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
		return bytes;
	}
}
