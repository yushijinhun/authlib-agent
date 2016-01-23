package yushijinhun.authlibagent.commons;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import static yushijinhun.authlibagent.commons.HexUtils.*;

public class RandomUtils {

	private static SecureRandom rnd;

	static {
		try {
			rnd = SecureRandom.getInstance("NativePRNGBlocking");
		} catch (NoSuchAlgorithmException e) {
			rnd = new SecureRandom();
		}
	}

	public static SecureRandom getSecureRandom() {
		return rnd;
	}

	public static String randomHexString(int bytes) {
		byte[] data = new byte[bytes];
		rnd.nextBytes(data);
		return bytesToHex(data);
	}

	public static UUID randomUUID() {
		byte[] randomBytes = new byte[16];
		rnd.nextBytes(randomBytes);
		randomBytes[6] &= 0x0f;
		randomBytes[6] |= 0x40;
		randomBytes[8] &= 0x3f;
		randomBytes[8] |= 0x80;
		long msb = 0;
		long lsb = 0;
		for (int i = 0; i < 8; i++)
			msb = (msb << 8) | (randomBytes[i] & 0xff);
		for (int i = 8; i < 16; i++)
			lsb = (lsb << 8) | (randomBytes[i] & 0xff);
		return new UUID(msb, lsb);
	}

	private RandomUtils() {
	}

}
