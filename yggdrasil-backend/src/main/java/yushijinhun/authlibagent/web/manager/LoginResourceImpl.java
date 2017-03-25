package yushijinhun.authlibagent.web.manager;

import javax.ws.rs.ForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import yushijinhun.authlibagent.service.ForbiddenOperationException;
import yushijinhun.authlibagent.service.LoginService;

@Component("loginResource")
public class LoginResourceImpl implements LoginResource {

	@Autowired
	private LoginService loginService;

	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	@Override
	public AccountInfo login(LoginParam param) {
		try {
			return new AccountInfo(loginService.loginWithPassword(param.getUsername(), param.getPassword(), param.isIgnoreBanned()));
		} catch (ForbiddenOperationException e) {
			throw new ForbiddenException(e.getMessage(), e);
		}
	}

}
