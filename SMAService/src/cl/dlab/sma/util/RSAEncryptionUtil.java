package cl.dlab.sma.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import sun.misc.BASE64Decoder;

public class RSAEncryptionUtil
{

	static
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	

	public static void encrypt (String inputFilename, String encryptedFilename) throws Exception
	{
		encrypt(PropertyUtil.getProperty("ENCRIPTED-KEY"), inputFilename, encryptedFilename);
	}

	public static byte[] encrypt (byte[] messageBytes) throws Exception
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
        encrypt(PropertyUtil.getProperty("ENCRIPTED-KEY"), messageBytes, bo);
        return bo.toByteArray();
	}
	public static void encrypt (String publicKeyFilename, String inputFilename, String encryptedFilename) throws Exception
	{
        byte[] messageBytes = Files.readAllBytes( Paths.get(inputFilename));
        encrypt(publicKeyFilename, messageBytes, new FileOutputStream(encryptedFilename));
	}
	public static void encrypt (String publicKeyFilename, byte[] messageBytes, OutputStream encryptedOutputStream) throws Exception
	{

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        String key = readFileAsString(publicKeyFilename);
        BASE64Decoder b64 = new BASE64Decoder();
        AsymmetricKeyParameter publicKey = 
            (AsymmetricKeyParameter) PublicKeyFactory.createKey(b64.decodeBuffer(key));
        AsymmetricBlockCipher e = new RSAEngine();
        e = new org.bouncycastle.crypto.encodings.PKCS1Encoding(e);
        e.init(true, publicKey);

        //String inputdata = readFileAsString(inputFilename);
        int i = 0;
        int len = e.getInputBlockSize();
        while (i < messageBytes.length)
        {
            if (i + len > messageBytes.length)
            {
                len = messageBytes.length - i;
            }
            encryptedOutputStream.write(e.processBlock(messageBytes, i, len));
            i += e.getInputBlockSize();
        }
        encryptedOutputStream.close();
    }

	public static ByteArrayOutputStream decrypt(String privateKeyFilename, String encryptedFilename) throws Exception
	{
		return decrypt(privateKeyFilename, Files.readAllBytes(Paths.get(encryptedFilename)));
	}
	public static ByteArrayOutputStream decrypt(String encryptedFilename) throws Exception
	{
		return decrypt(PropertyUtil.getProperty("DESENCRIPTED-KEY"), Files.readAllBytes(Paths.get(encryptedFilename)));
	}

	public static byte[] decrypt(byte[] messageBytes) throws Exception
	{
		ByteArrayOutputStream bo = decrypt(PropertyUtil.getProperty("DESENCRIPTED-KEY"), messageBytes);
		return bo.toByteArray();
	}
	public static ByteArrayOutputStream decrypt(String privateKeyFilename, byte[] messageBytes) throws Exception
	{

		String key = readFileAsString(privateKeyFilename);
		BASE64Decoder b64 = new BASE64Decoder();
		AsymmetricKeyParameter privateKey = (AsymmetricKeyParameter) PrivateKeyFactory.createKey(b64.decodeBuffer(key));
		AsymmetricBlockCipher e = new RSAEngine();
		e = new org.bouncycastle.crypto.encodings.PKCS1Encoding(e);
		e.init(false, privateKey);

		

		int i = 0;
		int len = e.getInputBlockSize();
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		while (i < messageBytes.length)
		{
			if (i + len > messageBytes.length) 
			{
				len = messageBytes.length - i;
			}
            bo.write(e.processBlock(messageBytes, i, len));
			i += e.getInputBlockSize();
		}
		bo.close();
		return bo;
	}

	private static String readFileAsString(String filePath) throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}