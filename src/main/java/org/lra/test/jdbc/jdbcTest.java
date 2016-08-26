package org.lra.test.jdbc;

import java.io.Console;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Created by laurenra on 8/19/14.
 */
public class jdbcTest {
    public static void main(String[] args) throws IOException {

        String dbUrl = null;
        String dbUser = null;

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
        String commandHelpHeader = "\nTest JDBC connection with MySql driver\n\n";
        String commandHelpFooter = "\nExample:\n\n" +
                "  java -jar jdbcTestMySql.jar -u jdbc:mysql://hostname:port/dbname\n\n";

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar jdbcTestMySql.jar", commandHelpHeader, options, commandHelpFooter, true);
    }

    private static boolean testJDBCConnection(String dbUrl, String dbUser, String dbPassword) {

        boolean returnValue = true;
        DBConnection dbConnection = new mysqlConnection();

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
