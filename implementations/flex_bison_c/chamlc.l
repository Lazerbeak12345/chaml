lws	[ \t]*
%{
    int nParen,nComma,nComments;
%}
%%
^#!.*\n             ;   //SHABANG!
{lws}"/*"[^/]*"*/"  {nComments++;}
    /*
Due to some issues with the character / tempararlly removing multiline
comments (or adding a further layer of abstraction at an earlier) might be a good idea
*/
{lws}"//".*\n       {nComments++;}
\(                  {nParen++;printf("(");}
\)                  {nParen++;printf(")");}
,                   {nComma++;printf(",");}
%%
int yywrap(void) {
    return 1;
}
int main(void) {
    yylex();
    printf("%d Parens\t%d Commas\t%d Comments\n", nParen,nComma,nComments);
    return 0;
}
