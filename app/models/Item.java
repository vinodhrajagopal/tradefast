package models;

import java.util.*;

import javax.annotation.Nonnegative;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Digits;

import controllers.UserController;

import play.data.validation.Constraints.*;
import play.data.format.*;
import play.db.ebean.Model;

@Entity
@Table(name="items") 
public class Item extends Model {
	@Id
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
	public int saleDuration;

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
	@Digits(fraction = 0, integer = 5)
	@Nonnegative
	public int zipcode;
	
	@Required
	@MaxLength(100)
	public String country;
	
	public boolean isFree;
	
	public boolean sold;
	
	public boolean expired;
	
	public boolean deleted;
	
	@Valid
	@OneToMany(mappedBy = "item", cascade=CascadeType.ALL)
	public List<ItemTag> tags;
	

	/**
	 * Constructor for tests
	 * @param title
	 * @param price
	 * @param saleDuration
	 * @param city
	 * @param state
	 * @param country
	 * @param zipcode
	 */
	public Item(String title, float price, int saleDuration, String city, String state, String country, int zipcode) {
		this.title = title;
		this.price = price;
		this.saleDuration = saleDuration;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipcode = zipcode;
	}
	
	public Item() {}
	
	public static Finder<Long,Item> find = new Finder<Long, Item>(Long.class, Item.class);
	  
	public static Set<Item> listItems() {
		String currentUserId = UserController.getCurrentUserId(); 
		if (currentUserId != null) {
			return find.where().eq("deleted", false).
								eq("expired", false).
								ne("sellerId", currentUserId).findSet();
		} else {
			return find.where().eq("deleted", false).
								eq("expired", false).findSet();	
		}
	}
	
	public static Set<Item> listItemsPostedBy(String userName) {
		return userName == null ? null :
								find.where().eq("deleted", false).
								eq("expired", false).
								eq("sellerId", userName).
								findSet();
	}
	  
	private static void removeEmptyAndDuplicateTagsAndNormalize(Item item) {
		if (item.tags != null && item.tags.size() > 0) {
			Set<String> normalizedTags = new HashSet<String>();
			for(int i = item.tags.size()-1;i>=0;i--) {
				ItemTag itemTag = item.tags.get(i);
				if (itemTag == null || itemTag.tag == null || itemTag.tag.isEmpty()) {
					item.tags.remove(i);
				} else {
					itemTag.tag = itemTag.tag.trim();
					itemTag.normalizedTag = itemTag.tag.toLowerCase();
					if (normalizedTags.contains(itemTag.normalizedTag)) {
						item.tags.remove(i);
					} else {
						normalizedTags.add(itemTag.normalizedTag);
					}
				}
			}
		}
	}

	public static void create(Item item) {
		removeEmptyAndDuplicateTagsAndNormalize(item);
		item.save();
	}
	
	public static void update(Item item) {
		removeEmptyAndDuplicateTagsAndNormalize(item);
		item.update();		
	}
	  
	public static void delete(Long id) {
		find.ref(id).delete();
	}
	
	public static Item get(Long id) {
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
	
}