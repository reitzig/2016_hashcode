#!/bin/bash
alg="";
for x in "$@"
do
  if [ "${alg}" == "" ];
  then
    alg="${x}";
  else
    time java -cp out/production/*/ drones.Main "${alg}" "$x" "${x%in}out" &> "${x%in}log";
  fi
done
