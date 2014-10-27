/**
 * 
 */
package hello;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;

/**
 * @author Varun
 * IdCard Bean
 */
public class IdCard {
	
	@Id
	private String card_id;
	@NotEmpty @NotNull
    private String card_name;
    @NotEmpty @NotNull
    private String card_number;
    @NotEmpty @NotNull @Size(min=8, max=10)
    private String expiration_date;

    /**
     * Default Constructor
     */
    public IdCard(){	
    	
    }

    /**
     * Parameterized Constructor
     * @param card_id
     * @param card_name
     * @param card_number
     * @param expiration_date
     */
    public IdCard(String card_id, String card_name, String card_number, String expiration_date){
    	this.card_id = card_id;
    	this.card_name = card_name;
    	this.card_number = card_number;
    	this.expiration_date = expiration_date;
    }

	/**
	 * @return the card_id
	 */
	public String getCard_id() {
		return card_id;
	}

	/**
	 * @param card_id the card_id to set
	 */
	public void setCard_id(String card_id) {
		this.card_id = card_id;
	}

	/**
	 * @return the card_name
	 */
	public String getCard_name() {
		return card_name;
	}

	/**
	 * @param card_name the card_name to set
	 */
	public void setCard_name(String card_name) {
		this.card_name = card_name;
	}

	/**
	 * @return the card_number
	 */
	public String getCard_number() {
		return card_number;
	}

	/**
	 * @param card_number the card_number to set
	 */
	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}

	/**
	 * @return the expiration_date
	 */
	public String getExpiration_date() {
		return expiration_date;
	}

	/**
	 * @param expiration_date the expiration_date to set
	 */
	public void setExpiration_date(String expiration_date) {
		this.expiration_date = expiration_date;
	}

}
