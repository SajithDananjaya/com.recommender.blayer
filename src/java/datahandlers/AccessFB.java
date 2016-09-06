/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahandlers;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import processes.ConfigParameters;
import objectModels.FacebookUser;
import org.json.JSONArray;
import org.json.JSONObject;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.FacebookClient.AccessToken;
import com.restfb.Version;

/**
 *
 * @author Sajith
 */
public class AccessFB {

    private final static String BASE_URL = ConfigParameters
            .configParameter().getParameter("baseFBURL");

    public static URL getURL(FacebookUser user,
            String methodParam, int resultCount) throws IOException {
        String url = BASE_URL + methodParam
                + "?access_token=" + user.getAccessToken()
                + "&limit=" + resultCount;
        URL tempURL = new URL(url);
        return tempURL;
    }

    public static String getResponse(URL url) throws IOException {
        URLConnection respons = url.openConnection();
        String stringRespons = new Scanner(respons.getInputStream(), "UTF-8")
                .useDelimiter("\\A").next();
        return stringRespons;
    }

    public static JSONObject[] extractJSONObjects(String response) {
        JSONObject json = new JSONObject(response);
        JSONArray jarray = json.getJSONArray("data");
        int resultsCout = jarray.length();
        JSONObject[] jasonObjects = new JSONObject[resultsCout];
        for (int index = 0; index < resultsCout; index++) {
            jasonObjects[index] = jarray.getJSONObject(index);
        }
        return jasonObjects;
    }

    public static JSONObject getObject(String response) {
        JSONObject json = new JSONObject(response);
        JSONArray jarray = json.getJSONArray("data");
        return jarray.getJSONObject(0);
    }

    public static String extenedAccessToken(String currentToken) {
        String extendedAccessToken = "";
        String appID = ConfigParameters
                .configParameter().getParameter("appInfoOne");
        String appSecret = ConfigParameters
                .configParameter().getParameter("appInfoTwo");
        FacebookClient fbConn = 
                new DefaultFacebookClient(currentToken, Version.LATEST);
        AccessToken extended = fbConn
                .obtainExtendedAccessToken(appID, appSecret);
        extendedAccessToken = extended.getAccessToken();
        return extendedAccessToken;
    }

}
