package yushijinhun.authlibagent.web.manager;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateKey;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
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
	public Response getEncodedKey() {
		if (!allowDownloadPrivateKey) {
			throw new ForbiddenException("It is not allowed to download the private key");
		}

		RSAPrivateKey key = signatureService.getKey();
		if (key == null) {
			return Response.noContent().build();
		} else {
			return Response.ok().type(MediaType.APPLICATION_OCTET_STREAM).entity(key.getEncoded()).build();
		}
	}

	@Override
	public void setEncodedKey(HttpServletRequest req) throws IOException {
		if (!allowUploadPrivateKey) {
			throw new ForbiddenException("It is not allowed to upload a private key");
		}

		byte[] key = IOUtils.toByteArray(req.getInputStream());
		try {
			signatureService.setKey(KeyUtils.fromPKCS8(key));
		} catch (GeneralSecurityException e) {
			throw new BadRequestException("Invalid key: " + e, e);
		}
	}

}
