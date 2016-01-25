package yushijinhun.authlibagent.web.manager;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@XmlRootElement(name = "error")
public class ErrorResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private int errorCode;
	private String error;
	private String stackdump;

	@XmlElement
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@XmlElement
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@JsonInclude(Include.NON_NULL)
	@XmlElement
	public String getStackdump() {
		return stackdump;
	}

	public void setStackdump(String stackdump) {
		this.stackdump = stackdump;
	}

}