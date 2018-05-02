#!/bin/bash

offset=0
limit=100000
max=500000
endpoint=http://virtuoso-midi.amp.ops.labs.vu.nl/sparql
query="PREFIX+prov%3A+<http%3A%2F%2Fwww.w3.org%2Fns%2Fprov%23>+PREFIX+mid%3A+<http%3A%2F%2Fpurl.org%2Fmidi-ld%2Fmidi%23>+SELECT+%3Ffilename+%3Fpattern+WHERE+%7B+%3Fpattern+prov%3AwasDerivedFrom+%3Ffilename+%7D"
#"+LIMIT+%LIMIT+OFFSET+0&should-sponge=&format=csv%2Fhtml&timeout=0&debug=on"
max=$max+1
n=0; while [[ $n -lt $max ]]; do curl -d query="$query+LIMIT+$limit+OFFSET+$n" -d format="csv" "$endpoint"; n=$((n+$limit)); done

