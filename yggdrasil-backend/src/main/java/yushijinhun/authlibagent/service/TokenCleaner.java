package yushijinhun.authlibagent.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.model.Token;
import static org.hibernate.criterion.Restrictions.lt;

@Component
public class TokenCleaner {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private class CleanTask extends TimerTask {

		Class<?> entity;
		String timeField;
		long expireTime;

		CleanTask(Class<?> entity, String timeField, long expireTime) {
			this.entity = entity;
			this.timeField = timeField;
			this.expireTime = expireTime;
		}

		@Override
		public void run() {
			try {
				doCleanup();
			} catch (Exception e) {
				LOGGER.warn("failed to execute time expire clean up on " + entity, e);
			}
		}

		void doCleanup() {
			long earliestTime = System.currentTimeMillis() - expireTime;
			LOGGER.debug("executing time expire clean up on %s. earliestTime=%d", entity.getSimpleName(), earliestTime);

			int cleans;

			// TODO: maybe we can use spring managed transaction here?
			Session session;
			try {
				session = sessionFactory.openSession();
			} catch (HibernateException e) {
				// sometimes the data source is not initialized here
				// it's usually not a issue
				LOGGER.debug("failed to execute time expire clean up on " + entity + ", maybe the datasource is not initialized?", e);
				return;
			}

			try {
				List<?> matches = session.createCriteria(entity).add(lt(timeField, earliestTime)).list();
				cleans = matches.size();
				matches.forEach(session::delete);
				session.flush();
			} catch (RuntimeException e) {
				// roll back for unchecked exceptions
				session.getTransaction().rollback();
				throw e;
			} finally {
				session.close();
			}

			LOGGER.debug("executed time expire clean up on %s. clean up %d objects", entity.getSimpleName(), cleans);
		}

	}

	@Autowired
	private SessionFactory sessionFactory;

	@Value("#{config['expire.token.time']}")
	private long tokenExpireTime;

	@Value("#{config['expire.token.scantime']}")
	private long tokenScanTime;

	private Timer timer;

	@PostConstruct
	private void startThread() {
		timer = new Timer("token-cleaner", true);

		// token
		timer.scheduleAtFixedRate(new CleanTask(Token.class, "createTime", tokenExpireTime), tokenScanTime, tokenScanTime);
	}

	@PreDestroy
	private void stopThread() {
		timer.cancel();
	}

}
