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

To change default value, you can find mysql.properties and server.properties in source code, update it and rebuild project

### Example

```shell
./java -jar xxx.jar --bannedPorts=25565,25566
```

