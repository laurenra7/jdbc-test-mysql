package org.lra.test.jdbc;

import java.io.Console;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.lra.test.jdbc.oracle.oracleConnection;

/**
 * Created by laurenra on 8/19/14.
 */
public class jdbcTest {
    public static void main(String[] args) throws IOException {

        String dbDriverPath = null;
//        String dbType = null;
        String dbUrl = null;
        String dbUser = null;

        String username;
        String password;
        Connection connection;
        Statement statement;
        ResultSet resultSet;

        // Get a console to run from the command line.
        Console console = System.console();
        if (console == null) {
            System.err.println("No console");
            System.exit(1);
        }

        Options commandLineOptions = new Options();
        commandLineOptions.addOption(Option.builder("h")
                .longOpt("help")
                .desc("Show this help")
                .build());
        commandLineOptions.addOption(Option.builder("u")
                .longOpt("url")
                .desc("URL to test")
                .hasArg()
                .argName("URL")
                .build());
        commandLineOptions.addOption(Option.builder("n")
                .longOpt("username")
                .desc("Database user name")
                .hasArg()
                .argName("userName")
                .build());
//        commandLineOptions.addOption(Option.builder("p")
//                .longOpt("driverpath")
//                .desc("Path to database driver .jar file")
//                .hasArg()
//                .argName("driverPath")
//                .build());

        if(args.length == 0) {
            showCommandHelp(commandLineOptions);
        }
        else {
            CommandLineParser parser = new DefaultParser();
            try {
                CommandLine line = parser.parse(commandLineOptions, args);
                if (line.hasOption("help")) {
                    showCommandHelp(commandLineOptions);
                }
                else {
                    // Get database driver .jar file path
//                    if (line.hasOption("driverpath")) {
//                        dbDriverPath = line.getOptionValue("driverpath");
//                    }
//                    else {
//                        dbDriverPath = console.readLine("Path to DB driver .jar file: ");
//                    }

                    // Get database URL
                    if (line.hasOption("url")) {
                        dbUrl = line.getOptionValue("url");
                    }
                    else {
                        dbUrl = console.readLine("Database connection url: ");
                    }

                    // Get username
                    if (line.hasOption("username")) {
                        dbUser = line.getOptionValue("username");
                    }
                    else {
                        dbUser = console.readLine("User name: ");
                    }

                    // Get password, don't show on command line
                    char[] enterPassword = console.readPassword("Password: ");
                    String dbPassword = new String(enterPassword);

                    // Get driver .jar file and load classes.
//                    if (!loadJDBCDriver(dbDriverPath)) {
//                        System.exit(1);
//                    }

                    // Run JDBC connection test.
                    if (!testJDBCConnection(dbUrl, dbUser, dbPassword)) {
                        System.err.println("ERROR: JDBC test failed.");
                        System.exit(1);
                    }

                }
            }
            catch (ParseException exp) {
                System.err.println("Parsing failed: " + exp.getMessage());
                System.exit(1);
            }
        }

        System.exit(0);
    }

    private static void showCommandHelp(Options options) {
        String commandHelpHeader = "\nTest JDBC connections\n\n";
        String commandHelpFooter = "\nExample:\n\n" +
                "  java -jar jdbcTestOracle.jar -d oracle -u jdbc:oracle:thin:@ldap://my.server.com:456/dbname,cn=MyContext,dc=organization,dc=domain\n\n";

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar jdbcTest.jar", commandHelpHeader, options, commandHelpFooter, true);
    }

    private static boolean loadJDBCDriver(String pathToJar) {
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(pathToJar);
        } catch (IOException e) {
            System.err.println("ERROR: jar file not found in path " + pathToJar);
            e.printStackTrace();
            return false;
        }

        Enumeration<JarEntry> e = jarFile.entries();

        try {
            URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (e.hasMoreElements()) {
                JarEntry je = e.nextElement();
                System.out.println(je.getName()); // testing only
                if (je.isDirectory() || !je.getName().endsWith(".class")) {
                    System.out.println("...skipped"); // testing only
                    continue;
                }

                // -6 to trim ".class" off the end
                String className = je.getName().substring(0, je.getName().length() - 6);
                className = className.replace('/', '.');
                System.out.println("Loading class " + className + " ..."); // testing only

                try {
                    cl.loadClass(className);
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
            }

        } catch (MalformedURLException e1) {
            System.err.println("ERROR: malformed URL.");
            e1.printStackTrace();
            return false;
        }

        return true;
    }

    private static boolean testJDBCConnection(String dbUrl, String dbUser, String dbPassword) {

        boolean returnValue = true;
        DBConnection dbConnection = new oracleConnection();

//        DBConnection dbConnection = null;

        // Connect to database using connection pool.
//        switch (dbType.toLowerCase()) {
//            case DBType.MYSQL:
//                dbConnection = new mysqlConnection();
//                break;
//            case DBType.ORACLE:
//                dbConnection = new oracleConnection();
//                break;
//            default:
//                System.err.println("Invalid database type. Try mysql or oracle.");
//                return false;
//        }

        if (dbConnection.openConnection(dbUrl, dbUser, dbPassword)) {
            System.out.println("Running test SQL on database..."); // testing only

            // Test connection.
            if (dbConnection.isValidConnection()) {
                System.out.println("Test SQL successfully returned results.");
                returnValue = true;
            }
            else {
                System.out.println("Could not run test SQL.");
                returnValue = false;
            }

            // Close connection to database.
            System.out.println("Closing connection..."); // testing only
            dbConnection.closeConnection();
        }
        else {
            System.err.println("Error opening connection.");
            returnValue = false;
        }

        return returnValue;
    }


}
