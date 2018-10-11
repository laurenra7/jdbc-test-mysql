package org.lra.test.jdbc;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by laurenra on 8/19/14.
 */
public class mysqlConnection implements DBConnection {

    private String testSqlStatement = "select version()";
    private Connection connection = null;
    private long startTimeNano;
    private String outStr;

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean openConnection(String dbUrl, String username, String password) {

        outStr = "";

//        BoneCP connectionPool = null;

        // Load the JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("ERROR: can't find driver " + e.getMessage());
//            outStr = outStr + "ERROR: can't find driver " + e.getMessage() + "\n";
//            outputToFile();
            return false;
        }

        System.out.println("MySQL JDBC driver registered.");
//        outStr = outStr + "MySQL JDBC driver registered." + "\n";

        // Set up connection pool to database and get connection
        // using BoneCP 0.8 because it's fast (http://www.jolbox.com/)
//        BoneCPConfig config = new BoneCPConfig();
//        config.setJdbcUrl(dbUrl);
//        config.setUsername(username);
//        config.setPassword(password);
//        config.setMinConnectionsPerPartition(5);
//        config.setMaxConnectionsPerPartition(10);
//        config.setPartitionCount(1);

        try {
            startTimeNano = System.nanoTime();
            connection = DriverManager.getConnection(dbUrl, username, password);

//            connectionPool = new BoneCP(config);
//            connection = connectionPool.getConnection();
            System.out.println("getConnection - elapsed seconds: " + getElapsedSeconds(startTimeNano));
//            outStr = outStr + "getConnection - elapsed seconds: " + getElapsedSeconds(startTimeNano) + "\n";

        }
        catch (SQLException e) {
            System.out.println("getConnection FAIL - elapsed seconds: " + getElapsedSeconds(startTimeNano));
//            outStr = outStr + "getConnection FAIL - elapsed seconds: " + getElapsedSeconds(startTimeNano) + "\n";
            System.out.println("Connection failed. Check output console.");
//            outStr = outStr + "Connection failed. Check output console." + "\n";
            e.printStackTrace();
//            outStr = outStr + e.getMessage() + "\n";
//            outputToFile();
            return false;
        }

        if (connection != null) {
            System.out.println("Got the connection.  Congratulations!"); // testing only
//            outStr = outStr + "Got the connection.  Congratulations!" + "\n";
        }
        else {
            System.out.println("Failed to make connection."); // testing only
//            outStr = outStr + "Failed to make connection." + "\n";
//            outputToFile();
            return false;
        }

//        outputToFile();
        return true;
    }

    @Override
    public boolean isValidConnection() {
        try {
            Statement statement = connection.createStatement();
            startTimeNano = System.nanoTime();
            ResultSet resultSet = statement.executeQuery(testSqlStatement);
            System.out.println("executeQuery - elapsed seconds: " + getElapsedSeconds(startTimeNano));
//            outStr = outStr + "executeQuery - elapsed seconds: " + getElapsedSeconds(startTimeNano) + "\n";
            System.out.println("Test SQL statement: " + testSqlStatement);
//            outStr = outStr + "Test SQL statement: " + testSqlStatement + "\n";
            while(resultSet.next()) {
                System.out.println("Test SQL result: " + resultSet.getString("version()"));
//                outStr = outStr + "Test SQL result: " + resultSet.getString("version()") + "\n";
            }
        }
        catch (SQLException e) {
            System.out.println("executeQuery FAIL - elapsed seconds: " + getElapsedSeconds(startTimeNano));
//            outStr = outStr + "executeQuery FAIL - elapsed seconds: " + getElapsedSeconds(startTimeNano) + "\n";
            System.out.println("ERROR: did not execute SQL statement.");
//            outStr = outStr + "ERROR: did not execute SQL statement." + "\n";
            System.out.println(e.getMessage());
//            outStr = outStr + e.getMessage() + "\n";
            e.printStackTrace();
//            outputToFile();
            return false;
        }
//        outputToFile();
        return true;
    }

    @Override
    public boolean closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                return true;
            }
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        else return false;
    }

    private static BigDecimal getElapsedSeconds(long startTime) {
        BigDecimal elapsedNanoSecs = new BigDecimal(System.nanoTime() - startTime);
        return elapsedNanoSecs.scaleByPowerOfTen(-9);
    }

    private void outputToFile() {
        try (FileWriter fileWriter = new FileWriter("jdbcTestMySQL.out")) {
            fileWriter.write(outStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
