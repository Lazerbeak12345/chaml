# The Chameleon Programming Language Standard

This is a prototype of a programming language that implements a few features
I've been thinking about for some time. It is designed mostly for thinking
about.

<!--Look at https://vim.fandom.com/wiki/Creating_your_own_syntax_files later-->
<!--Look at https://stackoverflow.com/questions/38148857/customizing-syntax-highlighting-in-visual-studio-code-->

Here's a code sample for those interested:

```chaml
/**
 * An implimentation of else and elif using only if
 */
self.ret((b,f) {
  if(b,f);
  self.ret({
    state=Bool(b);
    obj=self;
    self.elif=(b,f) {
      state=state.or(b);
      if(not(state).and(b),f);
      self.ret(obj);
    };
    self.else=(b,f) {
      if(not(state).and(b),f);
    };
  });
});
```

## Table of Contents

- The Chameleon Programming Language Standard
  - Table of Contents
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
  - `self`
    - `self.ret`
  - Operators
  - Core features
  - Other features
  - Syntax
    - Comments
    - Typecasting
  - Reserved functions
    - `add`
    - `minus`
    - `times`
    - `div`
    - `gt`
    - `lt`
    - `gte`
    - `lte`
    - `and`
    - `or`
    - `xor`
    - `eq`
    - `not`
    - self.ret
    - self.throw
    - while
    - if
    - self.import
    - self.useSyntax
    - self.declareSyntax
    - Regexp
  - Interpretation/Compilation stages
  - Resources

### The future TOC

- [6/11] The Chameleon Programming Language Standard
  - [ ] The compiler
    - [ ] Stages
    - [ ] Runtime
    - [ ] Current state
    - [ ] Plans
  - [x] Variables
  - [x] Functions
  - [x] `self`
  - [x] Operators
  - [ ] Types
    - [ ] Type inference
    - [ ] Type functions
      - `Func`
      - `Bool`
      - `Int`
      - `Char`
      - `Arr`
      - `Str`
    - [ ] Casting
  - [ ] Libraries
    - [ ] Using libraries
      - [ ] `self.import`
      - [ ] Some example libraries
        - [ ] IO
        - [ ] Vector
        - [ ] Regexp
    - [ ] Writing libraries
  - [ ] Overloading
    - [ ] Overloading functions
    - [ ] Overloading types
    - [ ] Overloading operators
    - [ ] Overloading syntax - (points to dedicated section)
  - [ ] Syntax Extention
    - [ ] Using a syntax module
      - [ ] Common modules
    - [ ] Writing a syntax module
  - [x] Resources

## Variables

Variables may be set to the following (`Generator` is the constructor):

- `Function`
- `Number`
  - Supports all syntaxes java supports for numbers.
    - decimal `34`
    - bin `0b11`
    - hex `0xf4`
    - octal `012`
    - the underscore may be used for readability anywhere but the first or last
    characters.
  - Assumes to be a 64 bit number (a C++ double). Signing the value when using
  more then 63 bits will throw
  `Number overflow error: Tried to change sign of huge number`.
- `Char` (C-style)
  - Stored in memory nearly exactly the same as a number. (type signage is the
  only difference)
- `Array` fixed-length list of items
- `Bool` (C-style)

Strings are just arrays of chars. Shorthand for `['h','i']` is `"hi"`.

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

Uses call/pass by value. (Allow for pointers? Look into lazy reaction to pure function detection. Also look into deforestation.)

### Defining Functions

Supports 6 ways of declaring functions, listed below

#### No args

```text
anyVariableHere={
  //do something upon invoction
};
```

#### No args One line

```text
anyVariableHere=()=> 72;// return 72
```

#### One Arg

```text
anyVariableHere= in => {
  self.ret(in*2); // return the arg times two
};
```

#### One arg One Line

```text
anyVariableHere= in => in*2;//a single line is treated like a code block
```

#### Multi-arg

```test
anyVariableHere= (a,FEW,dif_ferent,var5,h$r3) => {
  //The => is optional when inbetween an endparen and an opening curly
  self.ret(a+FEW+dif_frent+var5+h$er3);
};
```

#### Multi-arg One line

```test
anyVariableHere= (a,FEW,dif_ferent, var5,h$r3) => a+FEW+dif_frent+var5+h$er3;
```

### Calling functions

