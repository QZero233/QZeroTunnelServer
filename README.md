# QZeroTunnelServer
A NAT traverse program's server

## User account configuration

User config files are all in a directory named users

Every file in that directory stands for a user

The name of the user is the name of the file

The content of the file is the user's password's hash(actually, sha256 value)

> eg.
>
> We have a user whose username is test
>
> Then we should create a file in directory users, rename it to test.config
>
> (Let assume the sha256 value of the user's password is password_hash)
>
> Then we edit the file, write password_hash to it
>
> Well, now we finish adding a user

