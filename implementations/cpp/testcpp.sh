#!/bin/bash
g++ lexer.cpp
lexFile() {
	./a.out $1 >> tmp
	rm a.out
}
lexFile ../chaml/lib/if.chaml
lexFile ../../tests/main.chaml
lexFile ../../tests/testlib.chaml
lexFile ../chaml/chamlc.chaml
less tmp
rm tmp
