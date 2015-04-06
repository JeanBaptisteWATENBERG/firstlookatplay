package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.*;
import play.core.Router;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.*;
import org.scribe.oauth.OAuthService;

import views.html.*;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application extends Controller {

    private static final String API_KEY = "";
    private static final String API_SECRET = "";
    private static Token requestToken;
    private static User user = new User();

    public static OAuthService getService() {
        return new ServiceBuilder()
                .provider(LinkedInApi.class)
                .apiKey(API_KEY)
                .apiSecret(API_SECRET)
                .callback(controllers.routes.Application.callback().absoluteURL(request()))//url to return the verification key
                .build();
    }

    public static Result index() {

        String url = controllers.routes.Application.requestLinkedin().absoluteURL(request());
        return ok(index.render(url));
    }

    public static Result callback() {


        //final Map<String, String[]> parameters = request().body().asFormUrlEncoded();

        /*JsonNode requ =*/
            String oauthVerifier = request().getQueryString("oauth_verifier");//.asText();//asJson();
            String oauthToken = request().getQueryString("oauth_token");



            User user = (User) new Model.Finder(Token.class,User.class).where().eq("requestTokenString",oauthToken).findUnique();



            Verifier verifier = new Verifier(oauthVerifier);
            Token accessToken = getService().getAccessToken(new Token(user.requestTokenString,user.requestTokenSecret), verifier);


            OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.linkedin.com/v1/people/~?format=json");
            getService().signRequest(accessToken,request);
            Response response = request.send();
            JsonNode json = Json.parse(response.getBody());


            user.verificationKey = oauthVerifier;
            user.accessTokenSecret = accessToken.getSecret();
            user.accessTokenString = accessToken.getToken();

            user.firstname = json.get("firstName").textValue();
            user.lastname = json.get("lastName").textValue();

            user.save();



        return ok(user.firstname+" "+user.lastname);
    }

    public static Result requestLinkedin() {
        try {
            requestToken = getService().getRequestToken();//token must be preserved for future connections


            user.requestTokenString = requestToken.getToken();
            user.requestTokenSecret = requestToken.getSecret();
            user.save();

            //link to get the authorization
            String url = getService().getAuthorizationUrl(requestToken);
            return redirect(url);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE,"An error occurred while login through linkedIn : "+e);
            return ok("An error occurred");
        }
    }
}
