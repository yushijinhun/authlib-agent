package yushijinhun.authlibagent.backend.service;

public interface PasswordAlgorithm {

	String hash(String password);

	boolean verify(String password, String hash);

}
