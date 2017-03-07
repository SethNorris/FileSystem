
public class ByteConverter {

	public static byte[] intToByteArray(int value) {
		byte[] bytes = new byte[2];
		String bitString = Integer.toBinaryString(value);
		String emptyString = "";
		for(int i = 0; i < 16 - bitString.length(); i++){
			emptyString += 0;
		}
		String finalString = emptyString + bitString;
		String finalStringOne = finalString.substring(0, 8);
		String finalStringTwo = finalString.substring(8, 16);
		bytes[0] = (byte) Integer.parseInt(finalStringOne, 2);
		bytes[1] = (byte)Integer.parseInt(finalStringTwo,2);
		return bytes;
	}

	public static int byteArrayToInt(byte x, byte y){
		int first = x;
		int second = y;
		String firstString = addZeros(Integer.toBinaryString(first & 0xff));
		String secondString = addZeros(Integer.toBinaryString(second & 0xff));
		String finalString = firstString + secondString;

		return Integer.parseInt(finalString, 2);
	}

	private static String addZeros(String thing){
		String emptyString = "";
		for(int i = 0; i < 8 - thing.length(); i++){
			emptyString += 0;
		}
		return emptyString + thing;
	}
	
}
