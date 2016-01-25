package yushijinhun.authlibagent.service;

import java.util.List;
import java.util.Objects;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.Token;
import static org.hibernate.criterion.Restrictions.eq;
import static yushijinhun.authlibagent.util.RandomUtils.randomUUID;
import static yushijinhun.authlibagent.util.UUIDUtils.unsign;
import static org.hibernate.criterion.Restrictions.and;

@Transactional
@Component
public class LoginServiceImpl implements LoginService {

	private static final String MSG_INVALID_USERNAME_OR_PASSWORD = "Invalid credentials. Invalid username or password.";
	private static final String MSG_INVALID_TOKEN = "Invalid token.";
	private static final String MSG_ACCOUNT_BANNED = "Account has been banned.";

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PasswordAlgorithm passwordAlgorithm;

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

	@Override
	public Account loginWithToken(String accessToken, String clientToken) throws ForbiddenOperationException {
		if (accessToken == null || clientToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}

		Token token = verifyToken(accessToken, clientToken);
		Account account = token.getOwner();
		if (account.isBanned()) {
			throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
		}

		return account;
	}

	@Override
	public Account loginWithToken(String accessToken) throws ForbiddenOperationException {
		if (accessToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}

		Token token = verifyToken(accessToken);
		Account account = token.getOwner();
		if (account.isBanned()) {
			throw new ForbiddenOperationException(MSG_ACCOUNT_BANNED);
		}

		return account;
	}

	@Override
	public Token createToken(Account account, String clientToken) {
		Objects.requireNonNull(account);
		if (clientToken == null) {
			clientToken = randomToken();
		}

		Session session = sessionFactory.getCurrentSession();

		// delete old token
		@SuppressWarnings("unchecked")
		List<Token> oldTokens = session.createCriteria(Token.class).add(and(eq("clientToken", clientToken), eq("owner", account))).list();
		oldTokens.forEach(session::delete);

		// create new token
		Token token = new Token();
		token.setAccessToken(randomToken());
		token.setClientToken(clientToken);
		token.setOwner(account);
		token.setCreateTime(System.currentTimeMillis());
		session.save(token);

		return token;
	}

	@Override
	public Token createToken(Account account) {
		return createToken(account, null);
	}

	@Override
	public void revokeToken(String accessToken, String clientToken) throws ForbiddenOperationException {
		if (accessToken == null || clientToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}

		Session session = sessionFactory.getCurrentSession();
		Token token = verifyToken(accessToken, clientToken);
		session.delete(token);
	}

	@Override
	public void revokeToken(String accessToken) throws ForbiddenOperationException {
		if (accessToken == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}

		Session session = sessionFactory.getCurrentSession();
		Token token = verifyToken(accessToken);
		session.delete(token);
	}

	@Override
	public void revokeAllTokens(Account account) {
		Objects.requireNonNull(account);

		Session session = sessionFactory.getCurrentSession();
		account.getTokens().forEach(session::delete);
	}

	@Override
	public boolean isTokenAvailable(String accessToken, String clientToken) {
		if (accessToken == null || clientToken == null) {
			return false;
		}

		Session session = sessionFactory.getCurrentSession();
		Token token = session.get(Token.class, accessToken);
		return token != null && token.getClientToken().equals(clientToken);
	}

	@Override
	public boolean isTokenAvailable(String accessToken) {
		if (accessToken == null) {
			return false;
		}

		Session session = sessionFactory.getCurrentSession();
		Token token = session.get(Token.class, accessToken);
		return token != null;
	}

	private static String randomToken() {
		return unsign(randomUUID());
	}

	private Token verifyToken(String accessToken, String clientToken) throws ForbiddenOperationException {
		Session session = sessionFactory.getCurrentSession();
		Token token = session.get(Token.class, accessToken);
		if (token == null || !token.getClientToken().equals(clientToken)) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}
		return token;
	}

	private Token verifyToken(String accessToken) throws ForbiddenOperationException {
		Session session = sessionFactory.getCurrentSession();
		Token token = session.get(Token.class, accessToken);
		if (token == null) {
			throw new ForbiddenOperationException(MSG_INVALID_TOKEN);
		}
		return token;
	}

}
