/**
 * 
 */
package hello;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

/**
 * @author Varun
 *
 */
public class WebLogin {
	private String login_id;
	@NotEmpty @NotNull @URL
    private String url;
	@NotEmpty @NotNull
    private String login;
	@NotEmpty @NotNull @Size(min=2)
    private String password;

    /**
     * Default Constructor
     */
    public WebLogin(){	
    	
    }

    /**
     * Parameterized constructor
     * @param login_id
     * @param url
     * @param login
     * @param password
     */
    public WebLogin(String login_id, String url, String login, String password){
    	this.login_id = login_id;
    	this.url = url;
    	this.login = login;
    	this.password = password;
    }

	/**
	 * @return the login_id
	 */
	public String getLogin_id() {
		return login_id;
	}

	/**
	 * @param login_id the login_id to set
	 */
	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
    
}
