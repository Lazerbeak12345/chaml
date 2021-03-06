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

It is designed to make using functions super easy. Without syntax extensions,
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
- Any operator may be applied to any (or no) variable/value.
  - The operators in this list are in two groups. Operations on the value are
  `.` `=<` `=>`, and the function call, where it is `(` evoked, `,` separated,
  and `)` terminated list of argument values. Operations on the variable are `=`
  (for assignment) and `~` (for overloading of a function with a deduced [yet
  definitively given] argument length)

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
  - Types
    - Type inference
    - Type constructors
  - Overloading
    - Overloading functions
    - Overloading operators
    - Overloading syntax
  - Built-in tools
    - `.ext`
  - Libraries
    - Using Libraries
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

- [10/12] The Chameleon Programming Language Standard
  - [x] Table of Contents
  - [x] The compiler
  - [x] Variables
  - [x] Functions
  - [x] Operators
  - [x] Types
  - [x] Overloading
  - [x] Built in
  - [1/3] Libraries
    - [1/2] Using libraries
      - [ ] Some example libraries
        - [ ] IO
        - [ ] Vector
        - [ ] Regexp
    - [ ] Writing libraries
  - [ ] Syntax Extension
    - [ ] Using a syntax module
      - [ ] Common modules
    - [ ] Writing a syntax module
      - [ ] Tokenizer
      - [ ] Parse tree
      - [ ] AST
      - [ ] Reverse AST
      - [ ] Reverse Parse Tree
      - [ ] Reverse Tokenizer
  - [x] Resources

## The compiler

### Stages

1. Feed source code (call this file A) into tokenizer (outputs a stream). Go to
step 2.
2. Feed token stream into parser. If an import or syntax extension is found, go
to step 2.1, or else go to step 3.
   1. Grab the file that the import or syntax extension requires, and pass that
  into step 1. (call this file file B) If it's a syntax module, go to step 2.3,
  or else go to step 2.2.
   2. Get the entire Parse tree for file B, and insert it into the proper node
   on file A's parse tree. Go to step 3 (not to be confused with step 2.3).
   3. Run File B through the interpreter (not the JIT compiler). It may modify
  the token list, parse tree generator, Ast tree generator, interpreter, AST tree
  reverser, parse tree reverser, and or the token reverser.
  This modification _only_ applies to this local file A (not file a's parent, if
  this is multiple levels deep, and not file B). Continue to step 3.
3. Convert the parse tree into an AST tree. If file A is being interpreted run
the file, or else continue to step 4.
4. Convert the AST tree into a parse tree for the output language (or language
family) Continue to step 5.
5. Convert the parse tree into a token stream for the output language (or
language family). Continue to step 6.
6. Convert the token stream into the transpile code for the output language.
(Most often this is LLVM, but I've been thinking about output to JavaScript,
JVM, and a few others). If the compiler is in JIT mode, run the file.

### Runtime

As the standard output of the file is planned to be LLVM code, it will have
an environment similar to that one, but I am considering having JVM and/or
JavaScript as options for target languages.

The exception to this is when it's in interpreter mode, but this should function
about the same.

### Current state

There are multiple parallel attempts to reach a working end-goal.

- The chaml one is 75% done, but once I realized I made a few critical mistakes
(such as not actually knowing what a lexer is and what a parser is), I decided
to stop for awhile.
  - In the lib directory are libraries that I plan to have available. Many are
  highly experimental.
  - I plan on re-writing this once a different implementation starts working.
- java is the official first language implementation. The tokeniser is almost
complete, and can now export to XML.

### Plans

GET IT WORKING

I personally don't have any plan but this, and what is in the "Current state"
section.

To be more specific:

