package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


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
}