```chaml
a=()=>1;
b=a=>a+1;
c=(a,b)=>a+b;

//Call a
a();        // returns 1
//call b, passing a value. (if a var, it's duplicated)
b(3);       // returns 4
c(3,7);     // returns 10

//Compose b on c (evaluates to `b(c(3,2))`)
b@c(20,3);  // returns 24
```

#### scoping

```text
//can be modified above theFunc
//can be modified within theFunc
//can be modified below theFunc
a=1947;
theFunc=(
  //can't be modified above theFunc
  //can be modified within theFunc
  //can be modified below theFunc (after a `anotherVar` is declared)
  e)=>{
  //can't be modified above theFunc
  //can be modified within theFunc
  //can't be modified below theFunc
  b=28142;
  //can't be modified above theFunc
  //can be modified within theFunc
  //can be modified below theFunc
  self.c=4234;

  e++;
};
//can't be modified above theFunc
//can't be modified within theFunc
//can be modified below theFunc
d=324;

/* here's how to modify this var too.
 * note that it isn't initialized till there is a value set to it.
 * If you read this variable on the first line of `theFunc` it would read `7`*/
theFunc.c=7;

//can't be modified above theFunc
//can be modified within theFunc
//can be modified below theFunc (but only after this call)
anotherVar=99;

theFunc(anotherVar);
//anotherVar does not change
```

Aditionally, functions can be defined within other functions. As they are stored
within variables, they follow the same scoping when it comes to running the
function. This also applies to variables within functions within functions:

```text
//cannot read or execute outerFunc here
//cannot read or execute innerFunc here
//cannot read a here
outerFunc={
  //can read &/or execute outerFunc here
  //cannot read or execute innerFunc here
  //cannot read a here
  innerFunc={
    //can read &/or execute innerFunc here
    //can read &/or execute outerFunc here
    //cannot read a here
    a=true;
    //can read a here
  };
  //can read &/or execute innerFunc here
  //cannot read a here
};
//can read &/or execute outerFunc here
//cannot read or execute innerFunc here
//cannot read a here
```

##### When var names collide

Sometimes, variable names are unintentially reused by devs. Here's an example of
functional, yet poorly written code.

```text
conflicingName=849234;
funcName=conflictingName=> conflicingName();

funcName(() => 21); //returns 21

anotherName=(conflicingName) => {
  self.ret(conflictingName*10);
};

k=3;
anotherName(k)//returns 30

//conflictingName is still 849234
```

### Returning

#### From inside a one-liner

```text
returnsNumber1=()=>1;
returnsArgTimesTwo=arg=>arg*2;
returnsTheSumOfArgs=(a,b)=>a+b;
```

#### From a block function

```chaml
returnsNumber1={
  self.ret(1);
};
returnsArgTimesTwo=arg=>{
  self.ret(arg*2);
};
returnsTheSumOfArgs=(a,b) => {
  self.ret(a+b);
};
```

Alternatively you could use

```chaml
returnsNumber1={
  =<1;
};
returnsArgTimesTwo=arg=>{
  =<arg*2;
};
returnsTheSumOfArgs=(a,b) => {
  =<a+b;
};
```

However, this latter approach isn't going to be implemented very soon.

## `self`

`self` is a variable that refers to the nearest function. Calling it calls the
function it resides in, setting properties to it sets properties to the function
it resides in and setting it overrides the property it resides in.

If there is a `self` conflict, just set a varuible to `self`.

### `self.ret`

A function taking one argument, the value to return from the function that it is
called in. (this could be a function that is passed as the 2nd arg to `if`.)

## Operators

<!--Note to self: look at https://www.tutorialspoint.com/java/java_basic_operators.htm-->
<!--NOTE to self: look at https://en.wikipedia.org/wiki/Graph_reduction-->

- SET `=` the only real operator
- Left byteshift `<<` TODO do i even want or need this?
- Right byteshift `>>` TODO see above todo

## Types

## Core features

These features are core to the design, and are very unlikely to change very
much.

- No keywords
  - Also try to avoid things like them. (Operators and Types are to be used
  sparingly)
- A sleek, secure way of having per-file syntax overloading.
  - Syntax modules included at the beginning of the file, much like imports
- Functions are call by value
- Any operator may be applied to any variable, at all.
  - EX: while `7.times(10)` returns `70`, a function that returns 7 multipled by a
  function that returns 10 returns a function that returns the result of that
  operator applied to each value, respectively.

