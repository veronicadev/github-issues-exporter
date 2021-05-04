import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Exporter {
    private static HashMap<String, String> configuration;
    private static final Logger logger = LoggerFactory.getLogger(Exporter.class);
    public static void main (String []args) {
        try{
            logger.info("Github issues exporter - Start");
            ConfigurationReader configurator = new ConfigurationReader();
            configuration = configurator.getConfiguration();
            getIssues();
            logger.info("Github issues exporter - End");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void getIssues() throws IOException {
        BufferedReader in = null;
        try{
            /*BUILD REQUEST*/
            logger.info("Exporter building request");
            String str = configuration.get("GITHUB_URL") + "/" +
                    configuration.get("OWNER") +
                    "/" +
                    configuration.get("REPO") +
                    "/issues";
            URL url = new URL(str);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            /*READING RESPONSE CONTENT*/
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                stringBuilder.append(inputLine);

            String result = stringBuilder.toString();
            logger.info("Exporter response" + result);
            saveIssues(new JSONArray(result));

        }catch (IOException e) {
            e.printStackTrace();
            logger.error("** Caught IOException in Exporter.getIssues **", e);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("** Caught Exception in Exporter.getIssues **", e);
        }
        finally {
            if(in!=null){
                in.close();
            }
        }
    }

    private static void saveIssues(JSONArray issuesObj){
        try{
            logger.info("Saving issues in file xls");
            if(!issuesObj.isEmpty()){
                HSSFWorkbook workbook = new HSSFWorkbook();
                HSSFSheet sheet = workbook.createSheet("Issues");
                HSSFRow rowHead = sheet.createRow((short)0);
                rowHead.createCell(0).setCellValue("N.");
                rowHead.createCell(1).setCellValue("Title");
                rowHead.createCell(2).setCellValue("Description");
                rowHead.createCell(3).setCellValue("Labels");
                rowHead.createCell(4).setCellValue("State");
                rowHead.createCell(5).setCellValue("Assignees");
                rowHead.createCell(6).setCellValue("Created at");
                rowHead.createCell(7).setCellValue("Updated at");
                rowHead.createCell(8).setCellValue("Closed at");
                for (int i=0; i<issuesObj.length(); i++) {
                    HSSFRow row = sheet.createRow((short)i+1);
                    JSONObject issueObj = (JSONObject) issuesObj.get(i);
                    row.createCell(0).setCellValue(issueObj.getLong("number"));
                    row.createCell(1).setCellValue(issueObj.getString("title"));
                    if(issueObj.has("body") && !issueObj.isNull("body")){
                        row.createCell(2).setCellValue(issueObj.getString("body"));
                    }
                    if(issueObj.has("body") && !issueObj.isNull("body")){
                        row.createCell(2).setCellValue(issueObj.getString("body"));
                    }
                    if(issueObj.has("assignees") && !issueObj.isNull("assignees")){
                        row.createCell(5).setCellValue(getAssegnees(issueObj.getJSONArray("assignees")));

                    }
                    row.createCell(3).setCellValue("Labels");
                    row.createCell(4).setCellValue(issueObj.getString("state"));
                    row.createCell(6).setCellValue(issueObj.getString("created_at"));
                    row.createCell(7).setCellValue(issueObj.getString("updated_at"));
                    if(issueObj.has("closed_at") && !issueObj.isNull("closed_at")){
                        row.createCell(8).setCellValue(issueObj.getString("closed_at"));
                    }
                }
                String exportPath = configuration.get("EXPORT_PATH");
                FileOutputStream fileOut = new FileOutputStream(exportPath);
                workbook.write(fileOut);
                fileOut.close();
                workbook.close();
                logger.info("Saving issues in file completed!");
                logger.info("You can find your file .xls in " + exportPath);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private static String getAssegnees(JSONArray assigneesJSON){
        String assignees = "";
        if(!assigneesJSON.isEmpty()){
            for (Object a: assigneesJSON) {
                JSONObject user = (JSONObject) a;
                assignees+=user.getString("login");
                assignees+="\n";
            }
        }
        return assignees;
    }
}
