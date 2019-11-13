# The CHAML Standard

A programming language that spins off of this question: "Is it possible to make
a programming language without any keywords?"

The answer so far: sort of, but not really. It depends if you count operators as
keywords, and if you count built-in functions.

There is also going to be support for many (if not all) of the aspects of LOP
(Language Oriented Programming), what I regard to be the next programming
paradigm. But it also has support for OOP, via syntax extensions (thanks to LOP
), and/or it's built-in object prototyping.

The name of the language is an acronym:

- CHAML
- Has
- A
- Modifiable
- Language

It is designed to make using functions easy-peasy. Without syntax extensions,
there are 6 (ish) syntaxes to define a function, and all functions are
first-order, so they can take other functions as arguments.

I'm actually looking for help with the initial runtime. Once that's done, it'll
be bootstrapped.

<!--Look at https://vim.fandom.com/wiki/Creating_your_own_syntax_files later-->
<!--Look at https://stackoverflow.com/questions/38148857/customizing-syntax-highlighting-in-visual-studio-code-->

Here's a code sample for those interested:

```chaml
/**
 * An implementation of else and elif using only if
 */
=<(b,f) {
  if(b,f);
  state=b;
  out={};
  out.elif=(b,f) {
    state=state.or(not(b));
    if(not(state),f);
    =<out;
  };
  out~out.elif;//Overload out to take elif
  out.else=(f) {
    if(not(state),f);
  };
  out~out.else;
  =<out;
};
```

These features are core to the design, and are very unlikely to change very
much.

- No keywords
  - Also try to avoid things like them. (Operators and Types are to be used
  sparingly)
- A sleek, secure way of having per-file syntax overloading.
  - Syntax modules included at the beginning of the file, much like imports
- Functions are call by value
- Everything is a function
- Any operator may be applied to any variable, at all.
  - EX: while `7.times(10)` returns `70`, a function that returns 7 multiplied
  by a function that returns 10 returns a function that returns the result of
  that operator applied to each value, respectively.

```chaml
retSeven=()=>7;
retTen=()=>10;
resultGetter=retSeven.times(retTen);
endResult=resultGetter();
```

## Table of Contents

- The Chameleon Programming Language Standard
  - Table of Contents
  - The compiler
    - Stages
    - Runtime
    - Current state
    - Plans
  - Variables
  - Functions
    - Defining functions
      - No args
      - No args One line
      - One Arg
      - One arg One Line
      - Multi-arg
      - Multi-arg One line
    - Calling functions
      - Scoping
        - When var names collide
    - Returning
      - From inside a one-liner
      - From a block function
  - Operators
  - Other features
  - Syntax
    - Comments
    - Typecasting
  - Reserved functions
    - throw
    - while
    - if
    - import
    - useSyntax
    - declareSyntax
    - Regexp
  - Resources

### The future TOC

- [8/12] The Chameleon Programming Language Standard
  - [x] Table of Contents
  - [x] The compiler
  - [x] Variables
  - [x] Functions
  - [x] Operators
  - [x] Types
  - [ ] Overloading
    - [ ] Overloading functions
    - [ ] Overloading types
    - [ ] Overloading operators
    - [ ] Overloading syntax - (points to dedicated section)
  - [ ] Builtins
    - [ ] `.ext`
  - [ ] Libraries
    - [ ] Using libraries
      - [ ] `import`
      - [ ] Some example libraries
        - [ ] IO
        - [ ] Vector
        - [ ] Regexp
    - [ ] Writing libraries
  - [ ] Syntax Extension
    - [ ] Using a syntax module
      - [ ] Common modules
    - [ ] Writing a syntax module
      - [ ] Tokeniser
      - [ ] Parse tree
      - [ ] AST
      - [ ] Reverse AST
      - [ ] Reverse Parse Tree
      - [ ] Reverse Tokeniser
  - [x] Resources

## The compiler

### Stages

