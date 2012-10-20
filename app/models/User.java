package models;

import java.util.*;

import javax.annotation.Nonnegative;
import javax.persistence.*;
import javax.validation.constraints.Digits;

import controllers.routes;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import utils.extractor.UserData;

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
    
    public String locale;

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
    
	private static String PATH_DEFAULT_PROFILE_PIC = "images/default_profile_photo.jpeg";
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
    	this.picture = userData.picture;
    	this.city = userData.city;
    	this.state = userData.state;
    	this.country = userData.country;
    	this.zipcode = userData.zipcode;
    	this.currency = userData.currency;
    	this.locale = userData.locale;
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
    	User user =  find.where().eq("email_id", email).eq("password", password).findUnique();
    	return user;
    }
    
    // --
    
    public String toString() {
        return "User(" + userName + ")";
    }

    public static String getProfilePictureUrl(String pathToProfilePic) {
    	return pathToProfilePic != null ? pathToProfilePic : routes.Assets.at(PATH_DEFAULT_PROFILE_PIC).url();
    	
    }
}
