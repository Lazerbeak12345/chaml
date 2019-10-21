#!/bin/bash
lexFile() {
	g++ lexer.cpp
	./a.out $1 >> tmp
	rm a.out
}
lexFile ../chaml/lib/if.chaml
lexFile ../chaml/chamlc.chaml
less tmp
rm tmp
