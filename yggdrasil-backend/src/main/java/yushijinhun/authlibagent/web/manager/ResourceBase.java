package yushijinhun.authlibagent.web.manager;

import java.util.UUID;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.GameProfile;

abstract public class ResourceBase {
	
	@Autowired
	protected SessionFactory sessionFactory;

	protected void requireNonNullBody(Object body) {
		if (body == null) {
			throw new BadRequestException("body cannot be empty");
		}
	}
	
	protected Account lookupAccount(String id){
		Session session = sessionFactory.getCurrentSession();
		Account account = session.get(Account.class, id);
		if (account == null) {
			throw new NotFoundException();
		}
		return account;
	}

	protected GameProfile lookupProfile(UUID uuid) {
		Session session = sessionFactory.getCurrentSession();
		GameProfile profile = session.get(GameProfile.class, uuid.toString());
		if (profile == null) {
			throw new NotFoundException();
		}
		return profile;
	}

}