1. Feed source code (call this file A) into tokenizer (outputs a stream). Go to
step 2.
2. Feed token stream into parser. If an import or syntax extention is found, go
to step 2.1, or else go to step 3.
   1. Grab the file that the import or syntax extention requires, and pass that
  into step 1. (call this file file B) If it's a syntax module, go to step 2.3,
  or else go to step 2.2.
   2. Get the entire Parse tree for file B, and insert it into the proper node
   on file A's parse tree. Go to step 3 (not to be confused with step 2.3).
   3. Run File B through the interpreter (not the JIT compiler). It may modify
  the token list, parse tree generator, Ast tree generator, interpeter, AST tree
  reverser, parse tree reverser, and or the token reverser.
  This modification _only_ applies to this local file A (not file a's parent, if
  this is multiple levels deep, and not file B). Continue to step 3.
3. Convert the parse tree into an AST tree. If file A is being interpreted run
the file, or else continue to step 4.
4. Convert the AST tree into a parse tree for the output language (or language
family) Continue to step 5.
5. Convert the parse tree into a token stream for the output language (or
language family). Coontinue to step 6.
6. Convert the token stream into the transpiled code for the output language.
(Most often this is LLVM, but I've been thinking about output to JavaScript,
JVM, and a few others). If the compiler is in JIT mode, run the file.

### Runtime

As the standard output of the file is planned to be LLVM code, it will have
an enviroment simmilar to that one, but I am considering having JVM and/or
JavaScript as options for target languages.

The exeption to this is when it's in interpeter mode, but this should function
about the same.

### Current state

There are multiple parallel attemps to reach a working end-goal.

- The cpp implimentation is the oldest one. It right now can only just barely
handle some comments, sometimes. I'm not likely to continue pursuit with this
one, I'm just using it for reference, and will most likely remove it.
- The chaml one is 75% done, but once I realised I made a few critical mistakes
(such as not actually knowing what a lexer is and what a parser is), I decided
to stop for awhile.
  - In the lib directory are libraries that I plan to have availiable. Many are
  highly experimental.
  - I plan on re-writing this once a different implimentation starts working.
- flex_bison was going to be the offical first language compiler/interpreter/
whatever_the_heck_I_actually_got_out_of_it, but as I am not super familiar with
C or C++ I am looking into alternatives. I actually can do basic math in the
library as if it was actually done, but it feels unreliable.
- java is my potential replacement for flex_bison. Very little progress, but as
I know the datatypes well enough, I should be able to make this work.

### Plans

GET IT WORKING

I personally don't have any plan but this, and what is in the "Current state"
section

## Variables

All variables are functions. When set with the `=` operator, it is overwritten
with the value on the right side. When set with the `~` (tilde) operator, the
caller is overloaded.

Naming conventions are identical to JavaScript; they may contain any number,
upper or lowercase letter, or these: `$_`. They cannot start with a number.

```text
newVar=3; // number
anotherVar=()=>3; //function returning a number
_yetanoth3rVar='c'; // this is just a char, but can be cast to and from a number
arrofnums=[3,5,3,2,5,64,5,64,3];//cant change len, but you can replace the value
$another="This is shorthand for an array of chars";
theLast1=true;//And here's a bool.
```

### Variables proposal 2

Names may include any character otherwise not used in any syntax.

## Functions

Uses call/pass by value.

### Defining Functions

Supports 6 ways of declaring functions, listed below

#### No args

```text
anyVariableHere={
  //do something upon invocation
};
```

#### No args One line

```text
anyVariableHere=()=> 72;// return 72
```

#### One Arg

```text
anyVariableHere=(in) {
  =<in.times(2); // return the arg times two
};
```

#### One arg One Line

```text
anyVariableHere= in => in*2;//a single line is treated like a code block
```

#### Multi-arg

```test
anyVariableHere= (a,FEW,dif_ferent,var5,h$r3) {
  =<a.plus(FEW).plus(dif_frent).plus(var5).plus(h$er3);
};
```

#### Multi-arg One line

```test
anyVariableHere= (a,FEW,dif_ferent, var5,h$r3) => a.plus(FEW).plus(dif_frent).plus(var5).plus(h$er3);
```

