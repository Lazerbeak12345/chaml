#!/bin/bash
lexFile() {
	g++ lexer.cpp
	./a.out $1
	rm a.out
}
lexFile ../chaml/lib/if.chaml > if.lexed.chaml
cat if.lexed.chaml
#diff -y if.lexed.chaml ../chaml/lib/if.chaml
rm if.lexed.chaml
#lexFile ../chaml/chamlc.chaml > chamlc.lexed.chaml
#diff -y chamlc.lexed.chaml ../chaml/chamlc.chaml
#rm chamlc.lexed.chaml