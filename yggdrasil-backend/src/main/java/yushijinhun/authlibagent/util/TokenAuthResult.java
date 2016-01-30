package yushijinhun.authlibagent.util;

import java.util.Objects;
import yushijinhun.authlibagent.model.Account;
import yushijinhun.authlibagent.model.Token;

/**
 * 包装了token登录后的验证结果. 包含Account和Token对象, 避免只返回Token/Account导致多次查询数据库.
 * 
 * @author yushijinhun
 */
public class TokenAuthResult {

	private Account account;
	private Token token;

	public TokenAuthResult(Account account, Token token) {
		this.account = account;
		this.token = token;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	@Override
	public int hashCode() {
		return Objects.hash(account, token);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof TokenAuthResult) {
			TokenAuthResult another = (TokenAuthResult) obj;
			return Objects.equals(token, another.token) && Objects.equals(account, another.account);
		}
		return false;
	}

}