### Calling functions

```chaml
a=()=>1;
b=a=>a.plus(1);
c=(a,b)=>a.plus(b);

//Call a
a();        // returns 1
//call b, passing a value. (if a var, it's duplicated)
b(3);       // returns 4
c(3,7);     // returns 10
```

### Overloadig functions

The `~` (tilde) operator makes the function on the right the function to call
given its number of arguments and the args that were passed in.

```chaml
tog=true;//A value that myF will be modifying
myF={//If no arguments are given, toggle `tog`
  if(tog,{
    tog=false;
  },{
    tog=true;
  });
};
myF~(firstArg) {//If one argument is given, unconditionally set `tog` to that
  tog=firstArg;
};
a=0;
myF~(ifT,ifF){//If two arguments are given, set `a` to the first if `tog` is true, and the second if it is false
  if(tog,{
    a=ifT;
  },{
    a=ifF;
  });
};
myF();//Toggle tog
myF(true);//Set tog to true
myF(10,20);//set a to 10 if tog is true, and set a to 20 if tog is false
//`a` should now be equal to 10.
```

#### Scoping

```text
//can't change or read any vars here
a=1947;
//`a` is the only var that can be changed or read here
theFunc=(e) {
  //`a`, `theFunc` and `e` are the only vars that can be changed or read here
  b=28142;
  //`a`, `theFunc`, `e` and `b` are the only vars that can be changed or read here
  theFunc.c=4234;
  //`a`, `theFunc`, `theFunc.c`, `e` and `b` are the only vars that can be changed or read here
  theFunc.p=()=>b;
  //`a`, `theFunc`, `theFunc.c`, `theFunc.p`, `e` and `b` are the only vars that can be changed or read here
  e=e.plus(1);
};
//`a` and `theFunc` are the only vars that can be changed or read here
d=324;
//`a`, `theFunc` and `d` are the only vars that can be changed or read here
theFunc.c=7;
//`a`, `theFunc`, `theFunc.c` and `d` are the only vars that can be changed or read here
v=99;
//`a`, `theFunc`, `theFunc.c`, `d` and `v` are the only vars that can be changed or read here
theFunc(v);
//`a`, `theFunc`, `theFunc.c`, `theFunc.p`, `d` and `v` are the only vars that can be changed or read here. `b` may be indirectly read through calling `theFunc.p`, but cannot be changed here
```

Additionally, functions can be defined within other functions. If you can read a
function, you can run it. As they are stored within variables, they follow the
same scoping when it comes to running the function.This also applies to
variables within functions within functions:

```text
//no vars can be read here
outerFunc={
  //`outerFunc` is the only var that can be changed or read here
  innerFunc={
    //`outerFunc` and `innerFunc` are the only vars that can be changed or read here
    a=true;
    //`outerFunc`, `innerFunc` and `a` are the only vars that can be changed or read here
  };
  //`outerFunc` and `innerFunc` are the only vars that can be changed or read here
};
//`outerFunc` is the only var that can be changed or read here
```

##### When var names collide

Sometimes, variable names are unintentionally reused by devs. Here's an example
of functional, yet poorly written code.

```text
conflicingName=849234;
funcName=conflictingName=> conflicingName();

funcName(() => 21); //returns 21

anotherName=(conflicingName) {
  =<conflictingName.times(10);
};

k=3;
anotherName(k)//returns 30

//conflictingName is still 849234
```

### Returning

#### From inside a one-liner

```text
returnsNumber1=()=>1;
returnsArgTimesTwo=arg=>arg.times(2);
returnsTheSumOfArgs=(a,b)=>a.plus(b);
```

#### From a block function

```chaml
returnsNumber1={
  =<1;
};
returnsArgTimesTwo=(arg) {
  =<arg.times(2);
};
returnsTheSumOfArgs=(a,b) {
  =<a.plus(b);
};
```

## Operators

<!--NOTE to self: look at https://en.wikipedia.org/wiki/Graph_reduction-->

