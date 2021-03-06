/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 *
 * @author Sajith
 */
public class ConfigParameters {

    private static ConfigParameters configs = null;
    private static final String CONFIG_FILE_PATH = "configs";
    private static HashMap<String, String> params;

    private ConfigParameters() {
    }

    public static ConfigParameters configParameter() {
        if (configs == null) {
            configs = new ConfigParameters();
            try {
                getConfigFileContent();
            } catch (Exception e) {
                System.out.println("Reading external file failed");
                setParameters();
            }
        }
        return configs;
    }

    private static void getConfigFileContent() throws IOException{
        InputStream is = ConfigParameters
                .class.getResourceAsStream(CONFIG_FILE_PATH);
        BufferedReader file = new BufferedReader(new InputStreamReader(is));
        String textLine = "";
        params = new HashMap<>();
        while ((textLine = file.readLine()) != null) {
            String[] data = textLine.split("::");
            params.put(data[0], data[1]);
        }
    }

    public String getParameter(String parameterName) {
        String parameter = "None";
        if (params.containsKey(parameterName)) {
            if (parameterName == "lastFMAPIKey") {
                parameter = getAPIKey();
            } else {
                parameter = params.get(parameterName);
            }
        }
        return parameter;
    }

    private String getAPIKey() {
        String[] apiKeys = params.get("lastFMAPIKey").split(",");
        Random r = new Random();
        int index = (r.nextInt(apiKeys.length - 0) + 0);
        return apiKeys[index];
    }

    private static void setParameters() {
        params = new HashMap<>();
        params.put("lastFMUserName", "sajithdr");
        params.put("artistCountPerUser", "5");
        params.put("tagCountPerArtist", "5");
        params.put("postCountPerUser", "300");
        params.put("initialUserCount", "50");
        params.put("learningStartBound", "10");
        params.put("userCountPerNewTag", "3");
        params.put("numberOfTracksPerUser", "15");
        params.put("dbName", "musicrec");
        params.put("dbUser", "code");
        params.put("dbPassword", "101201");
        params.put("tagLastFMURL", "https://www.last.fm/tag/");
        params.put("baseFBURL", "https://graph.facebook.com/v2.5/");
        params.put("baseLastFMURL", "https://ws.audioscrobbler.com/2.0/?method=");
        params.put("lastFMAPIKey", "accc242c838637a7ebc2f056a91956d2,9132bf012d7d42091d8465408df7c6b0,dd9ef64ad83b4c3b3d074a38f43cd3da");
        params.put("artistInfoFilePath", "./data/learnedArtists.txt");
        params.put("tagInfoFilePath", "./data/learnedTags.txt");
        params.put("userInfoFilePath", "./data/users.txt");
        params.put("dataSetFilePath", "./data/dataSet.arff");
        params.put("logFilePath", "./log.txt");
        params.put("appInfoOne", "530828057094884");
        params.put("appInfoTwo", "e275d764d5a339da4561578145f294f2");
    }

}
