package br.com.fiap;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.util.Base64;

public class Cryptography {

    public static KeyPair generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator geradorChave = KeyPairGenerator.getInstance("RSA");
        geradorChave.initialize(2048);
        return geradorChave.generateKeyPair();
    }

    public static KeyPair generateKey(BigInteger p, BigInteger q) throws Exception {
        BigInteger n = p.multiply(q);
        BigInteger totiente = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.valueOf(65537);
        BigInteger d = e.modInverse(totiente);

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        return new KeyPair(keyPair.getPublic(), keyPair.getPrivate());
    }

    public static String encrypt(String mensagem, PublicKey publicKey) throws Exception {
        byte[] messageToBytes = mensagem.getBytes("UTF-8");
        Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cifrador.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytesCripto = cifrador.doFinal(messageToBytes);
        return Base64.getEncoder().encodeToString(bytesCripto);
    }

    public static String decrypt(String mensagem, PrivateKey privateKey) throws Exception {
        byte[] bytesCifrados = Base64.getDecoder().decode(mensagem);
        Cipher cifrador = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cifrador.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] mensagemDecifrada = cifrador.doFinal(bytesCifrados);
        return new String(mensagemDecifrada, "UTF-8");
    }

    public static PublicKey bytesForKeys(byte[] bytesChave) throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytesChave);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
