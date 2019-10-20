#!/bin/bash
npx typescript lexer.ts
node lexer.js ../chaml/lib/if.chaml > if.lexed.chaml
rm lexer.js
cat if.lexed.chaml
#diff -y if.lexed.chaml ../chaml/lib/if.chaml
#rm if.lexed.chaml
