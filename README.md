# Network and Computer Security Project 2016-2017 #

Group 08 - Alameda Campus

75154	Jorge Miguel Tavares Veiga	jorgeveiga@tecnico.ulisboa.pt
75741	João André Dias Figueiredo	j.andre.dias@tecnico.ulisboa.pt
78865	Miguel Antão Pereira Amaral	miguel.p.amaral@tecnico.ulisboa.pt

Repositório:
[Programmer32/SIRS1617](https://github.com/Programmer32/SIRS1617/)

-------------------------------------------------------------------------------

## Install instructions

On project root directory:
```
mvn compile
mvn install
```

### Ambiente

1. Boot Linux

2. Start support servers
   JUDDI:  
   `$CATALINA_HOME/bin/startup.sh`

3. Create temporary directory 
   `cd $(mktemp -d)`

4. Clone project source code
   `git clone https://github.com/Programmer32/SIRS1617.git`

5. Install used library modules
   `cd uddi-naming`  
   `mvn clean install`  
   `cd ..`  
   `cd ui`  
   `mvn clean install`  
   `cd ..`

-------------------------------------------------------------------------------

### NotFenix Service 

[1] Build and Run **server**

```
cd notfenix-ws
mvn clean install
mvn package
mvn exec:java
```

[2] Build **client**  and run tests

```
cd notfenix-ws-cli
mvn clean install
mvn package
mvn exec:java
```

-------------------------------------------------------------------------------

### CA Service

[1] Build and run  **server**

```
cd CA-ws
mvn clean install
mvn package
mvn exec:java
```

[2] Build and run  **client**

```
cd CA-ws-cli
mvn clean install
mvn package
mvn exec:java
```

-------------------------------------------------------------------------------

**END**
