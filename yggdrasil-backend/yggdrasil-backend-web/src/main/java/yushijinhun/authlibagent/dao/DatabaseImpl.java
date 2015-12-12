package yushijinhun.authlibagent.dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseImpl implements Database {

	private SessionFactory sessionFactory;

	public DatabaseImpl() {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
