package test;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import codec.BaseCJK;

public class Test1 {
	
	public static void main(String args[]) {
		test1();
		test2();
	}
	
	
	public static void test1() {
		String str[] = new String[] {"q","we","rty","uiop","asdfg","hjklzx","cvbnm12","34567890",")(*&^%$#@","!MNBVCXZLK"};
		byte bytes[][] = new byte[str.length][];
		
		for(int i = str.length - 1; i >= 0; i--) {
			bytes[i] = str[i].getBytes();
		}
		
		char encoded[][] = new char[str.length][];
		for(int i = str.length - 1; i >= 0; i--) {
			encoded[i] = BaseCJK.encodeToChar(bytes[i]);
		}
		
		for(char c[] : encoded) {
			System.out.println(new String(c));
		}
		
		byte decoded[][] = new byte[str.length][];
		for(int i = str.length - 1; i >= 0; i--) {
			decoded[i] = BaseCJK.decodeToByte(encoded[i]);
		}
		
		for(int i = str.length - 1; i >= 0; i--) {
			System.out.println(new String(decoded[i]).equals(str[i]));
		}
		
	}
	
	
	public static void test2() {
		KeyPairGenerator generator = null;
		
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		generator.initialize(4096);
		KeyPair pair = generator.generateKeyPair();
		
		PublicKey publicKey = pair.getPublic();
		PrivateKey privateKey = pair.getPrivate();
		
		byte publicKeyBytes[] = publicKey.getEncoded();
		byte privateKeyBytes[] = privateKey.getEncoded();
		
		char publicKeyChar[] = BaseCJK.encodeToChar(publicKeyBytes);
		char privateKeyChar[] = BaseCJK.encodeToChar(privateKeyBytes);
		
		String publicKeyString = new String(publicKeyChar);
		String privateKeyString = new String(privateKeyChar);
		
		Cipher publicCipher;
		char cipherBaseCJK[] = null;
		try {
			publicCipher = Cipher.getInstance("RSA");
			publicCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			String message = "qwertyuiopasdfghjklzxcvbnm1234567890987654321mnbvcxzlkjhgfdsapoiuytrewqQWERTYUIOPASDFGHJKLZXCVBNM!@#$%^&*())(*&^%$#@!MNBVCXZLKJHGFDSAPOIUYTREWQajnFaojfEaoBfjSi";
			System.out.println("original text: \n" + message);
			byte cipherText1[] = publicCipher.doFinal(message.getBytes());
			
			cipherBaseCJK = BaseCJK.encodeToChar(cipherText1);
			System.out.println("BaseCJK cipher text: \n" + new String(cipherBaseCJK));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		Cipher privateCipher;
		try {
			privateCipher = Cipher.getInstance("RSA");
			privateCipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte cipherText2[] = BaseCJK.decodeToByte(cipherBaseCJK);
			byte messageByte[] = privateCipher.doFinal(cipherText2);
			String messageRecieved = new String(messageByte);
			System.out.println("recieved: \n" + messageRecieved);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
		System.out.println("key: ");
		System.out.println(publicKeyString);
		System.out.println(privateKeyString);
	}

}
