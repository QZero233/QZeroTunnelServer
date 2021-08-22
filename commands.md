# QZeroTunnel中的指令相关

## 服务器->客户端

### 1.id

用法 `id <客户端ID>`

告知客户端此次连接中该客户端的ID

### 2.connect_relay_session

用法 `connect_relay_session <RelaySessionID>`

示意客户端连接转发服务器，并提供参数中的ID，进行接洽，并开始流量转发

### 3.disconnect

用法 `disconnect`

与当前指令服务器断开连接

## 客户端->服务器

### 1.login

用法 `login <用户名> <密码>`

进行身份验证

如果不通过身份验证，将无法执行任何操作