#!/bin/bash
for x in "$@"
do
	wc -l < "$x" > "$x.wc.out"
	cat "$x" >> "$x.wc.out" 
done
