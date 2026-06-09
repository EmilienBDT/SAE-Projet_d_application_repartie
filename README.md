# SAE-Projet_d_application_repartie

## Groupe
- BODAT Emilien
- DI RENZO VILLER Aurélio

## Projet

### Avec Maven
```bash
mvn clean compile
# Lancer le service RMI
mvn exec:java -pl rmi-service
# Lancer le proxy
mvn exec:java -pl http-proxy
```

## HTTP-Proxy
Terminal dans dossier http-proxy :

### Avec Maven
```bash
mvn clean compile
# Vérifier l'API
mvn exec:java "-Dexec.mainClass=fr.iutnc.proxy.DataFetcher"
# Lancer le serveur
mvn exec:java "-Dexec.mainClass=fr.iutnc.proxy.ProxyServer"
```

### Avec JDK
- Windows
```bash
javac -cp "lib/*;../rmi-service/out" -d out src/main/java/fr/iutnc/proxy/*.java
cp src/main/resources/config.properties out/
java -cp "out;lib/*;../rmi-service/out" fr.iutnc.proxy.DataFetcher
java -cp "out;lib/*;../rmi-service/out" fr.iutnc.proxy.ProxyServer
```
- Ubuntu
```bash
javac -cp "lib/*:../rmi-service/out" -d out src/main/java/fr/iutnc/proxy/*.java
cp src/main/resources/config.properties out/
java -cp "out:lib/*:../rmi-service/out" fr.iutnc.proxy.DataFetcher
java -cp "out:lib/*:../rmi-service/out" fr.iutnc.proxy.ProxyServer
```

## RMI-Service
Terminal dans le dossier rmi-service :

### Avec Maven
```bash
mvn clean compile
# Lancer le serveur
mvn exec:java "-Dexec.mainClass=fr.iutnc.rmi.RMIServer"
# Lancer le client
mvn exec:java "-Dexec.mainClass=fr.iutnc.rmi.RMIClientTest"
```

### Avec JDK
- Windows
```bash
javac -cp "lib/*" -d out src/main/java/fr/iutnc/rmi/*.java
cp src/main/resources/config.properties out/
java -cp "out;lib/*" fr.iutnc.rmi.RMIServer
java -cp "out;lib/*" fr.iutnc.rmi.RMIClientTest
```
- Ubuntu
```bash
javac -cp "lib/*" -d out src/main/java/fr/iutnc/rmi/*.java
cp src/main/resources/config.properties out/
java -cp "out:lib/*" fr.iutnc.rmi.RMIServer
java -cp "out:lib/*" fr.iutnc.rmi.RMIClientTest
```