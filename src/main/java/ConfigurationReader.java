import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class ConfigurationReader {
    private HashMap<String, String> configuration;
    private InputStream inputStream;
    private static Logger logger = LoggerFactory.getLogger(ConfigurationReader.class);
    private void readConfiguration() throws IOException {
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            logger.info("Reading config.properties - Start");
            inputStream = ConfigurationReader.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            configuration = new HashMap<>();
            configuration.put("GITHUB_URL", prop.getProperty("github_url"));
            configuration.put("REPO", prop.getProperty("repo"));
            configuration.put("OWNER", prop.getProperty("owner"));
            configuration.put("EXPORT_PATH", prop.getProperty("export_path"));
            logger.info(configuration.toString());
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        logger.info("Reading config.properties - End");
    }

    public HashMap<String, String> getConfiguration() throws IOException {
        if(configuration==null){
            readConfiguration();
        }
        return configuration;
    }
}
