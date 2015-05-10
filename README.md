# [Midao JDBC 0.9.5] (http://pryzach.github.io/midao/)

## About

**Midao is data centric project with clear goal to simplify Java development for computer (based) information systems**

**Midao JDBC is created to simplify development with JDBC**

Goal of Midao JDBC project is to shield developer from nuances of vendor JDBC implementation and standard JDBC boilerplate code.

**Unlike standard JDBC libraries** - it allows usage of classes/maps, custom error handling, transaction handling, type conversion, cached and lazy queries, stored procedure handling and pooled connections.

**Unlike ORM frameworks** - it doesn't hide SQL from you, while allowing you to use classes/maps to set and read values from Databases. Class/map mapping also is allowed for stored procedures.

**Customization and Simplicity** - library tries to provide simplicity of DbUtils while providing functionality/customization of Spring JDBC and often going beyond that.

**Pluggable architecture** - allows to plug-in any suitable pooled datasource. Provides custom handlers tailored to specific DB Vendor. Provides some Spring JDBC handlers to make usage/migration from Spring JDBC easier/comfortable.

**Versatile** - with single jar supports both JDBC 3.0(Java 5) and JDBC 4.0(Java 6).

**Well tested** - not only it has around 800 unit and functional tests, but also it is tested with latest drivers of: Derby, MySQL (MariaDB), PostgreSQL, Microsoft SQL and Oracle.

## Start using

1. Add as Maven dependency

```
    <dependency>
      <groupId>org.midao</groupId>
      <artifactId>midao-jdbc-core</artifactId>
      <version>0.9.5</version>
    </dependency> 
```

2. Download jar directly [midao-jdbc-core-0.9.5.jar] (http://pryzach.github.io/midao/mjdbc/midao-jdbc-core-0.9.5.jar) and add to classpath

3. Browse [midao.org] (http://midao.org) and [JavaDoc] (http://pryzach.github.io/midao/javadoc/index.html)

## Get involved

If you would like to submit issue, ask a question or propose some functionality - please use [GitHub Issue Tracker] (https://github.com/pryzach/midao/issues)

## Contribute

If you would like to help with development - please contact me via [pryzach@gmail.com] (mailto:pryzach@gmail.com) or post a question using [GitHub Issue Tracker] (https://github.com/pryzach/midao/issues).

## Follow us

If you would like to keep track of this project you may want to:

 * Follow [@pryzach on Twitter] (http://twitter.com/pryzach).
 * Look at [Change log] (https://github.com/pryzach/midao/blob/master/CHANGELOG.md)
 * Subscribe to announcements@midao.org by directly writing email to that group.
 * Keep an eye on [status page] (http://pryzach.github.io/midao/status.html)

## License

Licensed under Apache License 2.0.

## For more information please visit [www.midao.org] (http://midao.org) 