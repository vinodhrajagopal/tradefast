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

/**
 * User entity managed by Ebean
 */
@Entity 
@Table(name="users")
public class User extends Model {
    @Id
    @Constraints.Required
    public String userName ;
	
	@Constraints.Required
    public String password;
    
    @Constraints.Required
    @Formats.NonEmpty
    public String emailId;
    
	@MaxLength(100)
	public String addressLine;

	@Required
	@MaxLength(100)
	public String city;
	
	@Required
	@MaxLength(100)
	public String state;
	
	@Required
	@Digits(fraction = 0, integer = 5)
	@Nonnegative
	public int zipcode;
	
	@Required
	@MaxLength(100)
	public String country;
	
	@Required
	@MinLength(3)
	@MaxLength(3)
	public String currency;
    
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
