package models;

import org.scribe.model.Token;
import org.scribe.model.Verifier;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Jean-Baptiste Watenberg
 */
@Entity
public class User extends Model {

    @Id
    public String id;

    public String firstname;
    public String lastname;
    //public String email; Unavailable in basic profile response
    public String requestTokenSecret;
    public String requestTokenString;
    public String verificationKey;
    public String accessTokenSecret;
    public String accessTokenString;


}
