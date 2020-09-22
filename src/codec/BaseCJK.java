package codec;

public class BaseCJK {
	
	public static final short DEFAULT_DELTA = 0xEEE;
	public static final short MIN_RECOMMENED_DELTA = 0xE00;
	public static final short MAX_RECOMMENED_DELTA = 0xFBF;
	
	
	public static char[] encodeToChar(byte src[]) {
		return encodeToChar(DEFAULT_DELTA, src, src.length);
	}
	
	
	public static char[] encodeToChar(short delta, byte src[]) {
		return encodeToChar(delta, src, src.length);
	}
	
	
	public static char[] encodeToChar(short delta, byte src[], int encodeLength) {
		char str[] = encodeToChar(src, encodeLength);
		
		for(int i = str.length - 1; i >= 0; i--) {
			str[i] = (char) (str[i] + delta);
		}
		
		return str;
	}
	
	/*
	 * encodeLength > 0
	 */
	public static char[] encodeToChar(byte src[], int encodeLength) {
		int strSize = (encodeLength - 1) / 7 + encodeLength + 1;
		
		char str[] = new char[(strSize + 1) / 2];
		for(int srcPos = (encodeLength / 7) * 7, strPos = (encodeLength / 7) * 4; srcPos > 0; srcPos -= 7, strPos -= 4) {
			str[strPos - 1] = (char) ( (((src[srcPos - 1] & 0xFF) | (src[srcPos - 2] & 0xFF) << 8) & 0x3FFF) | 0x4000 );
			str[strPos - 2] = (char) ( (((src[srcPos - 2] & 0xFF) >>> 6 | (src[srcPos - 3] & 0xFF) << 2 | (src[srcPos - 4] & 0xFF) << 10) & 0x3FFF) | 0x4000 );
			str[strPos - 3] = (char) ( (((src[srcPos - 4] & 0xFF) >>> 4 | (src[srcPos - 5] & 0xFF) << 4 | (src[srcPos - 6] & 0xFF) << 12) & 0x3FFF) | 0x4000 );
			str[strPos - 4] = (char) ( (((src[srcPos - 6] & 0xFF) >>> 2 | (src[srcPos - 7] & 0xFF) << 6) & 0x3FFF) | 0x4000 );
		}
		
		encodeRemainder(src, encodeLength, encodeLength % 7, str);
		return str;
	}
	
	public static void encodeRemainder(byte src[], int encodeLength, int remainder, char str[]) {
		int strN = (remainder + 2) / 2;
		switch(remainder) {
			case 6: str[str.length + 3 - strN] = (char) ( (((src[encodeLength - 1] & 0xFF) << 6) & 0xFFF) | 0x8000 );
					str[str.length + 2 - strN] = (char) ( (((src[encodeLength - 1] & 0xFF) >>> 6) & 0x3FFF) | 0x4000 );
			case 5: str[str.length + 2 - strN] |= ( (((src[encodeLength + 4 - remainder] & 0xFF) << 2) & 0x3FFF) | 0x4000 );
			case 4: str[str.length + 2 - strN] |=  remainder == 4 ?  ( (((src[encodeLength + 3 - remainder] & 0xFF) << 8) & 0xFFF) | 0x8000 ) : ( (((src[encodeLength + 3 - remainder] & 0xFF) << 10) & 0x3FFF) | 0x4000 );
					str[str.length + 1 - strN] = (char) ( (((src[encodeLength + 3 - remainder] & 0xFF) >>> 4) & 0x3FFF) | 0x4000 );
			case 3: str[str.length + 1 - strN] |= ( (((src[encodeLength + 2 - remainder] & 0xFF) << 4) & 0x3FFF) | 0x4000 );
			case 2: str[str.length + 1 - strN] |= remainder == 2 ? ( (((src[encodeLength + 1 - remainder] & 0xFF) << 10) & 0xFFF) | 0x8000 ) : ( (((src[encodeLength + 1 - remainder] & 0xFF) << 12) & 0x3FFF) | 0x4000 );
					str[str.length - strN] = (char) ( (((src[encodeLength + 1 - remainder] & 0xFF) >>> 2) & 0x3FFF) | 0x4000 );
			case 1: str[str.length - strN] |= ( (((src[encodeLength - remainder] & 0xFF) << 6) & 0x3FFF) | 0x4000 );
			default: break;
		}
	}
	
	
	public static byte[] decodeToByte(char str[]) {
		return decodeToByte(DEFAULT_DELTA, str, str.length);
	}
	
	
	public static byte[] decodeToByte(short delta, char str[]) {
		return decodeToByte(DEFAULT_DELTA, str, str.length);
	}
	
	
	public static byte[] decodeToByte(short delta, char str[], int decodeLength) {
		for(int i = str.length - 1; i >= 0; i--) {
			str[i] = (char) (str[i] - delta);
		}
		return decodeToByte(str, decodeLength);
	}
	
