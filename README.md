# Payment processing project

### First start
For the first start you need to create three database instances and execute sql script in each instance which located in resources/db/migration/first folder.

Then you need to specify connection attributes in application.properties file.

For example:
app.datasource.[datasorce name].jdbc-url - jdbc url connection for specific database
app.datasource.[datasorce name].username - username
app.datasource.[datasorce name].password - password
app.datasource.[datasorce name].driver-class-name - jdbc driver class name, e.g. org.postgresql.Driver for PostgreSQL database

### Project on GitGub
https://github.com/AlvaroCortez/payment-processing
