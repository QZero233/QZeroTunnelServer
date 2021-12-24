# QZeroTunnelServer
## Description

A NAT traverse program's server

## Start script

## Common script

```shell
./java -jar xxx.jar <Parameters>
```

Parameters' format should be `--parameterName1=parameterValue1 --parameterName2=parameterValue2 ...`

#### Parameter List

All the parameter are optional

**Caution:**

**Change the mysql properties if you have a customized one**

| ParameterName              | Detail                                                       | Default（if have one） |
| -------------------------- | ------------------------------------------------------------ | ---------------------- |
| server.port                | The main port of the server, which user will input in client | 8080                   |
| server.remindServerPort    | Port of remind server, client will get it automatically, change it only when there is conflict | 9997                   |
| server.receptionServerPort | Port of relay server, client will get it automatically, change it only when there is conflict | 9995                   |
| server.bannedPorts         | Ports that users are not allowed to use in tunnel openning, split each port with comma, like 8888,9999,1234 |                        |
| ssl.enabled                | Whether enable https connection                              | false                  |
| ssl.keyStorePath           | The path of your https keystore                              |                        |
| ssl.keyStorePassword       | The password of your https keystore                          |                        |
| ssl.keyAlias               | The alias of your https keystore                             |                        |
| ssl.keyStoreType           | The key store type of your https keystore                    | JKS                    |

To change default value, you can find mysql.properties and server.properties in source code, update it and rebuild project

### Example

```shell
./java -jar xxx.jar --bannedPorts=25565,25566
```

## Notice

### Usage of https

Usually, we use self signed certification,

To generate one, you need install jdk, and use `keytool` command

Examples are below:

```shell
keytool -genkey -alias server_test  -keyalg RSA -keystore D:\\server_test.keystore
```

Then input your password for twice

**NOTICE: The name must be your hostname such as 127.0.0.1 or www.testfire.net**
