package no.nilsjarh.ntnu.mobapp4.config;

import javax.annotation.security.DeclareRoles;
import javax.security.enterprise.identitystore.DatabaseIdentityStoreDefinition;
import javax.security.enterprise.identitystore.PasswordHash;
import no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer;
import no.ntnu.tollefsen.auth.Group;
import org.eclipse.microprofile.auth.LoginConfig;

/**
 *
 * @author nils
 */
@DatabaseIdentityStoreDefinition(
    dataSourceLookup=DatasourceProducer.JNDI_NAME,
    callerQuery="SELECT password FROM users WHERE id LIKE ?",
    groupsQuery="SELECT name FROM user_has_group WHERE id LIKE ?",
    hashAlgorithm = PasswordHash.class,
    priority = 80)
@DeclareRoles({Group.ADMIN,Group.USER})
@LoginConfig(authMethod = "MP-JWT",realmName = "template")
public class SecurityConfiguration {    
}
