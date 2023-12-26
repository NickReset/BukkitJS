package social.nickrest.bukkitjs.js;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import social.nickrest.npm.NPMLogger;

public class NPMLog4jLogger implements NPMLogger {

    private static final Logger logger = LogManager.getLogger(NPMLog4jLogger.class);

    @Override
    public void info(String s) {
        logger.info(s);
    }

    @Override
    public void warn(String s) {
        logger.warn(s);
    }

    @Override
    public void error(String s) {
        logger.error(s);
    }

    @Override
    public void debug(String s) {
        logger.debug(s);
    }

}
