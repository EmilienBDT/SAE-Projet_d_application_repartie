# SAE-Projet_d_application_repartie

## Test RMI BDD

Terminal dans dossier rmi-service :

### Avec Maven

```bash
mvn clean compile
mvn exec:java "-Dexec.mainClass=fr.iutnc.rmi.server.RMIServer"
mvn exec:java "-Dexec.mainClass=fr.iutnc.rmi.client.RMIClientTest"
```

### Avec JDK

- Windows
```bash
javac -cp "lib/*" -d out src/main/java/fr/iutnc/rmi/*.java
cp src/main/resources/config.properties out/
java -cp "out;lib/*" fr.iutnc.rmi.server.RMIServer
java -cp "out;lib/*" fr.iutnc.rmi.client.RMIClientTest
```

- Ubuntu
```bash
javac -cp "lib/*" -d out src/main/java/fr/iutnc/rmi/*.java
cp src/main/resources/config.properties out/
java -cp "out:lib/*" fr.iutnc.rmi.server.RMIServer
java -cp "out:lib/*" fr.iutnc.rmi.client.RMIClientTest
```

// \http-proxy> mvn clean compile exec:java