- Get the tokeniser written in java, and have the tokens exportable with a file
format.
- Make a parser in java that by default internally calls the tokenizer, never
needing to interact with the tokeniser output file, but still allow for that
to be an option. Also have exporting as a parse tree an available option.
- Make an AST tree executor in java. Just like the parser, it should be able to
either internally interact with the parser without going to a file format, or
read a file.
- Make a tokeniser just like the one in java, but written in CHAML.
- Make a parser just like the one in java, but written in CHAML, and instead of
interacting with the Java tokeniser, it can interact with the Chaml tokeniser.
- Make an interpreter just like the one in Java, but written in CHAML, and
instead of interacting with the Java Parser, it interacts with the CHAML parser
- Make a compiler to JavaScript in Chaml that functions under the same
requirements as the CHAML interpreter.
- Compile the entire CHAML suite to JavaScript, and stop using the Java
components completely.
- Remove the interpreter, and add JIT to the compiler.
- Allow for syntax extensions to modify or remove tokens.
- Allow for syntax extensions to modify or remove parse nodes, as well as
allowing addition of tokens.
- Allow for syntax extensions to modify or remove AST nodes, as well as allowing
addition of parse nodes.
- Modify the compiler to allow for exporting of the "native" AST.
- Split the compiler into two programs, the AST converter, and the "native" AST
compiler. Should both take AST files, and/or interact with the previous step.
- Modify the "native" AST compiler to allow for exporting of the "native" parse
tree.
- Split the "native" AST compiler into two programs, the reverse AST generator,
and the parse tree compiler.
- Modify the parse tree compiler to allow for exporting of the token stream.
- Split the parse tree compiler into the reverse Parser and the token compiler.
- Allow for syntax extensions to modify the output tokens.
- Allow for syntax extensions to modify the output parse tree (in any way).
- Allow for syntax extensions to modify the output AST tree (in any way).
- Celebrate!
- Add another output language?

## Variables

All variables are functions. When set with the `=` operator, it is overwritten
with the value on the right side. When set with the `~` (tilde) operator, the
caller is overloaded.

Naming conventions are identical to JavaScript; they may contain any number,
upper or lowercase letter, or these: `$_`. They cannot start with a number.

```text
newVar=3; // number
anotherVar=()=>3; //function returning a number
yetAnotherVar='c'; // this is just a char, but can be cast to and from a number
arrOfNumbers=[3,5,3,2,5,64,5,64,3];//cant change len, but you can replace the value
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
anyVariableHere=in {
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

### Overloading functions

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

Sometimes, variable names are unintentionally reused by developers. Here's an
example of operable, yet poorly written code.

```text
conflictingName=849234;
funcName=conflictingName=> conflictingName();

funcName(() => 21); //returns 21

anotherName=(conflictingName) {
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

### Type constructors

Type constructors are functions that return an object of said type.

The constructor bound to a value can be accessed with the `.constructor`
property. Many constructors will use `.ext` to "extend" the return of its
"parent" constructor. The return of `.ext` is the same as that it is called
upon, but when a value is missing, it is looked for in the value passed in to
`.ext`.

#### `Func`

This constructor is the default constructor for all values.

When a single argument is passed in, it returns that argument, with all of its
properties an methods stripped. It will, however, retain it's executor and its
overloads. For example, the return of `Func(Bool)` is just `Bool` but without
any sub-properties. In fact, aside from that crucial difference, it should
function identically.

#### `Bool`

The `Bool` constructor is the constructor for booleans, and is not built in (it
is, however globalized by default). It's actually one of the first constructors
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

- has mathematical methods on children- has mathematical methods on children
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

A constructor inheriting from `Int`. Like bool it is, by default, globalized.

- getCode returns the charcode
- other
  - `eq`

#### `Arr`

An array. I'm not really sure if this should be built in or not. (it wouldn't be
to hard to use booleans to define it, but would have some performance drawbacks)

- `.size` returns the size
- other
  - `eq`

#### `Str`

Strings are just arrays of chars. Shorthand for `['h','i']` is `"hi"`. Possibly
a default Syntax Extension? Inherits from `Arr`, of course.

## Overloading

The CHAML language (and one might even say _the CHAMLc programming environment_)
is designed to allow for as much overloading as possible.

Most languages have this concept, but just in case you either haven't heard of
it, or don't know what it's called (or perhaps even need a refresher):

In computer programming, overloading most often refers to the ability to
situationally extend, at a very "meta" programming level, what the default
"core" functionality of the language is. (An example could be an API programmer
allowing an object that is part of the API to be multiplied by a scalar, where
that object, by default, does not allow this. Matrices could be an application
of this example)

Examples of varieties include function overloading (most common), operator
overloading (less common), and _syntax_ overloading (least common).
CHAML supports the first and last of the three. The second, however, can be
implemented using syntax overloading.

