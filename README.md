# (UNNAMED PROTO LANGUAGE)

This is a prototype of a programming language that implements a few features
I've been thinking about for some time.

## Proposed feature listing

### Core features

These features are core to the design, and are very unlikely to change very
much.

- No keywords, everything is a function. - note, I am still not sure about
  whether primitives should be functions.
- Syntax modules included at the begginning of the file, much like imports
- Any operator may be applied to any variable, at all. (incuding `()`)
  - EX: while `7*10` returns `70`, a function that returns 7 multipled by a
function that returns 10 returns a function that returns the result of that
operator applied to each value, respectively.
- A sleek, secure way of having per-file syntax overloading.

### Other features

- Object inheritance
  - Planned to be much like TypeScript, but with functions at the top of the
tree.
  - To be implemented by importing a library. This ipmlementation may even have
  a syntax module availiable.
- Keywords, inline xml, obj syntax sugar all to be syntax modules.

### Syntax

#### Comments

```text
//Single-line C++ style

/*
Along with muli line C style are both supported
*/
```

#### Variables

```text
newVar=3; // local only variable
```

#### Operators

These are the closest to operators I am willing to steep to.

- Add with `+`
- Subtract with `-`
- Multiply with `*`
- Divide with `/`
- Iterate with `++` (prefix v postfix act the same as in C)

Unless noted, they don't apply to anything.

#### Functions

##### Defining

supports 6 ways of being defined.

###### No args

```text
anyVariableHere={
  //do something upon invoction
}
```

###### No args One line

```text
anyVariableHere=()=> 72;
```

###### One Arg

```text
anyVariableHere= in => {
  =< in*2;
};
```

###### One arg One Line

```text
anyVariableHere= in => in*2;//single line treated like a code block
```

###### Multi-arg

```test
anyVariableHere= (a,FEW,dif_ferent,var5,h$r3) => {
  //The => is optional when inbetween an endparen and an opening curly
  =< a+FEW+dif_frent+var5+h$er3
}
```

###### Multi-arg One line

```test
anyVariableHere= (a,FEW,dif_ferent, var5,h$r3) => a+FEW+dif_frent+var5+h$er3;
```

##### scoping

```text
//can be modified above theFunc
//can be modified within theFunc
//can be modified below theFunc
a=1947;
theFunc=(e)=>{
  //can't be modified above theFunc
  //can be modified within theFunc
  //can't be modified below theFunc
  b=28142;
  //can't be modified above theFunc
  //can be modified within theFunc
  //can be modified below theFunc
  .c=4234;

  e++;
}
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

//anotherVar now equals 100
```

###### When var names collide

Sometimes, variable names are unintentially reused. Here's an example of
functional, yet poorly written code.

```text
conflicingName=849234;
funcName=conflictingName=> conflicingName();

funcName(() => 21); //returns 21

anotherName=(conflicingName) => {
  =<++conflictingName;
}

k=3;
anotherName(k)//returns 3
//k is now 4

//conflictingName is still 849234
```

##### Returning

###### From iside a one-liner

```text
returnsNumber1=()=> 1;
returnsArgTimesTwo=arg=>arg*2;
returnsTheSumOfArgs=(a,b)=>a+b;
```

###### From a block function

```text
returnsNumber1={
  =<1;
};
returnsArgTimesTwo=arg=>{
  =<arg*2;
}
returnsTheSumOfArgs=(a,b) => {
  =<a+b;
}
```

##### Calling convention

```text
a=()=>1;
b=a=>1
c=(a,b)=>
```

##### Stupid

```text
functionsAreStoredWithinVariables={
  localVar=100;
  this.varAccessibleFromOutside=7;
  =<100;//optional, but this is how you return. Elsewhise, no return is made.
}

/*
functionsAreStoredWithinVariables.varAccessibleFromOutside

can be read &/or modified from outside forces

localVar can't
*/

functionsAreStoredWithinVariables(); // returns 100

anotherButWithOneArgAndNoBlock=A=>A*2;

anotherButWithOneArgAndNoBlock(9); //returns 18

multAbyB=(A,B)=> A*B;

multAandBbyPI=(A,B) => {
  PI=3.1415;
  =< multAbyB(A,B)*PI;
}
```

### Reserved functions

#### if

- Takes 2 args, a boolean and a function called when boolean is true.
- An optional third arg is another function, called if boolean is false.

If returns a function that resolves itself. Untill this is run, the if is not
evaluated.

```text
if(true,{
  //code if true
})()
```

#### import

- Takes 2 args
  - A string that must refer to either a module name, a Unix-style url to a
file (extention not needed), or to a URL resorce that the OS can handle.
- Returns a promise (think JavaScript promise) - TODO
- When module is called, the module has no access to anything else and is
gennerally treated as its own seperate, private program. Said module may
also call import. See *export* for more info.

#### export

- Takes 1 arg, the content to be retreived by *import*.
- File is cashed until compileing (or interpreting) is complete.

#### useSyntax

What I'm going for here is a clean way where the file "imports" a library, and
that library then allows for alien syntax below.

- Takes 1 arg, a string with the same requirements as arg 0 of *import*
- Use RegExp?

EVAL?, TODO (below)

- Takes 1 arg, a string with the same requirements as arg 0 of *import*
- Calls the function passed into *declareSyntax*, passing in the entirety of 
the source code. The return of the function (a string) will then be treated as
the new code, and the next call to this particular *useSyntax* within this file
will be ignored, thus preventing recursion. (Note that this means that all code
up till this point is called again. This is why it is bad practice, but it is
not unnallowed, for the function to be called outside of the logical "header".
This may, unfortunately cause minor issues for compilers, and they are not
obligated to support more then top-level calls to this function)
- result must be code that either other *useSyntax* calls (including ones
injected with a *useSyntax* call) in lower lines of code will be able to
transpile, not to mention the compiler/interpreter iself must be able to
understand.

#### declareSyntax

##### declareSyntax Proposal 1

Same idea as *export*, but takes 1 arg, a function that takes the entire file
following the *useSyntax* call as it's only argument, returning the modified
code to be ran.

##### declareSyntax Proposal 2

same idea as *export* but it can only take an object as its argument. See
*useSyntax* for more info. The object is as follows (in psudo jsonc):

```text
"NameOfSyntaxConversion":[/regexpThin/g,(entiretyOfTheLine_s_thatMatched) {
  //return a string to replace it
}]
```

#### Regexp

?? only if needed by declareSyntax proposal 2

A new regexp element.
