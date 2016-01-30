package yushijinhun.authlibagent.web.manager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.dao.TokenRepository;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.GameProfile;
import yushijinhun.authlibagent.model.Token;
import yushijinhun.authlibagent.service.PasswordAlgorithm;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import static java.util.stream.Collectors.toSet;
import static org.hibernate.criterion.Restrictions.conjunction;
import static org.hibernate.criterion.Restrictions.disjunction;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.eqOrIsNull;
import static org.hibernate.criterion.Projections.property;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static yushijinhun.authlibagent.util.ResourceUtils.requireNonNullBody;

@Component("accountResource")
public class AccountResourceImpl implements AccountResource {

	@Autowired
	private TokenRepository tokenRepo;

	@Autowired
	protected SessionFactory sessionFactory;

	@Autowired
	private PasswordAlgorithm passwordAlgorithm;

	@Transactional(readOnly = true)
	@Override
	public Collection<String> getAccounts(String accessToken, String clientToken, Boolean banned, String twitchToken) {
		if (accessToken == null && clientToken == null) {
			// no need for query redis
			return queryAccountsByProperties(banned, twitchToken);
		} else {
			// need to query redis
			Set<String> redisResult = queryAccountsByToken(accessToken, clientToken);
			if (banned == null && twitchToken == null) {
				// no need to query database
				return redisResult;
			} else {
				if (redisResult.isEmpty()) {
					// no result, no need to query database
					return redisResult;
				} else {
					// redis query result UNION database query result
					return queryAccountsByPropertiesInRange(banned, twitchToken, redisResult);
				}
			}
		}
	}

	/**
	 * Queries accounts by the properties of themselves.
	 * 
	 * @param banned null for not query
	 * @param twitchToken null for not query, empty for no token
	 * @return a set of id
	 */
	private Collection<String> queryAccountsByProperties(Boolean banned, String twitchToken) {
		return queryAccountsByCriterion(buildAccountsPropertiesConjunction(banned, twitchToken));
	}

	/**
	 * Queries accounts by tokens.
	 * 
	 * @param accessToken null for not query, cannot be empty
	 * @param clientToken null for not query, cannot be empty
	 * @return a set of id
	 */
	private Set<String> queryAccountsByToken(String accessToken, String clientToken) {
		if (accessToken != null) {
			Token result = tokenRepo.get(accessToken);
			if (result != null && clientToken != null && !clientToken.equals(result.getClientToken())) {
				return Collections.emptySet();
			} else {
				return result == null ? Collections.emptySet() : Collections.singleton(result.getOwner());
			}
		} else {
			return tokenRepo.getByClientToken(clientToken).stream().map(Token::getOwner).collect(toSet());
		}
	}

	/**
	 * Queries accounts by the properties of themselves in the given range.
	 * 
	 * @param banned null for not query
	 * @param twitchToken null for not query, empty for no token
	 * @param range the account range
	 * @return a set of id
	 */
	private Collection<String> queryAccountsByPropertiesInRange(Boolean banned, String twitchToken, Set<String> range) {
		Conjunction propertiesConjunction = buildAccountsPropertiesConjunction(banned, twitchToken);
		Disjunction accountsDisjunction = disjunction();
		range.forEach(id -> accountsDisjunction.add(eq("id", id)));
		return queryAccountsByCriterion(conjunction(propertiesConjunction, accountsDisjunction));
	}

	/**
	 * Queries accounts by criterion.
	 * 
	 * @param criterion criterion
	 * @return a set of id
	 */
	private Collection<String> queryAccountsByCriterion(Criterion criterion) {
		@SuppressWarnings("unchecked")
		List<String> ids = sessionFactory.getCurrentSession().createCriteria(Account.class).add(criterion).setProjection(property("id")).setCacheable(true).list();
		return ids;
	}

	/**
	 * Builds a conjunction by the properties of accounts.
	 * 
	 * @param banned null for not query
	 * @param twitchToken null for not query, empty for no token
	 * @return conjunction
	 */
	private Conjunction buildAccountsPropertiesConjunction(Boolean banned, String twitchToken) {
		Conjunction conjunction = conjunction();
		if (banned != null) {
			conjunction.add(eq("banned", banned));
		}
		if (twitchToken != null) {
			conjunction.add(eqOrIsNull("twitchToken", emptyToNull(twitchToken)));
		}
		return conjunction;
	}

	@Transactional
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

	@Transactional(readOnly = true)
	@Override
	public AccountInfo getAccountInfo(String id) {
		return createAccountInfo(lookupAccount(id));
	}

	@Transactional
	@Override
	public void deleteAccount(String id) {
		sessionFactory.getCurrentSession().delete(lookupAccount(id));
	}

	@Transactional
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

	@Transactional
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
