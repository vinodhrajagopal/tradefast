package junit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import models.User;

import org.junit.Test;

public class UserTest extends ModelTest<User> {

	@Override
	public List<User> fixturesToUnload() { 
		return User.findAll();
	}
	
	@Test
	public void createNewUser() {
		// Create a new user and save it
        User newUser = new User("bob@gmail.com", "secret", "Bob");
        newUser.save();
        assertNotNull("A new use should have been created", User.find.byId(newUser.emailId));
	}
	

    @Test
    public void authenticateUser() {
        // Create a new user and save it
        new User("bob@gmail.com", "secret", "Bob").save();
        
        // Test 
        assertNotNull("Should have returned Bob User", User.authenticate("bob@gmail.com", "secret"));
        assertNull("Should not have returned User", User.authenticate("bob@gmail.com", "badpassword"));
        assertNull("Should not have returned User", User.authenticate("tom@gmail.com", "secret"));
    }
}
