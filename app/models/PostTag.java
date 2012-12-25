package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.db.ebean.Model;

@Entity
@Table(name="post_tags")
public class PostTag extends Model {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="post_tags_id_seq")
	public Long id; //TODO: Look for EmbeddedId annotation. This id is really dummy one.. just to satisfy some EBean oddities on validation and persistence
	
	@ManyToOne
	@JoinColumn(name = "post_id")
	public Post post;
	
	@MinLength(3)
	@MaxLength(20)
	public String tag;
	
	@MinLength(3)
	@MaxLength(20)
	public String normalizedTag;	
	
	/**
	 * Constructor for tests
	 * @param post
	 * @param tag
	 */
	public PostTag(Post post, String tag) {
		this.post = post;
		this.tag = tag;
		this.normalizedTag = this.tag.toLowerCase();
	}
	
	public static Finder<Long,PostTag> find = new Finder<Long, PostTag>(Long.class, PostTag.class);
	
	public static List<PostTag> findTagsByPostId(Long postId) {
		return postId != null ? find.where("post_id = " + postId).findList() : null;
	}
	
	//TODO: Remove this.. this is just for testing
	@Override
	public void save() {
		super.save();
	}
}