package hello;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;

/**
 * @author Varun User Controller class
 */
@RestController
public class UserController extends WebMvcConfigurerAdapter {
	final AtomicLong userId = new AtomicLong();
	final AtomicLong idCardLong = new AtomicLong();
	private final AtomicLong webLoginLong = new AtomicLong();
	private final AtomicLong bankAccLong = new AtomicLong();
	SimpleDateFormat sdf = null;
	Date date = null;
	String emptyString = "";

	/**
	 * Create MongoDB collection with Mongolab
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @throws MongoException
	 */
	public static DBCollection getCollection(String collectionName) throws UnknownHostException, MongoException {
		String uri = "mongodb://ds047950.mongolab.com:47950/";
		MongoCredential mongoCredential = MongoCredential.createMongoCRCredential("varunkakuste", "cmpe273_assignment2_varun", "varun@124".toCharArray());
		MongoClientURI mongoClientURI = new MongoClientURI(uri);

		MongoClient mongoClient = new MongoClient(mongoClientURI);

		DB db = mongoClient.getDB("cmpe273_assignment2_varun");
		db.authenticate("varunkakuste", "varun@124".toCharArray());

		DBCollection dbobj = db.getCollection(collectionName);
		return dbobj;
	}

	/**
	 * Create User
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/api/v1/users", method = RequestMethod.POST, consumes = "application/json")
	public ResponseEntity<User> createUser(@RequestBody @Valid User user, String user_id, BindingResult bindingResult) throws UnknownHostException, MongoException {
		ResponseEntity<User> respEntity = null;
		DBCollection collection = UserController.getCollection("users");

		DBObject objQuery;
		DBCursor cursor;

		if (bindingResult.hasErrors()) {
			respEntity = new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		} else {
			Long newUserId = userId.incrementAndGet();
			String newUserIdStr = "u-" + newUserId;

			date = new Date();
			sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
			user.setCreated_at(sdf.format(date));
			user.setUpdated_at(sdf.format(date));
			
			User newUser = new User(newUserIdStr, chkIsNull(user.getEmail()),
					chkIsNull(user.getPassword()), chkIsNull(user.getName()),
					user.getCreated_at(), user.getUpdated_at());

			// Checks if the USer already exists,
			// if exists creates new User ID
			cursor = collection.find();
			String existUser = null;
			while (cursor.hasNext()) {
				objQuery = cursor.next();
				existUser = objQuery.get("user_id").toString();
				if (existUser != null && newUserIdStr.equalsIgnoreCase(existUser)) {
					newUserId = userId.incrementAndGet();
					newUserIdStr = "u-" + newUserId;
				}
			}

			// Insert the new User in the MongoDB collection
			BasicDBObject objToInsert = new BasicDBObject("user_id", newUserIdStr)
					.append("email", newUser.getEmail())
					.append("password", newUser.getPassword())
					.append("name", newUser.getName())
					.append("created_at", newUser.getCreated_at())
					.append("updated_at", newUser.getUpdated_at());

			collection.insert(objToInsert);

			respEntity = new ResponseEntity<User>(newUser, HttpStatus.CREATED);

		}
		return respEntity;
	}

	/**
	 * View User
	 * 
	 * @param user_id
	 * @return
	 * @throws MongoException
	 * @throws UnknownHostException
	 */
	@RequestMapping(value = "/api/v1/users/{user_id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<User> viewUser(@PathVariable String user_id) throws UnknownHostException, MongoException {
		User user = new User();
		ResponseEntity<User> respEntity = null;
		DBCollection collection = UserController.getCollection("users");
		DBObject obj = null;
		DBObject objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
		DBCursor cursor = collection.find(objQuery);

		if (cursor.hasNext()) {
			obj = cursor.next();
			// obj.removeField("_id");
			user.setUserId(obj.get("user_id").toString());
			user.setEmail(obj.get("email").toString());
			user.setPassword(obj.get("password").toString());
			user.setName(obj.get("name").toString());
			user.setCreated_at(obj.get("created_at").toString());
			user.setUpdated_at(obj.get("updated_at").toString());
			respEntity = new ResponseEntity<User>(user, HttpStatus.OK);
		} else {
			respEntity = new ResponseEntity<User>(user, HttpStatus.NO_CONTENT);
		}
		return respEntity;
	}

