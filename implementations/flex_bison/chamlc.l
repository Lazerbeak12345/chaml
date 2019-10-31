%{
    #include "chamlc.tab.h"
    #include <stdlib.h>
    void yyerror(char *);
%}

%%
    /* integers */
[0-9]+      {
                yylval = atoi(yytext);
                return INTEGER;
            }

    /* operators */
[()=/*%;,\n] { return *yytext; }

    /* skip whitespace */
[ \t]        ;

".plus"     { return PLUS; }
".minus"     { return MINUS; }
".times"     { return TIMES; }
".div"     { return DIV; }
".mod"     { return MOD; }

.           {
                yylval = *yytext;
                return IDENTIFIER;
            }

%%

int yywrap(void) {
    return 1;
}
