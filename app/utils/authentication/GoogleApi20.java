package utils.authentication;

import org.scribe.builder.api.DefaultApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.Verb;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

public class GoogleApi20 extends DefaultApi20
{
	  private static final String AUTHORIZE_URL = "https://accounts.google.com/o/oauth2/auth?response_type=code&client_id=%s&redirect_uri=%s";
	  private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

	  @Override
	  public String getAccessTokenEndpoint()
	  {
	    return "https://accounts.google.com/o/oauth2/token?grant_type=authorization_code";
	  }
	  
	  @Override
	  public Verb getAccessTokenVerb()
	  {
	    return Verb.POST;
	  }
	  

	  @Override
	  public String getAuthorizationUrl(OAuthConfig config)
	  {
	    Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback. Facebook does not support OOB");

	    // Append scope if present
	    if(config.hasScope())
	    {
	     return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
	    }
	    else
	    {
	      return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
	    }
	  }
}
