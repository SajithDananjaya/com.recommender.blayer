/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datahandlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.logging.Level;

import processes.AppLogger;
import processes.ConfigParameters;

/**
 *
 * @author Sajith
 */
public class AccessDB {
    
    private static final Logger LOGGER = AppLogger.getNewLogger(AccessDB.class.getName());
    private final String DB_USERNAME = ConfigParameters.configParameter().getParameter("");
    private final String DB_PASSWORD = ConfigParameters.configParameter().getParameter("");
    private final String DB_NAME = ConfigParameters.configParameter().getParameter("");
    private final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private final String DB_URL = "jdbc:mysql://localhost/" + DB_NAME;
    private static AccessDB dbConnectionObj; //Singelton DBConnection object
    private static Connection dbCon;
    private static boolean connectionInitiated = false;

    //Preventing intialization from outside
    private AccessDB() {
        if (dbCon == null) {
            try {
                Class.forName(DRIVER_CLASS);
                dbCon = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                connectionInitiated = true;
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Error@DBConnection");
                System.err.println(e.toString());
            }
        }
    }

    //if null initiates the singelton obj and returns it
    public static AccessDB getDBConnection() {
        if (dbConnectionObj == null) {
            dbConnectionObj = new AccessDB();
        }

        return dbConnectionObj;
    }

    //Returns connection states
    //if returns false connection parameters are faulty
    public boolean isInitiated() {
        return connectionInitiated;
    }

    //Saving data
    public int saveData(String sqlQuery) {
        //System.out.println(sqlQuery);
        int saveStatus = 0;
        try {
            Statement sql = dbCon.createStatement();
            saveStatus = sql.executeUpdate(sqlQuery);
            sql.close();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Data saving failed", e);
        }

        return saveStatus;
    }

    public ResultSet getData(String sqlQuery) {
        //System.out.println(sqlQuery);
        ResultSet rs = null;
        try {
            Statement sql = dbCon.createStatement();
            rs = sql.executeQuery(sqlQuery);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Data retriving failed", e);
        }
        return rs;
    }
    
}
