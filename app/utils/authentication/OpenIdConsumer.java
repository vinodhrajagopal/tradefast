package utils.authentication;

import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.*;
import org.openid4java.OpenIDException;

import controllers.Application;
import controllers.Authentication;
import controllers.routes;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Http.Request;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

/**
 * OpenID Consumer (Relying Party) implementation.
 */
public class OpenIdConsumer
{
    private ConsumerManager manager;

    private static final String OPEN_ID_DISCOVERED = "openid-disc";
	private static final String OPEN_ID_URL = "openid_url";
	private static final String OPEN_ID = "OpenID";
    
	@SuppressWarnings("serial")
	public static final Map<String,String> OPEN_ID_PROVIDERS = Collections.unmodifiableMap(new HashMap<String,String>(){{
																		put("Google", "https://www.google.com/accounts/o8/id");
																		put("Yahoo", "http://me.yahoo.com/");
																		put("OpenID", "");
																}});

    public OpenIdConsumer() throws ConsumerException {
        manager = new ConsumerManager();
    }

    // --- placing the authentication request ---
    public Result authRequest(String authenticationProvider) throws IOException {
    	String openIdProviderUrl = getOpenIdProviderUrl(authenticationProvider);
    	if (openIdProviderUrl != null && !openIdProviderUrl.isEmpty()) {
	        try {
	            List discoveries = manager.discover(openIdProviderUrl);
	            DiscoveryInformation discovered = manager.associate(discoveries);
	            Controller.session().put(OPEN_ID_DISCOVERED, discovered.toString());
	            AuthRequest authReq = manager.authenticate(discovered, Application.domainUrl() + routes.Authentication.verifyOpenIdProviderResponse().url());
	
	            // Attribute Exchange example: fetching the 'email' attribute
	            FetchRequest fetch = FetchRequest.createFetchRequest();
	            fetch.addAttribute(Authentication.EMAIL, "http://schema.openid.net/contact/email", true);
	
	            // attach the extension to the authentication request
	            authReq.addExtension(fetch);
	            return Results.redirect(authReq.getDestinationUrl(true));
	
	            /*
	            if (!discovered.isVersion2()) {
	                // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
	                // The only method supported in OpenID 1.x
	                // redirect-URL usually limited ~2048 bytes
	                return authReq.getDestinationUrl(true);
	            }
	            else {
	                // Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)
	
	                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("formredirection.jsp");
	                httpReq.setAttribute("parameterMap", authReq.getParameterMap());
	                httpReq.setAttribute("destinationUrl", authReq.getDestinationUrl(false));
	                dispatcher.forward(httpReq, httpResp);
	                
	            }*/
	        } catch (OpenIDException e) {
	            // present error to the user
	        }
        }
        return Results.TODO;
    }

    public Result verifyResponse(Request httpReq) {
        try {
            ParameterList response = new ParameterList(httpReq.queryString());

            // retrieve the previously stored discovery information
            String discoveryInfo = Controller.session().get(OPEN_ID_DISCOVERED);
            DiscoveryInformation discovered = deserializeDiscoveryInformation(discoveryInfo);//TODO: Get the discovered object from String format 

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = new StringBuffer("http://"+ httpReq.host() + httpReq.uri());

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request --Vinodh : Doesn't seem to need the same manager instance used to place the auth request
            VerificationResult verification = manager.verify(receivingURL.toString(),response, discovered);

            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                String email = null;
                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
                    List emails = fetchResp.getAttributeValues(Authentication.EMAIL);
                    email = (String)emails.get(0);
                	return Application.authenticationSuccess(email);
                }
            }
        } catch (OpenIDException e) {
            // present error to the user
        }   
        return Results.TODO;
    }
    
    private String getOpenIdProviderUrl(String authenticationProvider) {
    	return OPEN_ID.equals(authenticationProvider) ? Controller.form().bindFromRequest().get(OPEN_ID_URL) : OPEN_ID_PROVIDERS.get(authenticationProvider);
    }
    
    private DiscoveryInformation deserializeDiscoveryInformation(String discoveryInfo) {
    	if (discoveryInfo == null || discoveryInfo.isEmpty()) {
    		return null;
    	}
    	return null;//TODO: Need to fix this
    }
}