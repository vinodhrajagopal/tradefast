package controllers.search;

import java.util.HashSet;
import java.util.Set;


import models.Post;

import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;


import views.html.searchResults;

public class SearchController extends Controller {
	
	private static final String QUERY_PARAM_SEARCH_TERM = "searchTerm";
	
	public static Result search() {
		DynamicForm data = form().bindFromRequest();
		String searchTerm = data.get(QUERY_PARAM_SEARCH_TERM);
		Set<Post> results = null;
		if (searchTerm != null && !searchTerm.isEmpty()) {
			results = dbSearch(searchTerm, false);
		}
		return ok(searchResults.render(results));
	}
	
	/**
	 * The default database search. 
	 * This search looks for the searchTerm in
	 * 1.Post title
	 * 2.Post description
	 * 3.Post tags
	 * @param searchTerm
	 * @return
	 */
	private static Set<Post> dbSearch(String searchTerm, boolean searchLocation) {
		if (!searchLocation) {
			String normalizedSearchTerm = "%" + searchTerm.toLowerCase() + "%";
			/**
			 * TODO: The Ebean sql generated does a bad left outer join . So for now using two queries until i find a better solution
			 * select distinct t0.id c0, t0.title c1, t0.description c2, t0.price c3, t0.sale_duration c4, t0.created_time c5, t0.end_time c6, t0.created_by c7, t0.buyer_id c8, t0.address_line c9, t0.city c10, t0.state c11, t0.zipcode c12, t0.country c13, t0.contact_email c14, t0.contact_phone c15, t0.sold c16, t0.expired c17, t0.deleted c18 
				from posts t0 
				join post_tags u1 on u1.post_id = t0.id 
				where t0.deleted = ? and t0.expired = ? and (lower(t0.title) like ? or lower(t0.description) like ? or u1.normalized_tag like ? or post_tags.id = ? ) ] 
			
			The ideal single query would be this
			Set<Post> postsMatchingTitleOrDesc = Post.find.
														where().eq("deleted", false).
														eq("expired", false).
														disjunction().
															ilike("title", normalizedSearchTerm).
															ilike("description", normalizedSearchTerm). 
															like("tags.normalizedTag", normalizedSearchTerm).
															eq("post_tags.id", 1).
														endJunction().
														findSet();
			**/
			Set<Post> postsMatchingTitleOrDesc = Post.find.
													where().eq("deleted", false).
													eq("expired", false).
													disjunction().
														ilike("title", normalizedSearchTerm).
														ilike("description", normalizedSearchTerm). 
													endJunction().
													findSet();
			
			Set<Post> postsMatchingTag = Post.find.fetch("tags").
												where().eq("deleted", false).
												eq("expired", false).
												like("tags.normalizedTag", "%" +searchTerm.toLowerCase() + "%").
												findSet();
			Set<Post> result = new HashSet<Post>(postsMatchingTitleOrDesc);
			result.addAll(postsMatchingTag);
			return result;
		} else {
			return Post.find.
							where().eq("deleted", false).
							eq("expired", false).
							disjunction().
								ilike("city", searchTerm).
								ilike("description", searchTerm). 
							endJunction().
							findSet();
		}
	}
}
