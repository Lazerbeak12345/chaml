#!/bin/bash
flex chamlc.l
bison -d chamlc.y
gcc chamlc.tab.c lex.yy.c
#cat ../chaml/lib/if.chaml | ./chamlc
#cat ../../tests/main.chaml | ./chamlc
./a.out
rm lex.yy.c a.out chamlc.tab.c chamlc.tab.h
