package yushijinhun.authlibagent.backend.web;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.backend.model.Account;
import yushijinhun.authlibagent.backend.model.Token;
import yushijinhun.authlibagent.backend.service.PasswordAlgorithm;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import static org.hibernate.criterion.Restrictions.conjunction;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Projections.property;

@Transactional(noRollbackFor = WebApplicationException.class)
@Component("accountResource")
public class AccountResourceImpl implements AccountResource {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private PasswordAlgorithm passwordAlgorithm;

	@Override
	public String[] getAccounts(String accessToken, String clientToken, Boolean banned, String twitchToken) {
		Session session = sessionFactory.getCurrentSession();

		if (accessToken == null && clientToken == null) {
			Conjunction conjunction = conjunction();
			if (banned != null) {
				conjunction.add(eq("banned", banned));
			}
			if (twitchToken == null) {
				conjunction.add(eq("twitchToken", twitchToken));
			}

			@SuppressWarnings("unchecked")
			List<String> ids = session.createCriteria(Account.class).add(conjunction).setProjection(property("id")).list();
			return ids.toArray(new String[ids.size()]);

		} else {
			Conjunction conjunction = conjunction();
			if (accessToken != null) {
				conjunction.add(eq("accessToken", accessToken));
			}
			if (clientToken != null) {
				conjunction.add(eq("clientToken", clientToken));
			}
			if (banned != null) {
				conjunction.add(eq("owner.banned", banned));
			}
			if (twitchToken != null) {
				conjunction.add(eq("owner.twitchToken", twitchToken));
			}

			@SuppressWarnings("unchecked")
			List<String> ids = session.createCriteria(Token.class).add(conjunction).setProjection(property("owner.id")).list();
			return ids.toArray(new String[ids.size()]);
		}
	}

	@Override
	public AccountInfo createAccount(AccountInfo accountinfo) {
		checkRequest(accountinfo);
		if (accountinfo.getId() == null) {
			throw new BadRequestException("id is null");
		}

		Session session = sessionFactory.getCurrentSession();
		if (session.get(Account.class, accountinfo.getId()) != null) {
			throw new ConflictException("account already exists");
		}

		Account account = new Account();
		fillAccountInfo(account, accountinfo);
		session.save(account);
		return createAccountInfo(account);
	}

	@Override
	public AccountInfo getAccountInfo(String id) {
		Session session = sessionFactory.getCurrentSession();
		Account account = session.get(Account.class, id);
		if (account == null) {
			throw new NotFoundException();
		}
		return createAccountInfo(account);
	}

	@Override
	public void deleteAccount(String id) {
		Session session = sessionFactory.getCurrentSession();
		Account account = session.get(Account.class, id);
		if (account == null) {
			throw new NotFoundException();
		}
		session.delete(account);
	}

	@Override
	public AccountInfo updateOrCreateAccount(String id, AccountInfo info) {
		checkRequest(info);

		Session session = sessionFactory.getCurrentSession();
		Account account = session.get(Account.class, id);
		if (account == null) {
			account = new Account();
			account.setId(id);
		}
		fillAccountInfo(account, info);
		session.saveOrUpdate(account);

		return createAccountInfo(account);
	}

	@Override
	public AccountInfo updateAccount(String id, AccountInfo info) {
		checkRequest(info);

		Session session = sessionFactory.getCurrentSession();
		Account account = session.get(Account.class, id);
		if (account == null) {
			throw new NotFoundException();
		}
		fillAccountInfo(account, info);
		session.update(account);

		return createAccountInfo(account);
	}

	private void fillAccountInfo(Account account, AccountInfo info) {
		if (info.getId() != null) {
			if (account.getId() == null) {
				account.setId(info.getId());
			} else if (!account.getId().equals(info.getId())) {
				throw new ConflictException("id conflict");
			}
		}

		if (info.getBanned() != null) {
			account.setBanned(info.getBanned());
		}

		String twitchToken = info.getTwitchToken();
		if (twitchToken != null) {
			if ("".equals(twitchToken)) {
				twitchToken = null;
			}
			account.setTwitchToken(twitchToken);
		}

		String password = info.getPassword();
		if (password != null) {
			if ("".equals(password)) {
				account.setPassword(null);
			} else {
				account.setPassword(passwordAlgorithm.hash(password));
			}
		}
	}

	private AccountInfo createAccountInfo(Account account) {
		AccountInfo accountinfo = new AccountInfo();
		accountinfo.setId(account.getId());
		accountinfo.setBanned(account.isBanned());

		String twitchToken = accountinfo.getTwitchToken();
		accountinfo.setTwitchToken(twitchToken == null ? "" : twitchToken);
		return accountinfo;
	}

	private void checkRequest(AccountInfo info) {
		if (info == null) {
			throw new BadRequestException("body cannot be empty");
		}
	}

}
