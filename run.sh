#!/bin/sh
for x in "$@"
do
    time java -cp out/production/HashCode2016/ drones.EarliestCompletionFirst "$x" "${x%in}out" &
done
