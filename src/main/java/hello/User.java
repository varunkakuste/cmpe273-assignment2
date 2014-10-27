package hello;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;


/**
 * @author Varun
 * User Bean
 */
public class User {
	
	@Id
	private String userId;
	@NotEmpty @NotNull @Email
    private String email;
	@NotEmpty @NotNull @Size(min=2)
    private String password;
	@NotEmpty @NotNull @Size(min=2, max=30)
    private String name;
    private String created_at;
    private String updated_at;
    
    /**
     * Default Constructor
     */
    public User(){
    	
    }
    
    /**
     * Parameterized Constructor
     * @param user_id
     * @param email
     * @param password
     * @param name
     * @param created_at
     * @param updated_at
     */
    public User(String userId, String email, String password, String name, String created_at, String updated_at) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the created_at
	 */
	public String getCreated_at() {
		return created_at;
	}

	/**
	 * @param created_at the created_at to set
	 */
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	/**
	 * @return the updated_at
	 */
	public String getUpdated_at() {
		return updated_at;
	}

	/**
	 * @param updated_at the updated_at to set
	 */
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

}
