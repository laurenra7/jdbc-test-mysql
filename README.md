# jdbcTest
Test JDBC connection using MySQL JDBC driver.

See pom.xml for what driver version is used. Using **mysql-connector-java** version **5.1.31** currently.

**Build it:** 

```shell
mvn clean package
```

**Run it:**

```shell
java -jar target/jdbcTestMySql.jar
```

You can include the username and connection URL on the command line. If you don't include them on the command line, you will be prompted for them. You will always be prompted for the password. 

**Command line options:**

```
usage: java -jar jdbcTestMySql.jar [-h] [-n <userName>] [-u <URL>]

Test JDBC connection with MySql driver

  -h,--help                  Show this help
  -n,--username <userName>   Database user name
  -u,--url <URL>             URL to test

Example:

  java -jar jdbcTestMySql.jar -u jdbc:mysql://hostname:port/dbname
```