- SET `=`
- OVERLOAD `~` (Tilde. Sets the internal "caller" of the following closure's arg
len to the following closure)
- RETURN `=<`
- LAMBDA `=>`
- SUB-PROPERTY-ACCESS `.`

## Types

### Type inference

Types, by default, are strict as java, yet as inferred as python.
Each variable, once initially set, retains type metadata, including the
constructor, and requires that all values stored in that variable are either
directly, or indirectly constructed by the constructor bound to the variable.

The constructor bound to a value can be accessed with the `.constructor`
property. Well-formed constructors will use `.ext` to "extend" the return of
it's "parent" constructor. The return of `.ext` is the same as that it is called
upon, but when a value is missing, it is looked for in the value passed in to
`.ext`.

### Type constructors

Type constructors are functions where have this behavior given the number of
args;

Zero returns the default value, if applicable. (Think of it like a default
constructor)

One argument returns a duplicate of this type if it is already this type.
Elsewise, call that class's `class.to.` then whatever this class is.
EX: `class.to.Foo` inside of a class called `Foo`.

Any more arguments are type specific.

#### `Func`

This constructor is the default constructor for all values.

When a single argument is passed in, it returns that argument, with all of its
properties an methods stripped. It will, however, retain it's executor and its
overloads. For example, the return of `Func(Bool)` is just `Bool` but without
any sub-properties. In fact, aside from that crutial difference, it should
function identically.

#### `Bool`

The `Bool` constructor is the constructor for booleans, and is not built in (it
is, however globablised by default). It's actually one of the first constructors
written in chaml. Here's some things exposed in it's definition:

- `true` A function returning its first argument
- `false` A function returning its second argument
- public `not` function returns inverse of passed in boolean
  - has binary logic functions/methods
    - `and`
    - `or`
    - `xor` Exclusive or
  - other
    - `eq`

#### `Int`

A built in type. One _strictly_ cannot downcast to this from a function.

- has mathmatical methods on children- has mathmatical methods on children
  - `plus`
  - `minus`
  - `times`
  - `div`
  - `mod`
- also has boolean returners on children
  - `gt` greater than
  - `lt` less than
  - `gte` greater than or equal to
  - `lte` less than or equal to
- other
  - `eq`
  - `plus`
  - `minus`
  - `times`
  - `div`
  - `mod`
- also has boolean returners on children
  - `gt` greater than
  - `lt` less than
  - `gte` greater than or equal to
  - `lte` less than or equal to
- other
  - `eq`

#### `Char`

A constructor inheriting from `Int`. Like bool it is, by default, globalised.

- getCode returns the charcode
- other
  - `eq`

#### `Arr`

An array. I'm not really sure if this should be built in or not. (it wouldn't be
to hard to use booleans to define it, but would have some preformance drawbacks)

- `.size` returns the size
- other
  - `eq`

#### `Str`

Strings are just arrays of chars. Shorthand for `['h','i']` is `"hi"`. Possibly
a default Syntax Extention? Inherits from `Arr`, of course.

## Other features

- Object inheritance
  - The core components are provided (via some built-in prototyping), but can be
  improoved with a library.
- Keywords, common operators, inline xml, obj syntax sugar all to be syntax
modules.

## Syntax

### Overload lib ideas

- keywords
  - A full explicit type system
- inline xml
- lazy function calling (from perspective of caller)
- A lib to make ifs C-style
- a lib to make strings their own datatype
- a lib to make bools an instance of Int (0 or 1)
- a lib to generate a documentaiton markdown file using comments
  - could interact with the keywords lib idea and its children
  - could interact with the AST too

### Comments

```text
//Single-line C++ style.

/*
Along with muli line C style are both supported
*/

//* single line comments are evaluated first

And_thus="this line is still reached and evaluated";

/**
 * This type of comment is intended documentation for smooth integration with
 * your IDE, if it supports it. Must be valid markdown, but type indicators
 * may be accepted.
 *
 * No IDE's are known to support the language more then they would a .txt file.
 * Replace this with a labled list of known IDE's in the future. If the count
 * is greater then 20, remove this list.
 */
```