```chaml
retSeven=()=>7;
retTen=()=>10;
resultGetter=retSeven.times(retTen);
endResult=resultGetter();
```

### CORE PROPOSAL 2

Add "everything is a function - including natives"

### CORE PROPOSAL 3

Add

```md
- Arguments to a function are to be evaluated lazily, and booleans should
  short-circut, but the rest of the program is eager.
```

Instead of

```md
- Don't have some stupid feature such as lazy eval forced; allow for devs to
  wrap everything in a function if they wanted to.
```

(Or at least make the wording less harsh)

### core proposal "need"

add `(needed)` to `- Functions are call by value`

<!--note to self, see https://en.wikipedia.org/wiki/Evaluation_strategy#Call_by_value to impliment it-->

## Other features

- Object inheritance
  - The core components are provided, but can be improoved with a library.
- Keywords, inline xml, obj syntax sugar all to be syntax modules.

### Other features proposal "Operators"

Make most operators a syntax module.

## Syntax

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

### Typecasting

Variables are never cast automatically.

To cast use the constructor like so:
 `joinOfStringAndNum=+("The number is ",37.to.String()))`

One can overload casting by doing something like this:

```text
//Overload casting of Array to Boolean in all cases
Array.to.Boolean={
  //Return what you want the value to be. `self` is the current value.
};
//Overload instance only
myNumber=4234
myNumber.to.Char={
  //Same as before, return what you want the value to be,
};
```

- To `Boolean`
  - `Boolean` duplicate of original boolean
  - `Number` true only if equal to `0` or `-0`
  - `Array` If array is len of 1 and it is a bool, then that bool, else error
  thrown: `Casting error: Cannot cast Array to Boolean`
- To `Char`
  - `Bool` `'t'` if true, `'f'` if false
  - `Char` duplicate of original char
  - `Int` local charcode conversion to char. If invalid, throw sensible error
  like `Casting error: Cannot cast Number(`insert num here`) to Char` as it is
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
  error thrown: `Casting error: Cannot cast Array to Number`
  - `Str` If the string is what would pass for a valid in-line literal of any
  type (decimal, hex, etc.) then resolve it, elsewhise, throw:
  `Casting error: Cannot cast String(`insert string here`) to Number`
- To `Arr`
  - empty, array len of 0
  - `Bool` `"True"` if true, `"False"` if false.
  - `Char` The char is turned into a string with len of 1
  - `Int` empty array of the length of that number.
    - Optional 2nd arg: what to fill it with.
    - If not enough memory, error thrown: `Casting error: Cannot cast Number(`insert num here`) to Array`
  - `Array` duplicate of origianl array
- To `Func` all cases: return a function that returns the input, unless it
is empty, then throw `Casting error: Function constructor takes 1 argument`

Assume that missing cases signify that that function doesn't exsist on that
object.

#### Typecasting proposal - monads

Look into adding monads, such as

- just
- maybe

## Reserved functions

### `add`

Adds two objects. If it's two numbers, it just adds them.

Calls the first function's `add` sub-function (and therefore is overloadable on a
per-case basis). If not present, it returns a function that returns the sum of
calling each.

Takes two args, the items to add; returns one arg, the added items.

### `minus`

Subtracts two objects. If it's two numbers, it just subtracts them.

Calls the first function's `minus` sub-function (and therefore is overloadable
on a per-case basis). If not present, it returns a function that returns the
subtraction of calling each.

Takes two args, the items to subtract; returns one arg, the subtracted items.

### `times`

Multiplies two objects. If it's two numbers, it just multiplies them.

Calls the first function's `times` sub-function (and therefore is overloadable
on a per-case basis). If not present, it returns a function that returns the
multiple of calling each.

Takes two args, the items to subtract; returns one arg, the subtracted items.

### `div`

Divides two objects. If it's two numbers, it just divides them.

Calls the first function's `div` sub-function (and therefore is overloadable on a
per-case basis). If not present, it returns a function that returns the division
of calling each.

Takes two args, the items to subtract; returns one arg, the subtracted items.

### `gt`

Checks to see if one object is greater than the other. Returns a bool.

Calls the first function's `gt` sub-function (and therefore is overloadable on a
per-case basis). If not present, it returns a function that returns the "gt" of
the return of each when called.

