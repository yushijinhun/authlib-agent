package yushijinhun.authlibagent.backend.service;

import yushijinhun.authlibagent.backend.model.Account;

public interface PasswordAlgorithm {

	String hash(String password);

	boolean verify(String password, String hash);

	default boolean verify(String password, Account account) {
		return verify(password, account.getPassword());
	}

}
