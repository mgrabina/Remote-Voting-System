#!/bin/bash

cd client/target/
if [ ! -d "$DIR" ]; then
  tar -xzf VotingSystem-client-1.0-SNAPSHOT-bin.tar.gz
fi
cd VotingSystem-client-1.0-SNAPSHOT/

chmod u+x run-queryClient.sh

./run-queryClient.sh $*


