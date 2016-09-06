/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import objectModels.User;
import datahandlers.LastFMDataHandler;

/**
 *
 * @author Sajith
 */
public class ClusterProcess {

    private static final Logger LOGGER = AppLogger
            .getNewLogger(ClusterProcess.class.getName());

    private SimpleKMeans dataGraph;
    private Instances userPoints;
    private boolean clusterInitiated = false;
    private String[] clusters;

    public int getClusterID(User user) {
        Instance userInstance = toInstance(user, userPoints);
        int clusterNumber = getRelativeCluster(userInstance);
        return clusterNumber;
    }

    public String[] getRelatedUsers(User user) {
        int clusterNumber = getClusterID(user);
        String[] userIDs = getClusterUser(clusterNumber);
        return userIDs;
    }

    public String[] getClusterUser(int clusterNumber) {
        String[] userIDs = clusters[clusterNumber].split(",");
        return userIDs;
    }

    public void buildGraph(String filePath) {

        System.out.println("May throw exception for poorly formated data files");
        try {
            userPoints = new Instances(getDataFile(filePath));
            int numberOfClusters = clusterCount(userPoints);
            int maxSeedAmount = seedAmount(userPoints);
            initiateClusters(maxSeedAmount, numberOfClusters);
            if (clusterInitiated) {
                try {
                    dataGraph.buildClusterer(userPoints);
                    saveClusterInforamtion();
                    LOGGER.log(Level.INFO, "Clustering was completed");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error@ClusterProcess_initiateCluster", e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error@ClusterProcess_initiateCluster", e);
        }

    }

    private void initiateClusters(int randomSeedMax, int clusterCount) {
        if (!clusterInitiated) {
            dataGraph = new SimpleKMeans();
            dataGraph.setPreserveInstancesOrder(true);
            dataGraph.setSeed(randomSeedMax);
            System.out.println("May throw exception if the cluster count is a negative value");
            try {
                dataGraph.setNumClusters(clusterCount);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error@ClusterProcess_initiateCluster", e);
                clusterCount = ((-1) * clusterCount) + 1;
                initiateClusters(randomSeedMax, clusterCount);
            }
            clusterInitiated = true;
        }
    }

    private void saveClusterInforamtion() {
        this.clusters = new String[dataGraph.getNumClusters()];

        for (int index = 0; index < userPoints.numInstances(); index++) {
            Instance i = userPoints.instance(index);
            int clusterID = getRelativeCluster(i);
            if (clusters[clusterID] == null) {
                clusters[clusterID] = "" + (int) i.value(0);
            } else {
                clusters[clusterID] = clusters[clusterID] + "," + (int) i.value(0);
            }
        }
    }

    private int getRelativeCluster(Instance targetUserData) {
        int clusterID = 0;
        try {
            clusterID = dataGraph.clusterInstance(targetUserData);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error@ClusterProcess_getRelativePoints", e);
        }
        return clusterID;
    }

    private BufferedReader getDataFile(String filePath) {
        BufferedReader inputReader = null;
        System.out.println("May throw file not found exception for given path");
        try {
            inputReader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error@ClusterProcess_getDataFile", e);
        }
        return inputReader;
    }

    private Instance toInstance(User user, Instances dataSet) {
        Instance tempInstance = new Instance(userPoints.numAttributes());
        tempInstance.setDataset(userPoints);
        String userDataString = "0" + user.getTasteString(LastFMDataHandler
                .getInitialTagCount());
        String[] dataArray = userDataString.split(",");
        for (int index = 0; index < dataArray.length; index++) {
            tempInstance.setValue(index, Integer.parseInt(dataArray[index]));
        }
        return tempInstance;
    }

    private int seedAmount(Instances dataPoints) {
        return dataPoints.numInstances();
    }

    private int clusterCount(Instances dataPoints) {
        int totalDataCount = dataPoints.numInstances();
        return totalDataCount / 3;
//        return (int) Math.sqrt(totalDataCount);
    }

    public String[] getClusterList() {
        return this.clusters;
    }

    public void printCluster() {
        for (String s : clusters) {
            System.out.println(s);
        }
    }
}
