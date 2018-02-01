package org.hz.springcloud.example;

import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.codec.binary.Base64OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AESEncryption implements IEncryption {

    public static final String AES = "AES";
    private final byte[] key;

    public AESEncryption(byte[] key) {
        this.key = key;
    }

    protected Cipher getEncryptCipher() throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES));
        return cipher;
    }

    protected Cipher getDecryptCipher() throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES));
        return cipher;
    }


    @Override
    public OutputStream encrypt(OutputStream outputStream) throws IOException {
        try {
            return new CipherOutputStream(new Base64OutputStream(outputStream, true), getEncryptCipher());
            //return new Base64OutputStream(outputStream, true);
            //return outputStream;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public OutputStream decrypt(OutputStream outputStream) throws IOException {
        try {
            return new Base64OutputStream(new CipherOutputStream(outputStream, getDecryptCipher()), false);
            //return new Base64OutputStream(outputStream, false);
            //return outputStream;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public InputStream encrypt(InputStream inputStream) throws IOException {
        try {
            return new CipherInputStream(new Base64InputStream(inputStream, true), getEncryptCipher());
            //return new Base64InputStream(inputStream, true);
            //return inputStream;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public InputStream decrypt(InputStream inputStream) throws IOException {
        try {
            return new Base64InputStream(new CipherInputStream(inputStream, getDecryptCipher()), false);
            //return new Base64InputStream(inputStream, false);
            //return inputStream;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
