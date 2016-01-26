package yushijinhun.authlibagent.web.manager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.service.SignatureService;
import yushijinhun.authlibagent.util.KeyUtils;

@Component("keyResource")
public class KeyResourceImpl implements KeyResource {

	@Value("#{config['security.allowDownloadPrivateKey']}")
	private boolean allowDownloadPrivateKey;

	@Value("#{config['security.allowUploadPrivateKey']}")
	private boolean allowUploadPrivateKey;

	@Autowired
	private SignatureService signatureService;

	@Override
	public byte[] getEncodedKey() {
		if (!allowDownloadPrivateKey) {
			throw new ForbiddenException("It is not allowed to download the private key");
		}

		RSAPrivateKey key = signatureService.getKey();
		return key == null ? new byte[0] : key.getEncoded();
	}

	@Override
	public void setEncodedKey(byte[] key) throws IOException {
		if (!allowUploadPrivateKey) {
			throw new ForbiddenException("It is not allowed to upload a private key");
		}

		try {
			signatureService.setKey(key.length == 0 ? null : KeyUtils.fromPKCS8(key));
		} catch (GeneralSecurityException e) {
			throw new BadRequestException("Invalid key: " + e, e);
		}
	}

}
