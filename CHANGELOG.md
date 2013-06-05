## 0.9.0 RC (May 31, 2013)
 - Java 1.5/1.6 support with one jar.
 - Functional tests with older JDBC drivers.
 - C3P0 pooled datasource support.
 - Removal of one and only strict dependency - slf4j. Now it is optional: if it is present in classpath - it would be used, otherwise Java logging would be used.
 - Minor changes in Maven pom.xml files.
 - Removed some old code and fixed issue related to it.
 
## 0.9.1 RC (June 8, 2013)
 - **Added Microsoft SQL support for both JDBC3 and JDBC4.**
 - **Added Microsoft SQL functional tests (20+).**
 - Base statement handler improvements.
 - Base type handler improvements: now allows processing of output InputStream/Reader.
 - Introduction (actually rewrite) of new type handler. It is called Universal type handler as it is supported by both JDBC3/JDBC4 and MsSql, MySQL, PostgreSQL.
 - BaseMetadataHandler enhancement.
 - QueryParameters enhancement.
 - QueryRunner, BaseTypeHandler and QueryParameters test update.
 - Now system is more flexible: if metadata report 'return' field but it wasn't utilized in SQL String - system won't demand that field to be supplied (as out parameter). Applicable only for call with named input handler.
 - Improvements to TypeHandler utils.