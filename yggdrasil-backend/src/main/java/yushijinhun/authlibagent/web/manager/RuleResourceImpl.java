package yushijinhun.authlibagent.web.manager;

import java.util.Collection;
import java.util.List;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.model.AccessPolicy;
import yushijinhun.authlibagent.model.AccessRule;
import static org.hibernate.criterion.Restrictions.eq;
import static yushijinhun.authlibagent.util.ResourceUtils.requireNonNullBody;

@Transactional
@Component("ruleResource")
public class RuleResourceImpl implements RuleResource {

	@Autowired
	protected SessionFactory sessionFactory;

	@Transactional(readOnly = true)
	@Override
	public Collection<AccessRule> getRules(AccessPolicy policy) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(AccessRule.class);
		if (policy != null) {
			criteria.add(eq("policy", policy));
		}
		@SuppressWarnings("unchecked")
		List<AccessRule> result = criteria.setCacheable(true).list();
		return result;
	}

	@Transactional
	@Override
	public AccessRule createRule(AccessRule rule) {
		requireNonNullBody(rule);
		if (rule.getHost() == null) {
			throw new BadRequestException("host cannot be null");
		}
		if (rule.getPolicy() == null) {
			throw new BadRequestException("policy cannot be null");
		}

		Session session = sessionFactory.getCurrentSession();
		if (session.get(AccessRule.class, rule.getHost()) != null) {
			throw new ConflictException("rule already exists");
		}
		session.save(rule);

		return rule;
	}

	@Transactional(readOnly = true)
	@Override
	public AccessRule getRule(String host) {
		return lookupRule(host);
	}

	@Transactional
	@Override
	public AccessRule createOrUpdateRule(String host, AccessRule info) {
		requireNonNullBody(info);

		Session session = sessionFactory.getCurrentSession();
		AccessRule rule = session.get(AccessRule.class, host);
		if (rule == null) {
			rule = new AccessRule();
			rule.setHost(host);
		}
		fillRuleInfo(rule, info);
		session.saveOrUpdate(rule);

		return rule;
	}

	@Transactional
	@Override
	public AccessRule updateRule(String host, AccessRule info) {
		requireNonNullBody(info);

		AccessRule rule = lookupRule(host);
		fillRuleInfo(rule, info);
		sessionFactory.getCurrentSession().save(rule);

		return rule;
	}

	@Transactional
	@Override
	public void deleteRule(String host) {
		sessionFactory.getCurrentSession().delete(lookupRule(host));
	}

	private AccessRule lookupRule(String host) {
		Session session = sessionFactory.getCurrentSession();
		AccessRule rule = session.get(AccessRule.class, host);
		if (rule == null) {
			throw new NotFoundException();
		}
		return rule;
	}

	private void fillRuleInfo(AccessRule rule, AccessRule info) {
		if (info.getHost() != null) {
			if (rule.getHost() == null) {
				rule.setHost(info.getHost());
			} else if (!rule.getHost().equals(info.getHost())) {
				throw new ConflictException("host conflict");
			}
		}

		if (info.getPolicy() != null) {
			rule.setPolicy(info.getPolicy());
		}
	}

}
