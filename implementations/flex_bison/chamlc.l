%{
    #include "chamlc.tab.h"
    #include <stdlib.h>
    void yyerror(char *);
%}

%%

[0-9]+ {
                yylval = atoi(yytext);
                return INTEGER;
            }

\(         {return OPAREN;}
\)         {return CPAREN;}

[-+\n]      return *yytext;

[ \t]       ; /* skip whitespace */

.           yyerror("invalid character");

%%

int yywrap(void) {
    return 1;
}