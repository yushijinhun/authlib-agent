package yushijinhun.authlibagent.backend.util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public final class RMIUtils {

	public static void exportRemoteObject(Remote obj) {
		try {
			UnicastRemoteObject.exportObject(obj, 0);
		} catch (RemoteException e) {
			throw new IllegalStateException("cannot export [" + obj + "] to a remote obj", e);
		}
	}

	private RMIUtils() {
	}
}
