package controllers.authentication.openid;

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

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Http.Request;

import java.util.List;
import java.io.IOException;

/**
 * OpenID Consumer (Relying Party) implementation.
 */
public class OpenIdConsumer
{
    private ConsumerManager manager;
    private static final String OPENID_RETURN_TO_URL = Application.domainUrl() + "/openid/verify";
    private static final String OPEN_ID_DISCOVERED = "openid-disc";

    public OpenIdConsumer() throws ConsumerException {
        // instantiate a ConsumerManager object
        manager = new ConsumerManager();
    }

    // --- placing the authentication request ---
    public Result authRequest(String openIdIdentifier) throws IOException {
        try {
            // configure the return_to URL where your application will receive
            // the authentication responses from the OpenID provider
            

            // --- Forward proxy setup (only if needed) ---
            // ProxyProperties proxyProps = new ProxyProperties();
            // proxyProps.setProxyName("proxy.example.com");
            // proxyProps.setProxyPort(8080);
            // HttpClientFactory.setProxyProperties(proxyProps);

            // perform discovery on the user-supplied identifier
            List discoveries = manager.discover(openIdIdentifier);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);

            // store the discovery information in the user's session
            //httpReq.getSession().setAttribute("openid-disc", discovered);
            Controller.session().put(OPEN_ID_DISCOVERED, discovered.toString());
            

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = manager.authenticate(discovered, OPENID_RETURN_TO_URL);

            // Attribute Exchange example: fetching the 'email' attribute
            FetchRequest fetch = FetchRequest.createFetchRequest();
            fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);

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
        return Results.TODO;
    }

    public OpenIdVerifyResult verifyResponse(Request httpReq) {
        try {
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response = new ParameterList(httpReq.queryString());

            // retrieve the previously stored discovery information
            String discoveryInfo = Controller.session().get(OPEN_ID_DISCOVERED);
            DiscoveryInformation discovered = deserializeDiscoveryInformation(discoveryInfo);//TODO: Get the discovered object from String format 

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = new StringBuffer("http://"+ httpReq.host() + httpReq.uri());

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(receivingURL.toString(),response, discovered);

            // examine the verification result and extract the verified identifier
            Identifier verified = verification.getVerifiedId();
            if (verified != null) {
                AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();
                String email = null;

                if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
                    FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);

                    List emails = fetchResp.getAttributeValues("email");
                    email = (String) emails.get(0);
                }
                return new OpenIdVerifyResult(verified, email);  // success
            }
        } catch (OpenIDException e) {
            // present error to the user
        }   
        return null;
    }
    
    private DiscoveryInformation deserializeDiscoveryInformation(String discoveryInfo) {
    	if (discoveryInfo == null || discoveryInfo.isEmpty()) {
    		return null;
    	}
    	return null;
    }
    
    public static class OpenIdVerifyResult {
    	private final Identifier identifier;
    	private final String email;
    	
    	private OpenIdVerifyResult(Identifier identifier, String email) {
    		this.identifier = identifier;
    		this.email = email;
    	}
    	public String getEmail() {
    		return this.email;
    	}
    	public Identifier getIdentifier() {
    		return this.identifier;
    	}
    }
}