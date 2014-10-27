/**
 * 
 */
package hello;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Varun
 *
 */
public class BankAccount {
	private String ba_id;
    private String account_name;
	@NotEmpty @NotNull @Size(min=2)
    private String routing_number;
	@NotEmpty @NotNull @Size(min=2)
    private String account_number;
    
    /**
     * Default Constructor
     */
    public BankAccount() {	
    	
    }

    /**
     * Parameterized constructor
     * @param ba_id
     * @param account_name
     * @param routing_number
     * @param account_number
     */
    public BankAccount(String ba_id, String account_name, String routing_number, String account_number){
    	this.ba_id = ba_id;
    	this.account_name = account_name;
    	this.routing_number = routing_number;
    	this.account_number = account_number;
    }

	/**
	 * @return the ba_id
	 */
	public String getBa_id() {
		return ba_id;
	}

	/**
	 * @param ba_id the ba_id to set
	 */
	public void setBa_id(String ba_id) {
		this.ba_id = ba_id;
	}

	/**
	 * @return the account_name
	 */
	public String getAccount_name() {
		return account_name;
	}

	/**
	 * @param account_name the account_name to set
	 */
	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	/**
	 * @return the routing_number
	 */
	public String getRouting_number() {
		return routing_number;
	}

	/**
	 * @param routing_number the routing_number to set
	 */
	public void setRouting_number(String routing_number) {
		this.routing_number = routing_number;
	}

	/**
	 * @return the account_number
	 */
	public String getAccount_number() {
		return account_number;
	}

	/**
	 * @param account_number the account_number to set
	 */
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
    
}
