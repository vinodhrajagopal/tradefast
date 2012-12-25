package models;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import controllers.UserController;
import play.Logger;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import plugins.S3Plugin;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import javax.persistence.OneToOne;
import javax.persistence.Transient;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;


@Entity
@Table(name="post_photos")
public class PostPhoto extends Model {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="post_photos_id_seq")
    public Long id;
    
	@OneToOne
	@JoinColumn(name = "post_id")
	public Post post;
    
    private String bucket;

    public String name;

    @Transient
    public File file;
    
    public static Finder<Long,PostPhoto> find = new Finder<Long, PostPhoto>(Long.class, PostPhoto.class);

    public URL getUrl() throws MalformedURLException {
        return new URL("https://s3.amazonaws.com/" + bucket + "/" + getActualFileName());
    }

    private String getActualFileName() {
        return id + "/" + name;
    }
    
	public static Set<PostPhoto> getPhotos(Long postId) {
		return find.where().
						eq("post_id", postId).
						findSet();
	}

    @Override
    public void save() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not save because amazonS3 was null");
            throw new RuntimeException("Could not save");
        }
        else {
            this.bucket = S3Plugin.s3Bucket;
            
            super.save(); // assigns an id

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, getActualFileName(), file);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
            S3Plugin.amazonS3.putObject(putObjectRequest); // upload file
        }
    }

    @Override
    public void delete() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not delete because amazonS3 was null");
            throw new RuntimeException("Could not delete");
        }
        else {
            S3Plugin.amazonS3.deleteObject(bucket, getActualFileName());
            super.delete();
        }
    }

}