Comments are preserved as long as possible, with few exeptions (ex: interpeting
mode) throughout the compiling process. This means it should be possible to
make comment dependant functionality

### Typecasting

Variables are never cast automatically.

To cast use the constructor like so:
 `joinOfStringAndNum="The number is ".plus(String(37))`

One can overload casting by doing something like this:

```text
//Overload casting of Array to Boolean in all cases
Array.to.Boolean={
  //Return what you want the value to be. NOTE: BROKEN DUE TO LACK OF CURRENT VAL ACCESS!
};
//Overload instance only
myNumber=4234
myNumber.to.Char={
  //Same as before, return what you want the value to be. NOTE: BROKEN DUE TO LACK OF CURRENT VAL ACCESS!
};
```

- To `Boolean`
  - `Boolean` duplicate of original boolean
  - `Int` true only if equal to `0` or `-0`
  - `Array` If array is len of 1 and it is a bool, then that bool, else error
  thrown: `Casting error: Cannot cast Array to Boolean`
- To `Char`
  - `Bool` `'t'` if true, `'f'` if false
  - `Char` duplicate of original char
  - `Int` local charcode conversion to char. If invalid, throw sensible error
  like `Casting error: Cannot cast Int(`insert num here`) to Char` as it is
  value dependant.
  - `Arr` If array is len of 1 and it is a char, then that char, else error
  thrown: `Casting error: Cannot cast Array to Char`
- To `Int`
  - `Bool` `0` if true, `1` if false
  - `Char` local charcode conversion to int. If invalid, throw sensible error
  like `Casting error: Cannot cast Char(`insert char here`) to Integer` as it is
  value dependant.
  - `Int` duplicate of original num
  - `Arr` If array is len of 1 and it is a number, then that number, else
  error thrown: `Casting error: Cannot cast Array to Int`
  - `Str` If the string is what would pass for a valid in-line literal of any
  type (decimal, hex, etc.) then resolve it, elsewhise, throw:
  `Casting error: Cannot cast String(`insert string here`) to Int`
- To `Arr`
  - empty, array len of 0
  - `Bool` `"True"` if true, `"False"` if false.
  - `Char` The char is turned into a string with len of 1
  - `Int` empty array of the length of that number.
    - Optional 2nd arg: what to fill it with.
    - If not enough memory, error thrown: `Casting error: Cannot cast Int(`insert num here`) to Array`
  - `Array` duplicate of origianl array
- To `Func` all cases: return a function that returns the input, unless it
is empty, then throw `Casting error: Function constructor takes 1 argument`

Assume that missing cases signify that that function doesn't exsist on that
object.

## Reserved functions

### throw

UNDEFINED BEHAVIOR

### while

- Takes 2 args,
  - A function returning a boolean (as it is called multiple times)
  - a function called after each time the boolean is found to be true.
    - Also must return a boolean or nothing. If found to be false, but not
    undefined or null, loop stops.

This should allow for while, while-do, do-while and for behavior, as well as
other awesome combos (like a while-do-while, do-while-do, or do-while-do-while).

part of the `Bool` library

### if

- Takes 2 args,
  - a boolean
  - a function called when boolean is true.
- Returns an object containing two methods:
  - `elif` takes the same args as if and returns the same thing as if, but the
  function is only called if all previous `if` or `.elif` bools are false.
  In other words, it's only called when the state of the `if` is false. (see
  `else` below). Also returns the same thing as `if`.
  same thing(s) as `if`.
  - `else` takes a function, called only if all previous functions are false.
  Has no return.

If returns a function that resolves itself. Untill this is run, the if is not
evaluated.

```chaml
if(true,{
  //code if true
});

if(1>0,{
  //code if true
});

if(false,{
  //code if true
}).else({
  //code if false
});

if(99==93,{
  //code if first true
}).elif(false,{
  //code if 2nd true
}).else({
  //code if 1 & 2 false
});
```

part of the `Bool` library

### import

NOTE: access restriction may need to be changed

