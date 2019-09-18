#!/bin/bash
cp -i ../../../*.csv ./
java  -cp 'lib/jars/*' "ar.edu.itba.pod.client.VoteClient" $*

