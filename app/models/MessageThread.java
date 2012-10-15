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

import play.db.ebean.Model;

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
	@JoinColumn(name = "creator")
	public User creator;
	
	@OneToMany(mappedBy = "thread", cascade=CascadeType.ALL)
	public List<Message> messages;
}