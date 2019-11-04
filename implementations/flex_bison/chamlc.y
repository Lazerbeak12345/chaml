%token INTEGER
%token IDENTIFIER

    /*All tokens have equal precedence.*/
%token PLUS MINUS TIMES DIV MOD POW MCOMMENT

%{
    #include <stdio.h>
    //#include <map>
    void yyerror(char *);
    int yylex(void);
    int sym[300];
    //std::map<char *,int> sym;
    //std::map<int,char *> sym;
%}

%%

program:
    program statement '\n'
    | 
    ;

statement:
    expr ';'                    { printf("%d\n", $1); }
    | IDENTIFIER '=' expr ';'   { sym[$1] = $3; }
    | statement MCOMMENT
    ;

expr:
    INTEGER
    | IDENTIFIER        { $$ = sym[$1]; }
    | expr PLUS '(' expr ')'    { $$ = $1 + $4; }
    | expr MINUS '(' expr ')'   { $$ = $1 - $4; }
    | expr TIMES '(' expr ')'   { $$ = $1 * $4; }
    | expr DIV '(' expr ')'   { $$ = $1 / $4; }
    | expr MOD '(' expr ')'   { $$ = $1 % $4; }
    | '(' expr ')'      { $$ = $2; }
    | expr MCOMMENT
    | MCOMMENT expr;
    ;

%%

void yyerror(char *s) {
    fprintf(stderr, "%s\n", s);
}

int main(void) {
    yyparse();
    return 0;
}