### Overloading functions in CHAML

go to [Functions/Overloading functions]

### Overloading operators

See [Overloading syntax] for more info, but, although chamlc supplies no quick
way of operator overloading (as opposed to haskell), said overloads themselves,
or even a syntax that would allow for said quick overloads, can be implemented
using syntax overloading.

### Overloading syntax

go to [Syntax Extension] for more info.

## Built-in tools

There are several built-in tools that are included by default in CHAML. This
section will cover those not already listed in detail in the [Type Constructors]
section.

### `.ext`

`.ext` is a property of all objects/functions in CHAML where its return is a
"mix" of the object it was called upon, and the object passed into it,
properties of the latter replacing properties of the first.

## Libraries

Libraries are a convenient way of organizing one's code, and making use of
already written code.

### Using Libraries

To use a library, simply include that library in the PATH (NOTE: path system yet
to be determined), then do the following: (this assumes that you have a library
called "foo", that you wish to store in the variable `bar`)

```chaml
bar=#<[foo];
//Do stuff with bar
```

The library "foo" has no direct access to anything in this file, save for values
passed into bar and it's sub-properties. (note that it still can't modify those)

## Other features

- Object inheritance
  - The core components are provided (via some built-in prototyping), but can be
  improved with a library.
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
- a lib to generate a documentation markdown file using comments
  - could interact with the keywords lib idea and its children
  - could interact with the AST too

### Comments

```text
//Single-line C++ style.

/*
Along with multiline C style are both supported
*/

//* single line comments are evaluated first

And_thus="this line is still reached and evaluated";

/**
 * This type of comment is intended documentation for smooth integration with
 * your IDE, if it supports it. Must be valid markdown, but type indicators
 * may be accepted.
 *
 * No IDEs are known to support the language more then they would a .txt file.
 * Replace this with a labeled list of known IDEs in the future. If the count
 * is greater then 20, remove this list.
 */
```

Comments are preserved as long as possible, with few exceptions (ex:
interpreting mode) throughout the compiling process. This means it should be
possible to make comment dependant functionality

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
  type (decimal, hex, etc.) then resolve it, or else, throw:
  `Casting error: Cannot cast String(`insert string here`) to Int`
- To `Arr`
  - empty, array len of 0
  - `Bool` `"True"` if true, `"False"` if false.
  - `Char` The char is turned into a string with len of 1
  - `Int` empty array of the length of that number.
    - Optional 2nd arg: what to fill it with.
    - If not enough memory, error thrown: `Casting error: Cannot cast Int(`insert num here`) to Array`
  - `Array` duplicate of original array
- To `Func` all cases: return a function that returns the input, unless it
is empty, then throw `Casting error: Function constructor takes 1 argument`

Assume that missing cases signify that that function doesn't exist on that
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
  function is only called if all previous `if` or `.elif` booleans are false.
  In other words, it's only called when the state of the `if` is false. (see
  `else` below). Also returns the same thing as `if`.
  same thing(s) as `if`.
  - `else` takes a function, called only if all previous functions are false.
  Has no return.

If returns a function that resolves itself. Until this is run, the if is not
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
    file (extension not needed), or to a URL resource that the OS can handle.
    - NOTE: this file can also be a Redox style URL an IPFS style address, or a
      git address.
  - An optional callback
- Returns the value
- When module is called, the module has no access to anything else and is
  generally treated as its own separate, private program. Said module may
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
    - A char (or single char str) `'*'` (signifying all)

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
- A two-parter on how lex and yacc work, respectively
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
- [https://www.youtube.com/watch?v=sTLkomM2P0o](https://www.youtube.com/watch?v=sTLkomM2P0o) "Introduction to building a programming language" A talk about implementing a subset of PHP in JavaScript
- [https://cdsmith.wordpress.com/2011/01/09/an-old-article-i-wrote/](https://cdsmith.wordpress.com/2011/01/09/an-old-article-i-wrote/) A great article on distinctions about different properties of type systems.
- [https://www.youtube.com/watch?v=sTLkomM2P0o](https://www.youtube.com/watch?v=sTLkomM2P0o) A general overview of language design, getting perfectly in-depth.
