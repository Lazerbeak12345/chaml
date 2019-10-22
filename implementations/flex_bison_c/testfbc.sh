#!/bin/bash
flex chamlc.l
gcc lex.yy.c -o chamlc
#cat ../chaml/lib/if.chaml | ./chamlc
cat ../../tests/main.chaml | ./chamlc
rm lex.yy.c chamlc
