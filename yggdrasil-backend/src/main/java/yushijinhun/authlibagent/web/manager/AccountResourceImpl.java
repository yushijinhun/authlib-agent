package yushijinhun.authlibagent.web.manager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.model.Token;
import yushijinhun.authlibagent.service.PasswordAlgorithm;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import static java.util.stream.Collectors.toSet;
import static org.hibernate.criterion.Restrictions.conjunction;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.eqOrIsNull;
import static org.hibernate.criterion.Projections.property;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static yushijinhun.authlibagent.util.ResourceUtils.requireNonNullBody;

@Transactional
@Component("accountResource")
public class AccountResourceImpl implements AccountResource {

	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired
	private PasswordAlgorithm passwordAlgorithm;

	@Override
	public Collection<String> getAccounts(String accessToken, String clientToken, Boolean banned, String twitchToken) {
		Conjunction accountConjunction = conjunction();
		if (banned != null) {
			accountConjunction.add(eq("banned", banned));
		}
		if (twitchToken != null) {
			accountConjunction.add(eqOrIsNull("twitchToken", emptyToNull(twitchToken)));
		}

		Session session = sessionFactory.getCurrentSession();
		if (accessToken == null && clientToken == null) {
			@SuppressWarnings("unchecked")
			List<String> ids = session.createCriteria(Account.class).add(accountConjunction).setProjection(property("id")).list();
			return ids;

		} else {
			Conjunction tokenConjunction = conjunction();
			if (accessToken != null) {
				if (accessToken.isEmpty()) {
					throw new BadRequestException("accessToken is empty");
				}
				tokenConjunction.add(eq("accessToken", accessToken));
			}
			if (clientToken != null) {
				if (clientToken.isEmpty()) {
					throw new BadRequestException("clientToken is empty");
				}
				tokenConjunction.add(eq("clientToken", clientToken));
			}

			@SuppressWarnings("unchecked")
			List<String> ids = session.createCriteria(Token.class).add(tokenConjunction).createCriteria("owner").add(accountConjunction).setProjection(property("id")).list();
			Set<String> result = new HashSet<>(ids);
			return result;
		}
	}

	@Override
	public AccountInfo createAccount(AccountInfo info) {
		requireNonNullBody(info);
		if (info.getId() == null) {
			throw new BadRequestException("id cannot be null");
		}

		Session session = sessionFactory.getCurrentSession();
		if (session.get(Account.class, info.getId()) != null) {
			throw new ConflictException("account already exists");
		}

		Account account = new Account();
		fillAccountInfo(account, info);
		session.save(account);
		return createAccountInfo(account);
	}

	@Override
	public AccountInfo getAccountInfo(String id) {
		return createAccountInfo(lookupAccount(id));
	}

	@Override
	public void deleteAccount(String id) {
		sessionFactory.getCurrentSession().delete(lookupAccount(id));
	}

	@Override
	public AccountInfo updateOrCreateAccount(String id, AccountInfo info) {
		requireNonNullBody(info);

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
		requireNonNullBody(info);

		Account account = lookupAccount(id);
		fillAccountInfo(account, info);
		sessionFactory.getCurrentSession().update(account);

		return createAccountInfo(account);
	}

	private void fillAccountInfo(Account account, AccountInfo info) {
		if (info.getId() != null) {
			if (account.getId() == null) {
				account.setId(info.getId());
			} else if (!account.getId().equals(info.getId())) {
				// changing the id is not allowed
				throw new ConflictException("id conflict");
			}
		}

		if (info.getBanned() != null) {
			account.setBanned(info.getBanned());
		}

		account.setTwitchToken(emptyToNull(info.getTwitchToken()));

		String password = info.getPassword();
		if (password != null) {
			if (password.isEmpty()) {
				account.setPassword(null);
			} else {
				account.setPassword(passwordAlgorithm.hash(password));
			}
		}

		if (info.getProfiles() != null && !info.getProfiles().equals(getAccountProfiles(account))) {
			// changing the profiles is not allowed
			throw new ConflictException("profiles conflict");
		}

		if (info.getSelectedProfile() != null) {
			UUID newSelectedProfile = info.getSelectedProfile().isEmpty() ? null : UUID.fromString(info.getSelectedProfile());
			if (newSelectedProfile != getAccountSelectedProfile(account)) {
				if (newSelectedProfile == null) {
					account.setSelectedProfile(null);
				} else {
					Session session = sessionFactory.getCurrentSession();
					GameProfile profile = session.get(GameProfile.class, newSelectedProfile.toString());
					if (profile == null) {
						throw new BadRequestException(String.format("profile %s not exists", newSelectedProfile));
					}
					if (!account.equals(profile.getOwner())) {
						throw new ConflictException(String.format("the owner of %s is %s, not %s", newSelectedProfile, profile.getOwner().getId(), account.getId()));
					}
					account.setSelectedProfile(profile);
				}
			}
		}
	}

	private Set<UUID> getAccountProfiles(Account account) {
		Set<GameProfile> profiles = account.getProfiles();
		// profiles will be null if the account is in transient status
		return profiles == null ? Collections.emptySet() : profiles.stream().map(p -> UUID.fromString(p.getUuid())).collect(toSet());
	}

	private UUID getAccountSelectedProfile(Account account) {
		GameProfile profile = account.getSelectedProfile();
		// profile will be null if the account is in transient status
		return profile == null ? null : UUID.fromString(profile.getUuid());
	}

	private AccountInfo createAccountInfo(Account account) {
		AccountInfo info = new AccountInfo();
		info.setId(account.getId());
		info.setBanned(account.isBanned());
		info.setTwitchToken(nullToEmpty(account.getTwitchToken()));
		info.setProfiles(getAccountProfiles(account));
		UUID selectedProfile = getAccountSelectedProfile(account);
		info.setSelectedProfile(selectedProfile == null ? "" : selectedProfile.toString());
		return info;
	}

	private Account lookupAccount(String id) {
		Session session = sessionFactory.getCurrentSession();
		Account account = session.get(Account.class, id);
		if (account == null) {
			throw new NotFoundException();
		}
		return account;
	}

}