	/**
	 * Update User
	 * 
	 * @param user
	 * @param user_id
	 * @return
	 * @throws MongoException 
	 * @throws UnknownHostException 
	 */
	@RequestMapping(value = "/api/v1/users/{user_id}", method = RequestMethod.PUT)
	public ResponseEntity<User> updateUser(@RequestBody @Valid User user, @PathVariable String user_id, BindingResult bindingResult) throws UnknownHostException, MongoException {
		date = new Date();
		sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		User updatedUser = new User();
		ResponseEntity<User> respEntity = null;
		DBObject obj = null;
		DBObject newObj = null;
		DBObject objQuery = null;
		DBCursor cursor = null;
		DBCollection collection = UserController.getCollection("users");

		if (bindingResult.hasErrors()) {
			respEntity = new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		} else {
			objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
			cursor = collection.find(objQuery);
			
			if (cursor.hasNext()) {
				obj = cursor.next();
				
				newObj = new BasicDBObject("user_id", user_id)
					.append("email", user.getEmail())
					.append("password", user.getPassword())
					.append("name", user.getName())
					.append("created_at", obj.get("created_at").toString())
					.append("updated_at", sdf.format(date));

				collection.findAndModify(obj, newObj);
				
				updatedUser.setUserId(newObj.get("user_id").toString());
				updatedUser.setEmail(newObj.get("email").toString());
				updatedUser.setPassword(newObj.get("password").toString());
				updatedUser.setName(newObj.get("name").toString());
				updatedUser.setCreated_at(newObj.get("created_at").toString());
				updatedUser.setUpdated_at(newObj.get("updated_at").toString());

				respEntity = new ResponseEntity<User>(updatedUser, HttpStatus.CREATED);
			} else {
				respEntity = new ResponseEntity<User>(updatedUser, HttpStatus.NO_CONTENT);
			}
		}
		return respEntity;
	}

	/**
     * Create ID Card
     * @param idCard
     * @param user_id
     * @return
	 * @throws MongoException 
	 * @throws UnknownHostException 
     */
    @RequestMapping(value="/api/v1/users/{user_id}/idcards", method=RequestMethod.POST, consumes="application/json")
    public ResponseEntity<IdCard> createIdCard(@RequestBody @Valid IdCard idCard, @PathVariable String user_id, BindingResult bindingResult) throws UnknownHostException, MongoException {
    	ResponseEntity<IdCard> respEntity = null;
    	DBCollection usersCollection = UserController.getCollection("users");
		DBCollection idCardscollection = UserController.getCollection("idcards");
		DBObject objQuery;
		DBObject objIdCardToInsert;
		DBCursor cursor;
		DBCursor cursorIdCards;
		Long newCardId;
		String newCardIdStr = emptyString;
		String idCardExist = emptyString;
		
		if (bindingResult.hasErrors()) {
    		respEntity = new ResponseEntity<IdCard>(idCard, HttpStatus.BAD_REQUEST);
        } else {
        	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
			cursor = usersCollection.find(objQuery);
			if(cursor.hasNext()) {
				newCardId = idCardLong.incrementAndGet();
		    	newCardIdStr = "c-" + newCardId;
		    	
		    	cursorIdCards = idCardscollection.find();
				while (cursorIdCards.hasNext()) {
					objQuery = cursorIdCards.next();
					idCardExist = objQuery.get("card_id").toString();
					if (idCardExist != null && newCardIdStr.equalsIgnoreCase(idCardExist)) {
						newCardId = idCardLong.incrementAndGet();
				    	newCardIdStr = "c-" + newCardId;
					}
				}

				idCard.setCard_id(newCardIdStr);
				// Insert the new User in the MongoDB collection
				objIdCardToInsert = new BasicDBObject();
					objIdCardToInsert.put("user_id", user_id);
					objIdCardToInsert.put("card_id", idCard.getCard_id());
					objIdCardToInsert.put("card_name", idCard.getCard_name());
					objIdCardToInsert.put("card_number", idCard.getCard_number());
					objIdCardToInsert.put("expiration_date", idCard.getExpiration_date());
				idCardscollection.insert(objIdCardToInsert);
		    	
				respEntity = new ResponseEntity<IdCard>(idCard, HttpStatus.CREATED);
			} else {
				respEntity = new ResponseEntity<IdCard>(idCard, HttpStatus.NO_CONTENT);
			}
		}
    	return respEntity;
    }

