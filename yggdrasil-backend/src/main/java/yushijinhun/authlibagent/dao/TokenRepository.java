package yushijinhun.authlibagent.dao;

import java.util.Set;
import yushijinhun.authlibagent.model.Token;

public interface TokenRepository {

	Token get(String accessToken);

	Set<Token> getByClientToken(String clientToken);

	Set<Token> getByAccount(String account);

	void put(Token token);

	void delete(String accessToken);

	void deleteByAccount(String account);

}