- Takes 1-2 args
  - A string that must refer to either a module name, a Unix-style url to a
    file (extention not needed), or to a URL resorce that the OS can handle.
    - NOTE: this file can also be a Redox style URL an IPFS style adress, or a
      git adress.
  - An optional callback
- Returns the value
- When module is called, the module has no access to anything else and is
  gennerally treated as its own seperate, private program. Said module may
  also call import.
  
The imported module is logically wrapped around a function, the return of which
is what becomes the return of [[import]].

### useSyntax

NOTE: access restriction may need to be changed

- Takes 1-2 args
  - A string with the same requirements as arg 0 of *import*
  - An optional variable that is one of the following:
    - An array of strings with the below description
    - A string that exactly matches the name of the syntax conversion defined by
    `declareSyntax`.
    - A char (or single char str) `'*'` (sigifying all)

### declareSyntax

NOTE1: access restriction may need to be changed
NOTE2: huge changes are going to happen here

Takes three args:

- A string representing the name of the conversion
- A regexp finding the match
- A function taking the match, returning the replacement.

<!--TODO: Change name of this to better reflect its function-->

### Regexp

A new regexp element.

## Resources

These are some but not all of the resources that I have used thus far.
(In no particular order)

- [http://lucacardelli.name/Papers/TypeSystems.pdf](http://lucacardelli.name/Papers/TypeSystems.pdf)
  - Here's the IPFS link: [/ipfs/QmcrFvSwxarj2r1HDMsBzcjt5SgtMcvCp8ByuFLk97WEov/TypeSystems.pdf](https://ipfs.io/ipfs/QmcrFvSwxarj2r1HDMsBzcjt5SgtMcvCp8ByuFLk97WEov/TypeSystems.pdf)
- [https://thecodeboss.dev/2016/02/programming-concepts-type-introspection-and-reflection/](https://thecodeboss.dev/2016/02/programming-concepts-type-introspection-and-reflection/)
- [https://www.info.ucl.ac.be/~pvr/VanRoyChapter.pdf](https://www.info.ucl.ac.be/~pvr/VanRoyChapter.pdf)
  - Here's the IPFS link: [/ipfs/QmcrFvSwxarj2r1HDMsBzcjt5SgtMcvCp8ByuFLk97WEov/VanRoyChapter.pdf](https://ipfs.io/ipfs/QmcrFvSwxarj2r1HDMsBzcjt5SgtMcvCp8ByuFLk97WEov/VanRoyChapter.pdf)
- [https://thecodeboss.dev/2015/11/programming-concepts-static-vs-dynamic-type-checking/](https://thecodeboss.dev/2015/11/programming-concepts-static-vs-dynamic-type-checking/)
- [https://www.tutorialspoint.com/compiler_design/compiler_design_regular_expressions.htm](https://www.tutorialspoint.com/compiler_design/compiler_design_regular_expressions.htm)
- [https://en.wikipedia.org/wiki/Extensible_programming](https://en.wikipedia.org/wiki/Extensible_programming)
There are a lot of ideas here that I want. See the section titled "extensible
compiler"
- [http://wiki.c2.com/?ExtensibleProgrammingLanguage](http://wiki.c2.com/?ExtensibleProgrammingLanguage)
- [http://wiki.c2.com/?MetaProgramming](http://wiki.c2.com/?MetaProgramming)
- [https://en.wikipedia.org/wiki/Lambda_calculus](https://en.wikipedia.org/wiki/Lambda_calculus)s
- [https://www.epaperpress.com/lexandyacc/index.html](https://www.epaperpress.com/lexandyacc/index.html) This is actually more than one link, and this is the TOC for them.
- [http://www.paulgraham.com/langdes.html](http://www.paulgraham.com/langdes.html)
Great for knowing the "why" behind different different ideas.
- [http://wiki.c2.com/?AutomatedCodeGeneration](http://wiki.c2.com/?AutomatedCodeGeneration)
- [http://wiki.c2.com/?CompileTimeResolution](http://wiki.c2.com/?CompileTimeResolution)
- [https://wespiser.com/writings/wyas/01_introduction.html](https://wespiser.com/writings/wyas/01_introduction.html)
- [http://www.theenterprisearchitect.eu/blog/2013/02/14/designing-the-next-programming-language-understand-how-people-learn/](http://www.theenterprisearchitect.eu/blog/2013/02/14/designing-the-next-programming-language-understand-how-people-learn/)
- [https://tomassetti.me/antlr-mega-tutorial/](https://tomassetti.me/antlr-mega-tutorial/)
- [https://tomassetti.me/resources-create-programming-languages/](https://tomassetti.me/resources-create-programming-languages/)
- [http://wiki.c2.com/?MetaProgramming](http://wiki.c2.com/?MetaProgramming)
- [http://wiki.c2.com/?StumblingBlocksForDomainSpecificLanguages](http://wiki.c2.com/?StumblingBlocksForDomainSpecificLanguages)
- [http://wiki.c2.com/?LanguageOrientedProgramming](http://wiki.c2.com/?LanguageOrientedProgramming)
- [http://www.jayconrod.com/posts/37/a-simple-interpreter-from-scratch-in-python-part-1](http://www.jayconrod.com/posts/37/a-simple-interpreter-from-scratch-in-python-part-1)
- [https://ruslanspivak.com/lsbasi-part1/](https://ruslanspivak.com/lsbasi-part1/)
- [https://www.codeproject.com/articles/345888/how-to-write-a-simple-interpreter-in-javascript](https://www.codeproject.com/articles/345888/how-to-write-a-simple-interpreter-in-javascript)
- [https://en.wikipedia.org/wiki/Haskell_(programming_language)](https://en.wikipedia.org/wiki/Haskell_(programming_language))
- [http://www.stephendiehl.com/llvm/](http://www.stephendiehl.com/llvm/)
- [https://en.wikipedia.org/wiki/Parsec_(parser)](https://en.wikipedia.org/wiki/Parsec_(parser))
- A two parter on how lex and yacc work, respectivly
  1. [https://www.youtube.com/watch?v=54bo1qaHAfk](https://www.youtube.com/watch?v=54bo1qaHAfk)
  2. [https://www.youtube.com/watch?v=__-wUHG2rfM](https://www.youtube.com/watch?v=__-wUHG2rfM)
- [http://www.onboard.jetbrains.com/articles/04/10/lop/](http://www.onboard.jetbrains.com/articles/04/10/lop/) - From the maker of Intellij IDE and CEO of JetBrains! (concerning Language Oriented Programming)
- [http://ropas.snu.ac.kr/~kwang/520/pierce_book.pdf](http://ropas.snu.ac.kr/~kwang/520/pierce_book.pdf)
  - [/ipfs/QmcrFvSwxarj2r1HDMsBzcjt5SgtMcvCp8ByuFLk97WEov/pierce_book.pdf](https://ipfs.io/ipfs/QmcrFvSwxarj2r1HDMsBzcjt5SgtMcvCp8ByuFLk97WEov/pierce_book.pdf)
- [http://wiki.c2.com/?LispMacro](http://wiki.c2.com/?LispMacro)
- [https://www.youtube.com/watch?v=lC5UWG5N8oY](https://www.youtube.com/watch?v=lC5UWG5N8oY)
C++Now 2017: Ryan Newton "Haskell taketh away: limiting side effects for
parallel programming"
- [https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form)
- [https://www.youtube.com/watch?v=sTLkomM2P0o](https://www.youtube.com/watch?v=sTLkomM2P0o) "Introduction to building a programming language" A talk about implimenting a subset of PHP in JavaScript
- [https://cdsmith.wordpress.com/2011/01/09/an-old-article-i-wrote/](https://cdsmith.wordpress.com/2011/01/09/an-old-article-i-wrote/) A great article on distinctions about different properties of type systems.
- [https://www.youtube.com/watch?v=sTLkomM2P0o](https://www.youtube.com/watch?v=sTLkomM2P0o) A general overview of language design, getting perfectly in-depth.
