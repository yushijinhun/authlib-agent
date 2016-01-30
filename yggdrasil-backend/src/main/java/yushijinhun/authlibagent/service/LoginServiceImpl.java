package yushijinhun.authlibagent.service;

import java.util.Objects;
import java.util.UUID;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.dao.TokenRepository;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.Token;
import yushijinhun.authlibagent.util.TokenAuthResult;
import static yushijinhun.authlibagent.util.RandomUtils.randomUUID;
import static yushijinhun.authlibagent.util.UUIDUtils.unsign;

@Component
public class LoginServiceImpl implements LoginService {

	private static final String MSG_INVALID_USERNAME_OR_PASSWORD = "Invalid credentials. Invalid username or password.";
	private static final String MSG_INVALID_TOKEN = "Invalid token.";
	private static final String MSG_ACCOUNT_BANNED = "Account has been banned.";

	@Autowired
	private TokenRepository tokenRepo;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PasswordAlgorithm passwordAlgorithm;

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public Account loginWithPassword(String username, String password) throws ForbiddenOperationException {
		if (username == null || password == null) {
			throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD);
		}

		Session session = sessionFactory.getCurrentSession();
		Account account = session.get(Account.class, username);
		if (account == null || account.getPassword() == null || !passwordAlgorithm.verify(password, account.getPassword())) {
			throw new ForbiddenOperationException(MSG_INVALID_USERNAME_OR_PASSWORD);
		}
		if (account.isBanned()) {
			throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
		}

		return account;
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public TokenAuthResult loginWithToken(String accessToken, String clientToken) throws ForbiddenOperationException {
		if (accessToken == null || clientToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}
		return loginWithToken(verifyToken(accessToken, clientToken));
	}

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public TokenAuthResult loginWithToken(String accessToken) throws ForbiddenOperationException {
		if (accessToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}
		return loginWithToken(verifyToken(accessToken));
	}

	private TokenAuthResult loginWithToken(Token token) throws ForbiddenOperationException {
		Account account = sessionFactory.getCurrentSession().get(Account.class, token.getOwner());
		if (account == null || account.isBanned()) {
			throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
		}
		return new TokenAuthResult(account, token);
	}

	@Override
	public Token createToken(String account, UUID selectedProfile, String clientToken) {
		Objects.requireNonNull(account);
		if (clientToken == null) {
			clientToken = randomToken();
		}

		Token token = new Token();
		token.setAccessToken(randomToken());
		token.setClientToken(clientToken);
		token.setOwner(account);
		token.setSelectedProfile(selectedProfile);

		tokenRepo.put(token);
		return token;
	}

	@Override
	public Token createToken(String account, UUID selectedProfile) {
		return createToken(account, selectedProfile, null);
	}

	@Override
	public void revokeToken(String accessToken, String clientToken) throws ForbiddenOperationException {
		if (accessToken == null || clientToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}

		verifyToken(accessToken, clientToken);
		tokenRepo.delete(accessToken);
	}

	@Override
	public void revokeToken(String accessToken) throws ForbiddenOperationException {
		if (accessToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}

		verifyToken(accessToken);
		tokenRepo.delete(accessToken);
	}

	@Override
	public void revokeAllTokens(String account) {
		Objects.requireNonNull(account);
		tokenRepo.deleteByAccount(account);
	}

	@Override
	public boolean isTokenAvailable(String accessToken, String clientToken) {
		if (accessToken == null || clientToken == null) {
			return false;
		}
		Token token = tokenRepo.get(accessToken);
		return token != null && clientToken.equals(token.getClientToken());
	}

	@Override
	public boolean isTokenAvailable(String accessToken) {
		if (accessToken == null) {
			return false;
		}
		Token token = tokenRepo.get(accessToken);
		return token != null;
	}

	private static String randomToken() {
		return unsign(randomUUID());
	}

	private Token verifyToken(String accessToken, String clientToken) throws ForbiddenOperationException {
		Token token = tokenRepo.get(accessToken);
		if (token == null || !token.getClientToken().equals(clientToken)) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}
		return token;
	}

	private Token verifyToken(String accessToken) throws ForbiddenOperationException {
		Token token = tokenRepo.get(accessToken);
		if (token == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}
		return token;
	}

}