    /**
     * List All ID Cards
     * @param user_id
     * @return
     * @throws MongoException 
     * @throws UnknownHostException 
     */
    @RequestMapping(value="/api/v1/users/{user_id}/idcards", method=RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<IdCard>> listAllIdCard(@PathVariable String user_id) throws UnknownHostException, MongoException {
    	List<IdCard> idCardsList = new ArrayList<IdCard>();
    	IdCard idCard = null;
    	ResponseEntity<List<IdCard>> respEntity = null;
    	DBCollection idCardscollection = UserController.getCollection("idcards");
    	DBObject objQuery;
		DBCursor cursor;
    	
    	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
		cursor = idCardscollection.find(objQuery);
		if(cursor.hasNext()) {
			while(cursor.hasNext()) {
				objQuery = cursor.next();
				idCard = new IdCard();
				
				idCard.setCard_id(objQuery.get("card_id").toString());
				idCard.setCard_name(objQuery.get("card_name").toString());
				idCard.setCard_number(objQuery.get("card_number").toString());
				idCard.setExpiration_date(objQuery.get("expiration_date").toString());
				idCardsList.add(idCard);
			}
			respEntity = new ResponseEntity<List<IdCard>>(idCardsList, HttpStatus.OK);
		} else {
			respEntity = new ResponseEntity<List<IdCard>>(idCardsList, HttpStatus.NO_CONTENT);
		}
    	return respEntity;
    }
    
    /**
     * Delete ID Card
     * @param user_id
     * @param card_id
     * @return
     * @throws MongoException 
     * @throws UnknownHostException 
     */
    @RequestMapping(value="/api/v1/users/{user_id}/idcards/{card_id}", method=RequestMethod.DELETE)
    public ResponseEntity<IdCard> deleteIdCard(@PathVariable String user_id, @PathVariable String card_id) throws UnknownHostException, MongoException {
    	DBCollection idCardscollection = UserController.getCollection("idcards");
    	DBObject objQuery;
		DBCursor cursor;
		IdCard idCard = null;
    	
    	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).add("card_id", card_id).get();
		cursor = idCardscollection.find(objQuery);
		if (cursor.hasNext()) {
			objQuery = cursor.next();
			idCardscollection.remove(objQuery);
		}

		return new ResponseEntity<IdCard>(idCard, HttpStatus.NO_CONTENT); 
    }
    
    /**
     * Create ID Card
     * @param idCard
     * @param user_id
     * @return
	 * @throws MongoException 
	 * @throws UnknownHostException 
     */
    @RequestMapping(value="/api/v1/users/{user_id}/weblogins", method=RequestMethod.POST, consumes="application/json")
    public ResponseEntity<WebLogin> createWebLogin(@RequestBody @Valid WebLogin webLogin, @PathVariable String user_id, BindingResult bindingResult) throws UnknownHostException, MongoException {
    	ResponseEntity<WebLogin> respEntity = null;
    	DBCollection usersCollection = UserController.getCollection("users");
		DBCollection webLogincollection = UserController.getCollection("weblogins");
		DBObject objQuery;
		DBObject objWebLoginToInsert;
		DBCursor cursor;
		DBCursor cursorWebLogin;
		Long newWebLogin;
		String newWebLoginStr = emptyString;
		String webLoginExist = emptyString;
		
		if (bindingResult.hasErrors()) {
    		respEntity = new ResponseEntity<WebLogin>(webLogin, HttpStatus.BAD_REQUEST);
        } else {
        	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
			cursor = usersCollection.find(objQuery);
			if(cursor.hasNext()) {
				newWebLogin = webLoginLong.incrementAndGet();
				newWebLoginStr = "l-" + newWebLogin;
		    	
				cursorWebLogin = webLogincollection.find();
				while (cursorWebLogin.hasNext()) {
					objQuery = cursorWebLogin.next();
					webLoginExist = objQuery.get("login_id").toString();
					if (webLoginExist != null && newWebLoginStr.equalsIgnoreCase(webLoginExist)) {
						newWebLogin = webLoginLong.incrementAndGet();
						newWebLoginStr = "l-" + newWebLogin;
					}
				}

				webLogin.setLogin_id(newWebLoginStr);
				// Insert the new WebLogin in the MongoDB collection
				objWebLoginToInsert = new BasicDBObject();
				objWebLoginToInsert.put("user_id", user_id);
				objWebLoginToInsert.put("login_id", webLogin.getLogin_id());
				objWebLoginToInsert.put("url", webLogin.getUrl());
				objWebLoginToInsert.put("login", webLogin.getLogin());
				objWebLoginToInsert.put("password", webLogin.getPassword());
				webLogincollection.insert(objWebLoginToInsert);
		    	
				respEntity = new ResponseEntity<WebLogin>(webLogin, HttpStatus.CREATED);
			} else {
				respEntity = new ResponseEntity<WebLogin>(webLogin, HttpStatus.NO_CONTENT);
			}
		}
    	return respEntity;
    }
    
