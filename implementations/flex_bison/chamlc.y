%token INTEGER IDENTIFIER
%left '+' '-'
%left '*' '/' '%'

%{
    #include <stdio.h>
    void yyerror(char *);
    int yylex(void);
    int sym[300];
%}

%%

program:
    program statement '\n'
    | 
    ;

statement:
    expr                       { printf("%d\n", $1); }
    | IDENTIFIER '=' statement { sym[(int) $1] = $3; }
    ;

expr:
    INTEGER
    | IDENTIFIER        { $$ = sym[(int) $1]; }
    | expr '+' expr     { $$ = $1 + $3; }
    | expr '-' expr     { $$ = $1 - $3; }
    | expr '*' expr     { $$ = $1 * $3; }
    | expr '/' expr     { $$ = $1 / $3; }
    | expr '%' expr     { $$ = $1 % $3; }
    | '(' expr ')'      { $$ = $2; }
    ;

%%

void yyerror(char *s) {
    fprintf(stderr, "%s\n", s);
}

int main(void) {
    yyparse();
    return 0;
}
