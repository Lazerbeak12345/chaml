%{
    #include "chamlc.tab.h"
    #include <stdlib.h>
    void yyerror(char *);
%}

%%

    /* variables */
[a-z]       {
                yylval = *yytext - 'a';
                return IDENTIFIER;
            }

    /* integers */
[0-9]+      {
                yylval = atoi(yytext);
                return INTEGER;
            }

    /* operators */
[-+()=/*%\n] { return *yytext; }

    /* skip whitespace */
[ \t]        ;

    /* anything else is an error */
.               yyerror("invalid character");

%%

int yywrap(void) {
    return 1;
}
