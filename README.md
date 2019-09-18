# Remote-Voting-System

Run each section on a different terminal.

## Start Registry

cd into project root
chmod u+x ./setup_registry.sh
sh ./setup_registry.sh


## Start server

chmod u+x ./server.sh
sh ./server.sh


## Start clients
### Vote client
chmod u+x ./voteClient.sh
Mandatory Parameters:
-DserverAddress,--DserverAddress <arg>   IP address of the server
-DvotesPath,--DvotesPath <arg>           Absolute Votes file path
sh ./voteClient.sh 

### Fiscal client
chmod u+x ./fiscalClient.sh
sh ./fiscalClient.sh 

### Management client
chmod u+x ./managementClient.sh
sh ./managementClient.sh 

### Query client
chmod u+x ./queryClient.sh
sh ./votequeryClientClient.sh 

