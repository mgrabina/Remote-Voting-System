#!/bin/bash
java  -cp 'lib/jars/*' "ar.edu.itba.pod.client.QueryClient" $*
cp -i ./*.csv ../../../
