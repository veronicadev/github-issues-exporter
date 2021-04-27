import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Exporter {
    private static HashMap<String, String> configuration;

    public static void main (String []args) {
        try{
            ConfigurationReader configurator = new ConfigurationReader();
            configuration = configurator.getConfiguration();

            StringBuilder str = new StringBuilder(configuration.get("GITHUB_URL"));
            str.append("/")
                    .append(configuration.get("OWNER"))
                    .append("/")
                    .append(configuration.get("REPO"))
                    .append("/issues");
            URL url = new URL(str.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);
            in.close();
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
