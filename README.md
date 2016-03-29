# Projeto de Sistemas Distribuídos 2015-2016 #

Grupo de SD 03 - Campus Alameda

Pedro Bucho 69537 pedro.m.bucho@tecnico.ulisboa.pt

João Figueiredo 75741 j.andre.dias@tecnico.ulisboa.pt

Miguel Amaral 78865 miguel.p.amaral@tecnico.ulisboa.pt


Repositório:
[tecnico-distsys/A_03-project](https://github.com/tecnico-distsys/A_03-project/)

-------------------------------------------------------------------------------

## Instruções de instalação 

!FIXME

### Ambiente

[0] Iniciar sistema operativo

Indicar Linux

[1] Iniciar servidores de apoio

JUDDI:
```
...
```
[2] Criar pasta temporária

```
cd ...
mkdir ...
```


[3] Obter código fonte do projeto (versão entregue)

git clone git@github.com:tecnico-distsys/A_03-project.git 

[4] Instalar módulos de bibliotecas auxiliares

//FIXME
cd uddi-naming
mvn clean install

```
cd ...
mvn clean install
```


-------------------------------------------------------------------------------

### Serviço TRANSPORTER

[1] Construir e executar **servidor**

```
cd ...-ws
mvn clean install
mvn exec:java
```

[2] Construir **cliente** e executar testes

```
cd ...-ws-cli
mvn clean install
```

...


-------------------------------------------------------------------------------

### Serviço BROKER

[1] Construir e executar **servidor**

```
cd ...-ws
mvn clean install
mvn exec:java
```


[2] Construir **cliente** e executar testes

```
cd ...-ws-cli
mvn clean install
```

...

-------------------------------------------------------------------------------
**FIM**
