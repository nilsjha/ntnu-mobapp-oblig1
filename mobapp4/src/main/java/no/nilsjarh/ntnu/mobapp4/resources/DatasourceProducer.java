package no.nilsjarh.ntnu.mobapp4.resources;

import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.sql.DataSource;
import static no.nilsjarh.ntnu.mobapp4.resources.DatasourceProducer.JNDI_NAME;
import org.eclipse.microprofile.config.inject.ConfigProperty;


/**
 *
 * @author mikael
 */
@Singleton
@DataSourceDefinition(
    name            = JNDI_NAME,
    className       = "org.postgresql.ds.PGSimpleDataSource", 
    serverName      = "${MPCONFIG=dataSource.serverName}",
    portNumber      = 5432,
    databaseName    = "${MPCONFIG=dataSource.databaseName}",
    user            = "${MPCONFIG=dataSource.user}",
    password        = "${MPCONFIG=dataSource.password}"
)

public class DatasourceProducer {
    public static final String JNDI_NAME =  "java:app/jdbc/postgres-microprofile";

    @Resource(lookup=JNDI_NAME)
    DataSource ds;

    @Produces
    public DataSource getDatasource() {
        return ds;
    }
}
