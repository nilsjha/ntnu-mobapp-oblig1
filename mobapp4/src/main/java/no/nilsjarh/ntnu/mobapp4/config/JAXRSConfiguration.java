package no.nilsjarh.ntnu.mobapp4.config;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;


/**
 * Configures JAX-RS for the application.
 * @author Juneau
 */
@ApplicationPath("api")
public class JAXRSConfiguration extends ResourceConfig {
    public JAXRSConfiguration() {
        
	    /**
	     * Somehow this hides the error msgs when enabled! 
	     */
	    
	    packages(true,"no.nilsjarh.ntnu.mobapp4.resources","no.ntnu.tollefsen.auth")
            .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
            // @ValidateOnExecution annotations on subclasses won't cause errors.
            .property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true)
            .register(MultiPartFeature.class);
	    
    }
}
