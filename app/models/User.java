package models;

import java.util.*;

import javax.annotation.Nonnegative;
import javax.persistence.*;
import javax.validation.constraints.Digits;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import utils.authentication.OAuthClient.UserData;

/**
 * User entity managed by Ebean
 */
@Entity 
@Table(name="users")
public class User extends Model {
    @Id
    @Constraints.Required
    public String userName ;
	
	//@Constraints.Required
    public String password;
    
    @Constraints.Required
    @Formats.NonEmpty
    @Constraints.Email
    public String emailId;
    
    public String picture;
    
    public String language;

	@Required
	@MinLength(3)
	@MaxLength(3)
	public String currency;
	
	@MaxLength(100)
	public String addressLine;

	@Required
	@MaxLength(100)
	public String city;
	
	@Required
	@MaxLength(100)
	public String state;
	
	@Required
	public String zipcode;
	
	@Required
	@MaxLength(100)
	public String country;
	

    
    /**
     * For test purposes
     * @param emailId
     * @param screenName
     * @param password
     */
    public User(String emailId, String password, String userName) {
    	this.emailId = emailId;
    	this.password = password;
    	this.userName = userName;
    }
    
    public User(UserData userData) {
    	this.userName = userData.username;
    	this.emailId = userData.email;
    	this.picture = userData.pic;
    	if (userData.current_location != null) {
    		this.city = userData.current_location.city;
        	this.state = userData.current_location.state;
        	this.country = userData.current_location.country;
        	this.zipcode = userData.current_location.zip;
    	}
    	if (userData.currency != null) {
    		this.currency = userData.currency.user_currency;
    	}
    }
    
    // -- Queries
    
    public static Model.Finder<String,User> find = new Model.Finder<String, User>(String.class, User.class);
    
    /**
     * Retrieve all users.
     */
    public static List<User> findAll() {
        return find.all();
    }

    /**
     * Retrieve a User from email.
     */
    public static User findByEmail(String email) {
        return find.where().eq("email_id", email).findUnique();
    }
    
    /**
     * Retrieve a User from userName.
     */
    public static User findByUsername(String userName) {
        return find.where().eq("user_name", userName).findUnique();
    }
    
    /**
     * Authenticate a User.
     */
    public static User authenticate(String email, String password) {
        return find.where()
            .eq("email_id", email)
            .eq("password", password)
            .findUnique();
    }
    
    // --
    
    public String toString() {
        return "User(" + userName + ")";
    }

}
