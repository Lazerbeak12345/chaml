lws	[ \t]*
%{
    int nParen,nComma,nComments;
%}
%%
^#!.*\n	;	//SHABANG!
{lws}"/*"[^/]*"*/"	{nComments++;}
{lws}"//".*\n	{nComments++;}
[()]	{nParen++;printf(" ");}
,		{nComma++;printf(" ");}
%%
int yywrap(void) {
    return 1;
}
int main(void) {
    yylex();
    printf("%d Parens\t%d Commas\t%d Comments\n", nParen,nComma,nComments);
    return 0;
}