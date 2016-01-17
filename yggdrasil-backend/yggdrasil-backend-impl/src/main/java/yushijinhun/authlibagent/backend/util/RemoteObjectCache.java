package yushijinhun.authlibagent.backend.util;

import java.rmi.Remote;
import java.util.function.Function;
import static yushijinhun.authlibagent.backend.util.RMIUtils.*;

public class RemoteObjectCache<K, V extends Remote> extends Cache<K, V> {

	public RemoteObjectCache(Function<K, V> producer) {
		super(producer);
	}

	@Override
	protected V wrap(K key, V val) {
		V obj = super.wrap(key, val);
		exportRemoteObject(obj);
		return obj;
	}

}
