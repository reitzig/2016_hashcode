#!/bin/bash
alg="";
for x in "$@"
do
  if [ "${alg}" == ""; ]
  then
    alg="${alg}";
  else
    time java -cp out/production/*/ drones."${alg}" "$x" "${x%in}out";
  fi
done
