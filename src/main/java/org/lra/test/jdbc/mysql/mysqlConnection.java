package org.lra.test.jdbc.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;
import org.lra.test.jdbc.DBConnection;

/**
 * Created by laurenra on 8/19/14.
 */
public class mysqlConnection implements DBConnection {

    private Connection connection = null;

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
        BoneCP connectionPool = null;

        // Load the JDBC driver
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println("ERROR: No MySQL JDBC driver.");
            System.out.println(e.getMessage());
            return false;
        }

        System.out.println("MySQL JDBC driver registered."); // testing only

        // Set up connection pool to database and get connection
        // using BoneCP 0.8 because it's fast (http://www.jolbox.com/)
        BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinConnectionsPerPartition(5);
        config.setMaxConnectionsPerPartition(10);
        config.setPartitionCount(1);

        try {
            connectionPool = new BoneCP(config);
            connection = connectionPool.getConnection();
        }
        catch (SQLException e) {
            System.out.println("Connection failed. Check output console.");
            e.printStackTrace();
            return false;
        }

        if (connection != null) {
            System.out.println("Got the connection.  Congratulations!"); // testing only
        }
        else {
            System.out.println("Failed to make connection."); // testing only
            return false;
        }

        return true;
    }

    @Override
    public boolean isValidConnection() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select host, user from mysql.user");
            System.out.println("host" + "\t" + "user");
            while(resultSet.next()) {
                System.out.println(resultSet.getString("host") + "\t" + resultSet.getString("user"));
            }
        } catch (SQLException e) {
            System.out.println("ERROR: did not execute SQL statement.");
            System.out.println(e.getMessage());
//            e.printStackTrace();
            return false;
        }
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
}
