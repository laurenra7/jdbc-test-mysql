package org.lra.test.jdbc;

import java.sql.Connection;

/**
 * Created by laurenra on 8/19/14.
 */
public interface DBConnection {

    public Connection getConnection();

    public void setConnection(Connection connection);

    public boolean openConnection(String dbUrl, String username, String password);

    public boolean isValidConnection();

    public boolean closeConnection();

}
