package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import exceptions.CannotCreateThreadForOwnPostException;
import exceptions.PostCannotBeNullException;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="message_threads") 
public class MessageThread extends Model {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="message_threads_id_seq")
	public Long id;
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	public Post post;
	
	@ManyToOne
	@JoinColumn(name = "created_by")
	public User createdBy;
	
	@OneToMany(mappedBy = "thread", cascade=CascadeType.ALL)
	public List<Message> messages;
	
	public static Finder<Long,MessageThread> find = new Finder<Long, MessageThread>(Long.class, MessageThread.class);
	
	public MessageThread(Post post, User createdBy) {
		this.post = post;
		this.createdBy = createdBy;
	}
	
	public static MessageThread getMessageThread(Long threadId) {
		return find.byId(threadId);
	}
	
	public static MessageThread create(Post post, User createdBy) throws PostCannotBeNullException, CannotCreateThreadForOwnPostException {
		if (post == null) {
			throw new PostCannotBeNullException();
		}

		if (post.createdBy.equals(createdBy)) {
			throw new CannotCreateThreadForOwnPostException();
		}
		
		MessageThread newThread = new MessageThread(post,createdBy);
		newThread.save();
		return newThread;
	}
}