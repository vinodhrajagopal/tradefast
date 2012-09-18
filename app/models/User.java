package models;

import java.util.*;
import javax.persistence.*;

import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.*;

/**
 * User entity managed by Ebean
 */
@Entity 
@Table(name="users")
public class User extends Model {

    @Id
    @Constraints.Required
    @Formats.NonEmpty
    public String emailId;

    @Constraints.Required
    public String password;
    
    @Constraints.Required
    public String screenName ;
    
    /**
     * For test purposes
     * @param emailId
     * @param screenName
     * @param password
     */
    public User(String emailId, String password, String screenName) {
    	this.emailId = emailId;
    	this.password = password;
    	this.screenName = screenName;
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
        return "User(" + emailId + ")";
    }

}
