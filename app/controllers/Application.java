package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.User;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.model.*;
import play.core.Router;
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

        final Map<String, String[]> parameters = request().body().asFormUrlEncoded();

        if (!parameters.containsKey("error")&&parameters.containsKey("code")) {
            Verifier verifier = new Verifier(parameters.get("code")[0]);
            Token accessToken = getService().getAccessToken(requestToken, verifier);

            OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.linkedin.com/v1/people/~?format=json");
            getService().signRequest(accessToken,request);
            Response response = request.send();
            JsonNode json = Json.parse(response.getBody());


            user.verificationKey = verifier;
            user.accessToken = accessToken;

            user.firstname = json.get("firstName").textValue();
            user.lastname = json.get("lastName").textValue();

            user.save();

        }

        return ok();
    }

    public static Result requestLinkedin() {
        try {
            requestToken = getService().getRequestToken();//token must be preserved for future connections

            user.requestToken = requestToken;
            user.save();

            //link to get the authorization
            String url = getService().getAuthorizationUrl(requestToken);
            return redirect(url);
        } catch (Exception e) {
            Logger.getGlobal().log(Level.SEVERE,"An error occurred while login through linkedIn : "+e);
            return ok(index.render("An error occurred"));
        }
    }
}