	/*
	 * decodeLength > 0
	 */
	public static byte[] decodeToByte(char str[], int decodeLength) {
		int bytesSize = decodeLength * 2 - (decodeLength - 1) / 4 - ((str[decodeLength - 1] & 0xFFFF) >>> 14);
		
		int batchNum = (decodeLength - 1) / 4;
		byte bytes[] = new byte[bytesSize];
		for(int strPos = batchNum * 4, bytePos = batchNum * 7 ; bytePos > 0; strPos -= 4, bytePos -= 7) {
			bytes[bytePos - 1] = (byte) ( str[strPos - 1] );
			bytes[bytePos - 2] = (byte) ( (str[strPos - 1] & 0x3FFF) >>> 8 | str[strPos - 2] << 6 );
			bytes[bytePos - 3] = (byte) ( str[strPos - 2] >>> 2 );
			bytes[bytePos - 4] = (byte) ( (str[strPos - 2] & 0x3FFF) >>> 10 | str[strPos - 3] << 4 );
			bytes[bytePos - 5] = (byte) ( str[strPos - 3] >>> 4 );
			bytes[bytePos - 6] = (byte) ( (str[strPos - 3] & 0x3FFF) >>> 12 | str[strPos - 4] << 2 );
			bytes[bytePos - 7] = (byte) ( str[strPos - 4] >>> 6 );
		}
		
		decodeRemainder(str, decodeLength, bytesSize - batchNum * 7, bytes);
		
		return bytes;
	}
	
	
	public static void decodeRemainder(char str[], int decodeLength, int remainder, byte bytes[]) {
		int strNum = remainder / 2;
		switch(remainder) {
			case 7: bytes[bytes.length - 1] = (byte) ( str[decodeLength - 1] );
			case 6: bytes[bytes.length + 5 - remainder] = (byte) ( (remainder == 6 ? ((str[decodeLength - 1] & 0xFFF) >>> 6) : ((str[decodeLength - 1] & 0x3FFF) >>> 8)) | str[decodeLength - 2] << 6 );
			case 5: bytes[bytes.length + 4 - remainder] = (byte) ( str[decodeLength + 1 - strNum] >>> 2 );
			case 4: bytes[bytes.length + 3 - remainder] = (byte) ((remainder == 4 ? ((str[decodeLength + 1 - strNum] & 0xFFF) >>> 8) : ((str[decodeLength + 1 - strNum] & 0x3FFF) >>> 10)) | str[decodeLength - strNum] << 4);
			case 3: bytes[bytes.length + 2 - remainder] = (byte) ( str[decodeLength - strNum] >>> 4 );
			case 2: bytes[bytes.length + 1 - remainder] = (byte) ( (remainder == 2 ? ((str[decodeLength - strNum] & 0xFFF) >>> 10) : ((str[decodeLength - strNum] & 0x3FFF) >>> 12)) | str[decodeLength - 1 - strNum] << 2 );
			case 1: bytes[bytes.length - remainder] = (byte) (str[decodeLength - 1 - strNum] >>> 6);
			default: break;
		}
	}
	
}
