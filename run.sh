#!/bin/sh
for x in "$@"
do
    time java -cp out/production/*/ drones.EarliestCompletionFirst "$x" "${x%in}out" &
done
