# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 03 - Campus Alameda

Pedro Bucho 69537 pedro.m.bucho@tecnico.ulisboa.pt

João Figueiredo 75741 j.andre.dias@tecnico.ulisboa.pt

Miguel Amaral 78865 miguel.p.amaral@tecnico.ulisboa.pt


Repositório:
[tecnico-distsys/A_03-project](https://github.com/tecnico-distsys/A_03-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 

On project root directory:
```
mvn compile
mvn install
```

### Ambiente

1. Iniciar Linux
2. Iniciar servidores de apoio  
   JUDDI:  
   `$CATALINA_HOME/bin/startup.sh`
3. Criar pasta temporária  
   `cd $(mktemp -d)`
4. Obter código fonte do projeto (versão entregue)  
   `git clone git@github.com:tecnico-distsys/A_03-project.git -b SD_1`
5. Instalar módulos de bibliotecas auxiliares  
   `cd uddi-naming`  
   `mvn clean install`  
   `cd ..`  
   `cd ui`  
   `mvn clean install`  
   `cd ..`

-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**

```
cd transporter-ws
mvn clean install
mvn package
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd transporter-ws-cli
mvn clean install
mvn package
mvn exec:java

```

-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd broker-ws
mvn clean install
mvn package
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd broker-ws-cli
mvn clean install
mvn package
mvn exec:java
```

-------------------------------------------------------------------------------

### Serviço CA

[1] Construir e executar **servidor**

```
cd CA-ws
mvn clean install
mvn package
mvn exec:java
```

[2] Construir e executar **cliente**

```
cd CA-ws-cli
mvn clean install
mvn package
mvn exec:java
```

-------------------------------------------------------------------------------

**FIM**
