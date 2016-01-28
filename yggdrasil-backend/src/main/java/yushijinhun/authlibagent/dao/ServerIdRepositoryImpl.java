package yushijinhun.authlibagent.dao;

import java.util.UUID;
import org.springframework.stereotype.Component;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Component
public class ServerIdRepositoryImpl implements ServerIdRepository {

	private Cache serveridCache = CacheManager.getInstance().getCache(ServerIdRepository.class.getCanonicalName());

	@Override
	public void createServerId(String serverid, UUID profile) {
		serveridCache.put(new Element(serverid, profile));
	}

	@Override
	public void deleteServerId(String serverid) {
		serveridCache.remove(serverid);
	}

	@Override
	public UUID getOwner(String serverid) {
		Element element = serveridCache.get(serverid);
		return element == null ? null : (UUID) element.getObjectValue();
	}

}
