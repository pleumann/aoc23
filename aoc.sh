#/bin/bash

if [ "$1" == "" ]
then
  echo "Usage: ./aoc23.sh <day> [<input>]"
  exit
fi
 
if [ "$2" == "" ]
then
  INPUT="input.txt"
else
  INPUT="$2"
fi

java -Xss1024M -cp dist/AdventOfCode23.jar day${1}.Puzzle src/day${1}/${INPUT}
