package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import controllers.UserController;
import exceptions.CannotCreateThreadForOwnPostException;
import exceptions.CannotUpdateNonParticipatingThread;
import exceptions.MessageCannotBeNullOrEmptyException;
import exceptions.PostCannotBeNullException;
import exceptions.UserNotLoggedInException;


import play.data.format.Formats;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
@Table(name="messages")
public class Message extends Model {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="messages_id_seq")
	public Long id;
	
	@ManyToOne
	@JoinColumn(name = "thread_id")
	public MessageThread thread;
	
	@ManyToOne
	@JoinColumn(name = "message_from")
	public User from;
	
	@Required
	@MinLength(1)
	@MaxLength(250)
	public String body;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date createdTime;
	
	private Message(MessageThread thread, User from, String body) {
		this.thread = thread;
		this.from = from;
		this.body = body;
		this.createdTime = new Date();
	}
	
	@PrePersist
	public void onCreate() {
		this.createdTime = new Date();
	}
	
	public static Message create(Long postId, Long threadId, String message) throws UserNotLoggedInException,
																		MessageCannotBeNullOrEmptyException, 
																		CannotUpdateNonParticipatingThread, 
																		PostCannotBeNullException, 
																		CannotCreateThreadForOwnPostException{
		String trimmedMessage = message == null ? null : message.trim();
		if (trimmedMessage == null || trimmedMessage.trim().isEmpty()) {
			throw new MessageCannotBeNullOrEmptyException();
		}
		
		User loggedInUser = UserController.loggedInUser();
		
		if (loggedInUser == null) {
			throw new UserNotLoggedInException();
		}
		
		Post post = Post.get(postId);
		
		if (post == null) {
			throw new PostCannotBeNullException();
		}

		MessageThread messageThread = MessageThread.getMessageThread(threadId);
		if (messageThread == null) {
			// What do you do now ? Be optimistic and create a new thread ??
			messageThread = MessageThread.create(post, loggedInUser);
		}
		
		if(!messageThread.creator.userName.equals(loggedInUser.userName) && !post.sellerId.equals(loggedInUser.userName)) {
			throw new CannotUpdateNonParticipatingThread();
		}
		
		Message newMessage = new Message(messageThread, loggedInUser, trimmedMessage);
		newMessage.save(); //TODO: This might throw an exception as well
		return newMessage;
	}
}