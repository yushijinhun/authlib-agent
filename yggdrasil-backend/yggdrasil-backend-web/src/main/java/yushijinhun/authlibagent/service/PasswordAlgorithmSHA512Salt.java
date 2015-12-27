package yushijinhun.authlibagent.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Component;
import static yushijinhun.authlibagent.util.RandomUtils.*;
import static yushijinhun.authlibagent.util.HexUtils.*;

@Component("password_sha512_salt")
public class PasswordAlgorithmSHA512Salt implements PasswordAlgorithm {

	private int saltLength = 16;

	@Override
	public String hash(String password) {
		String salt = randomHexString(saltLength);
		String mixed = toMixed(password, salt);
		return salt + "$" + mixed;
	}

	@Override
	public boolean verify(String password, String hash) {
		String[] splited = hash.split("\\$", 2);
		String salt = splited[0];
		String mixed = splited[1];
		String mixedToVerify = toMixed(password, salt);
		return mixed.equals(mixedToVerify);
	}

	private String toMixed(String password, String salt) {
		try {
			MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
			String pwdHash = bytesToHex(sha512.digest(password.getBytes("UTF-8")));
			String premixed = pwdHash + "$" + salt;
			String mixed = bytesToHex(sha512.digest(premixed.getBytes("UTF-8")));
			return mixed;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

}
