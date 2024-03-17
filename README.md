# What is the code repository about

## 1.Implement a simple cas server for sso
### 1.1 Getting Started
start the cas-server:
```bash
cd cas-server
mvn clean package -DskipTests
java -jar cas-server-1.0.0-SNAPSHOT.jar
```
Access cas server at http://127.0.0.1:8000/cas/login to Login
default user/password: admin/123456

start the resource server:
```bash
cd resource-server
mvn clean package -DskipTests
java -jar resource-server-1.0.0-SNAPSHOT.jar
```

Access the resource server at http://127.0.0.1:8080

