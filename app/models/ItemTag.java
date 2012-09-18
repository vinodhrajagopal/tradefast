package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.MinLength;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@Entity
@Table(name="item_tags") 
public class ItemTag extends Model {
	@Id
	public Long id; //This id is really dummy one.. just to satisfy some EBean oddities on validation and persistence
	
	@ManyToOne
	@JoinColumn(name = "item_id")
	public Item item;
	
	@MinLength(3)
	@MaxLength(20)
	public String tag;
	
	@MinLength(3)
	@MaxLength(20)
	public String normalizedTag;	
	
	/**
	 * Constructor for tests
	 * @param item
	 * @param tag
	 */
	public ItemTag(Item item, String tag) {
		this.item = item;
		this.tag = tag;
		this.normalizedTag = this.tag.toLowerCase();
	}
	
	public static Finder<Long,ItemTag> find = new Finder<Long, ItemTag>(Long.class, ItemTag.class);
	
	public static List<ItemTag> findTagsByItemId(Long itemId) {
		return itemId != null ? find.where("item_id = " + itemId).findList() : null;
	}
}