### `lt`

Checks to see if one object is less than the other. Returns a bool.

Calls the first function's `lt` sub-function (and therefore is overloadable on a
per-case basis). If not present, it returns a function that returns the "lt" of
the return of each when called.

### `gte`

Checks to see if one object is greater than or equal to the other. Returns a
bool.

Calls the first function's `gte` sub-function (and therefore is overloadable on
a per-case basis). If not present, it returns a function that returns the "gte"
of the return of each when called.

(This sub-function often just equals the return of the `eq` sub-function applied
to the `gt` sub-function)

### `lte`

Checks to see if one object is less than or equal to the other. Returns a bool.

Calls the first function's `lte` sub-function (and therefore is overloadable on
a per-case basis). If not present, it returns a function that returns the "lte"
of the return of each when called.

(This sub-function often just equals the return of the `eq` sub-function applied
to the `lt` sub-function)

### `and`

A subfunction of bools (and a few other things).

- If the `self` bool value was false, it returns false.
- If the `self` bool value was true, and the new bool is true, return true.
- Otherwise return false.

### `or`

A subfunction of bools (and a few other things).

- If the `self` bool value was true, it returns true.
- If the `self` bool value was false, and/or the new bool is true, return true.
- Otherwise return false.

### 'xor'

A subfunction of bools (and a few other things).

- If the `self` bool value was true and the new bool is false, it returns true.
- If the `self` bool value was false and the new bool is true, it returns true.
- Otherwise return false.

### `eq`

Determines equality between two objects.

Calls the first function's `eq` sub-function (and therefore is overloadable on a
per-case basis). If not present, it returns a function that returns the eqality
function applied to the return of calling each.

Takes two args, the items to subtract; returns one arg, the subtracted items.

### `not`

Returns false if passed in true, returns true if passed in false.

### self.ret

How one returns from a function. If at the topmost level of the source code, It
exports that instead. If this is the topmost file, then the return value is
called with an array of an array of chars (the arguments)

### self.throw

### while

- Takes 2 args,
  - A function returning a boolean (as it is called multiple times)
  - a function called after each time the boolean is found to be true.
    - Also must return a boolean or nothing. If found to be false, but not
    undefined or null, loop stops.

This should allow for while, while-do, do-while and for behavior, as well as
other awesome combos (like a while-do-while, do-while-do, or do-while-do-while).

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

### self.import

- Takes 2 args
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
is what becomes the return of [[self.import]].

### self.useSyntax

- Takes 2 args
  - A string with the same requirements as arg 0 of *import*
  - An optional variable that is one of the following:
    - An array of strings with the below description
    - A string that exactly matches the name of the syntax conversion defined by
    `declareSyntax`.
    - A char (or single char str) `'*'` (sigifying all)

### self.declareSyntax

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
- [https://thecodeboss.dev/2016/02/programming-concepts-type-introspection-and-reflection/](https://thecodeboss.dev/2016/02/programming-concepts-type-introspection-and-reflection/)
- [https://www.info.ucl.ac.be/~pvr/VanRoyChapter.pdf](https://www.info.ucl.ac.be/~pvr/VanRoyChapter.pdf)
- [https://thecodeboss.dev/2015/11/programming-concepts-static-vs-dynamic-type-checking/](https://thecodeboss.dev/2015/11/programming-concepts-static-vs-dynamic-type-checking/)
- [https://www.tutorialspoint.com/compiler_design/compiler_design_regular_expressions.htm](https://www.tutorialspoint.com/compiler_design/compiler_design_regular_expressions.htm)
- [https://en.wikipedia.org/wiki/Extensible_programming](https://en.wikipedia.org/wiki/Extensible_programming)
- [http://wiki.c2.com/?ExtensibleProgrammingLanguage](http://wiki.c2.com/?ExtensibleProgrammingLanguage)
- [http://wiki.c2.com/?MetaProgramming](http://wiki.c2.com/?MetaProgramming)
- [https://en.wikipedia.org/wiki/Lambda_calculus](https://en.wikipedia.org/wiki/Lambda_calculus)s
- [https://www.epaperpress.com/lexandyacc/index.html](https://www.epaperpress.com/lexandyacc/index.html) This is actually more than one link, and this is the TOC for them.
- [http://www.paulgraham.com/langdes.html](http://www.paulgraham.com/langdes.html)
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