    /**
     * List All Web-site Logins
     * @param user_id
     * @return
     * @throws MongoException 
     * @throws UnknownHostException 
     */
    @RequestMapping(value="/api/v1/users/{user_id}/weblogins", method=RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<WebLogin>> listAllWebLogins(@PathVariable String user_id) throws UnknownHostException, MongoException {
    	
    	List<WebLogin> webLoginList = new ArrayList<WebLogin>();
    	ResponseEntity<List<WebLogin>> respEntity = null;
    	WebLogin webLogin = null;
    	DBCollection webLogincollection = UserController.getCollection("weblogins");
    	DBObject objQuery;
		DBCursor cursor;
    	
    	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
		cursor = webLogincollection.find(objQuery);
		if(cursor.hasNext()) {
			while(cursor.hasNext()) {
				objQuery = cursor.next();
				webLogin = new WebLogin();
				webLogin.setLogin_id(objQuery.get("login_id").toString());
				webLogin.setUrl(objQuery.get("url").toString());
				webLogin.setLogin(objQuery.get("login").toString());
				webLogin.setPassword(objQuery.get("password").toString());
				
				webLoginList.add(webLogin);
			}
			respEntity = new ResponseEntity<List<WebLogin>>(webLoginList, HttpStatus.OK);
		} else {
			respEntity = new ResponseEntity<List<WebLogin>>(webLoginList, HttpStatus.NO_CONTENT);
		}
    	return respEntity;
    }
    
    /**
     * Delete Web Login
     * @param user_id
     * @param login_id
     * @return
     * @throws MongoException 
     * @throws UnknownHostException 
     */
    @RequestMapping(value="/api/v1/users/{user_id}/weblogins/{login_id}", method=RequestMethod.DELETE)
    public ResponseEntity<WebLogin> deleteWebLogin(@PathVariable String user_id, @PathVariable String login_id) throws UnknownHostException, MongoException {
    	DBCollection webLogincollection = UserController.getCollection("weblogins");
    	DBObject objQuery;
		DBCursor cursor;
		WebLogin webLogin = null;
    	
    	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).add("login_id", login_id).get();
		cursor = webLogincollection.find(objQuery);
		if (cursor.hasNext()) {
			objQuery = cursor.next();
			webLogincollection.remove(objQuery);
		}

		return new ResponseEntity<WebLogin>(webLogin, HttpStatus.NO_CONTENT); 
    }

