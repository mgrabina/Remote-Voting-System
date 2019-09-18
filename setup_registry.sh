#!/bin/bash
mvn install
cd server/target/
tar -xzf VotingSystem-server-1.0-SNAPSHOT-bin.tar.gz
cd VotingSystem-server-1.0-SNAPSHOT/
chmod u+x run-registry.sh
echo "Registry running"
./run-registry.sh

