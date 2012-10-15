package models;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.Valid;
import javax.validation.constraints.Digits;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;

import controllers.UserController;

import play.data.validation.Constraints.*;
import play.data.format.*;
import play.db.ebean.Model;

@Entity
@Table(name="posts") 
public class Post extends Model {
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="posts_id_seq")
	public Long id;
	
	@Required
	@MaxLength(140)
	public String title;
	
	@MaxLength(1000)
	public String description;
	
	@Digits(integer=10, fraction=2)
	public float price;
	
	@Required
	@Min(1)
	@Max(24)
	public int postDuration;

	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date createdTime;
	
	@Formats.DateTime(pattern="yyyy-MM-dd HH:mm:ss")
	public Date endTime;
	
	@Required
	@ManyToOne
	@JoinColumn
	public String sellerId;

	@ManyToOne
	@JoinColumn
	public String buyerId;
	
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
	
	public boolean isFree;
	
	public boolean sold;
	
	public boolean expired;
	
	public boolean deleted;
	
	@Valid
	@OneToMany(mappedBy = "post", cascade=CascadeType.ALL)
	public List<PostTag> tags;//TODO Change this to Set
	
	@OneToMany(mappedBy = "post", cascade=CascadeType.ALL)
	public Set<MessageThread> messageThreads;

	/**
	 * Constructor for tests
	 * @param title
	 * @param price
	 * @param postDuration
	 * @param city
	 * @param state
	 * @param country
	 * @param zipcode
	 */
	public Post(String title, float price, int postDuration, String city, String state, String country, String zipcode) {
		this.title = title;
		this.price = price;
		this.postDuration = postDuration;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipcode = zipcode;
	}
	
	public Post() {}
	
	public static Finder<Long,Post> find = new Finder<Long, Post>(Long.class, Post.class);
	
	public static Set<Post> listPosts() {
		String currentUserName = UserController.currentUserName();
		if (currentUserName != null) {
			return getBaseQuery(currentUserName).ne("sellerId", currentUserName).findSet();
		} else {
			return find.where().
						eq("deleted", false).
						eq("expired", false).
						findSet();
		}
	}
	
	public static Set<Post> listPostsCreatedBy(String userName) {
		return userName == null || userName.isEmpty() ? null : getBaseQuery().eq("sellerId", userName).findSet(); 
	}
	  
	private static void removeEmptyAndDuplicateTagsAndNormalize(Post post) {
		if (post.tags != null && post.tags.size() > 0) {
			Set<String> normalizedTags = new HashSet<String>();
			for(int i = post.tags.size()-1;i>=0;i--) {
				PostTag postTag = post.tags.get(i);
				if (postTag == null || postTag.tag == null || postTag.tag.isEmpty()) {
					post.tags.remove(i);
				} else {
					postTag.tag = postTag.tag.trim();
					postTag.normalizedTag = postTag.tag.toLowerCase();
					if (normalizedTags.contains(postTag.normalizedTag)) {
						post.tags.remove(i);
					} else {
						normalizedTags.add(postTag.normalizedTag);
					}
				}
			}
		}
	}

	public static void create(Post post) {
		removeEmptyAndDuplicateTagsAndNormalize(post);
		post.save();
	}
	
	public static void update(Post post) {
		removeEmptyAndDuplicateTagsAndNormalize(post);
		post.update();		
	}
	  
	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	public static Post get(Long id) {
		return find.byId(id);
	}
	
	public static List<String> expiryDurations() {
		List<String> hours = new ArrayList<String>();
        hours.add("1");
        hours.add("2");
        hours.add("4");
        hours.add("6");
        hours.add("12");
        hours.add("24");
        return hours;
	}
	
	private static ExpressionList<Post> getBaseQuery() {
		return find.fetch("messageThreads").
					fetch("messageThreads.creator", "userName").
					fetch("messageThreads.messages", "body, createdTime").
					fetch("messageThreads.messages.from", "userName").
					where().eq("deleted", false).
							eq("expired", false);		
	}
	
	private static ExpressionList<Post> getBaseQuery(String viewingUser) {
		return viewingUser == null ? getBaseQuery() : getBaseQuery().or(Expr.eq("messageThreads.creator.userName",viewingUser), Expr.isNull("messageThreads.creator.userName"));
	}
}