    /**
     * Create Bank Account
     * @param bankAcc
     * @param user_id
     * @return
     */
    @RequestMapping(value="/api/v1/users/{user_id}/bankaccounts", method=RequestMethod.POST, consumes="application/json")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody @Valid BankAccount bankAcc, @PathVariable String user_id, BindingResult bindingResult) throws UnknownHostException, MongoException {
    	ResponseEntity<BankAccount> respEntity = null;
    	DBCollection usersCollection = UserController.getCollection("users");
		DBCollection bankAccountsCollection = UserController.getCollection("bankaccounts");
		DBObject objQuery;
		DBObject objBankAccountToInsert;
		DBCursor cursor;
		DBCursor cursorBankAccount;
		Long newBankAccount;
		String newBankAccountStr = emptyString;
		String bankAccountExist = emptyString;
		
		if (bindingResult.hasErrors()) {
    		respEntity = new ResponseEntity<BankAccount>(bankAcc, HttpStatus.BAD_REQUEST);
        } else {
        	
        	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
			cursor = usersCollection.find(objQuery);
			if(cursor.hasNext()) {
				newBankAccount = bankAccLong.incrementAndGet();
		    	newBankAccountStr = "b-" + newBankAccount;
		    	
		    	cursorBankAccount = bankAccountsCollection.find();
				while (cursorBankAccount.hasNext()) {
					objQuery = cursorBankAccount.next();
					bankAccountExist = objQuery.get("ba_id").toString();
					if (bankAccountExist != null && newBankAccountStr.equalsIgnoreCase(bankAccountExist)) {
						newBankAccount = bankAccLong.incrementAndGet();
				    	newBankAccountStr = "b-" + newBankAccount;
					}
				}

				//call to consume Rest API Part 2
				bankAcc.setAccount_name(consumeRestToGetAccountName(bankAcc.getRouting_number(), bankAcc.getAccount_name()));
				bankAcc.setBa_id(newBankAccountStr);
				// Insert the new WebLogin in the MongoDB collection
				objBankAccountToInsert = new BasicDBObject();
				objBankAccountToInsert.put("user_id", user_id);
				objBankAccountToInsert.put("ba_id", bankAcc.getBa_id());
				objBankAccountToInsert.put("account_name", bankAcc.getAccount_name());
				objBankAccountToInsert.put("routing_number", bankAcc.getRouting_number());
				objBankAccountToInsert.put("account_number", bankAcc.getAccount_number());
				bankAccountsCollection.insert(objBankAccountToInsert);
		    	
				respEntity = new ResponseEntity<BankAccount>(bankAcc, HttpStatus.CREATED);
			} else {
				respEntity = new ResponseEntity<BankAccount>(bankAcc, HttpStatus.NO_CONTENT);
			}
		}
    	return respEntity;
    }

    /**
     * List All Bank Accounts
     * @param user_id
     * @return
     */
    @RequestMapping(value="/api/v1/users/{user_id}/bankaccounts", method=RequestMethod.GET, produces="application/json")
    public ResponseEntity<List<BankAccount>> listAllBankAcc(@PathVariable String user_id) throws UnknownHostException, MongoException {
    	List<BankAccount> bankAccList = new ArrayList<BankAccount>();
    	ResponseEntity<List<BankAccount>> respEntity = null;
    	BankAccount bankAccount = null;
    	DBCollection bankAccountsCollection = UserController.getCollection("bankaccounts");
    	DBObject objQuery;
		DBCursor cursor;
    	
    	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).get();
		cursor = bankAccountsCollection.find(objQuery);
		if(cursor.hasNext()) {
			while(cursor.hasNext()) {
				objQuery = cursor.next();
				bankAccount = new BankAccount();
				
				bankAccount.setBa_id(objQuery.get("ba_id").toString());
				bankAccount.setAccount_name(objQuery.get("account_name").toString());
				bankAccount.setRouting_number(objQuery.get("routing_number").toString());
				bankAccount.setAccount_number(objQuery.get("account_number").toString());
				
				bankAccList.add(bankAccount);
			}
			respEntity = new ResponseEntity<List<BankAccount>>(bankAccList, HttpStatus.OK);
		} else {
			respEntity = new ResponseEntity<List<BankAccount>>(bankAccList, HttpStatus.NO_CONTENT);
		}
    	return respEntity;
    }
    
    /**
	 * Delete Bank Account
	 * @param user_id
	 * @param ba_id
	 * @return
	 */
	@RequestMapping(value="/api/v1/users/{user_id}/bankaccounts/{ba_id}", method=RequestMethod.DELETE)
    public ResponseEntity<BankAccount> deleteBankAcc(@PathVariable String user_id, @PathVariable String ba_id) throws UnknownHostException, MongoException {
		DBCollection bankAccountsCollection = UserController.getCollection("bankaccounts");
    	DBObject objQuery;
		DBCursor cursor;
		BankAccount bankAccount = null;
    	
    	objQuery = BasicDBObjectBuilder.start().add("user_id", user_id).add("ba_id", ba_id).get();
		cursor = bankAccountsCollection.find(objQuery);
		if (cursor.hasNext()) {
			objQuery = cursor.next();
			bankAccountsCollection.remove(objQuery);
		}

		return new ResponseEntity<BankAccount>(bankAccount, HttpStatus.NO_CONTENT); 
    }

	/**
	 * Get Account name by consuming Rest
	 * @param routingNum
	 * @return
	 */
	public String consumeRestToGetAccountName(String routingNum, String accountName) {
		String result = emptyString;
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> entity = restTemplate.getForEntity("http://www.routingnumbers.info/api/data.json?rn=" + routingNum, String.class);
		JsonParser jsonParser = new JacksonJsonParser();
 		Map<String,Object> resbody = jsonParser.parseMap(entity.getBody());
		if((resbody.get("code").toString().equals("200"))) {
			result = resbody.get("customer_name").toString();
		} else {
			result = chkIsNull(accountName);
		}
		return result;
	}
	
	/**
	 * Method is to check if the String is empty or NULL
	 * 
	 * @param str
	 * @return String
	 */
	public String chkIsNull(String str) {
		String result = null;
		if (null == str || emptyString.equalsIgnoreCase(str)) {
			result = emptyString;
		} else {
			result = str;
		}
		return result;
	}

}
