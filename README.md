# (UNNAMED PROTO LANGUAGE)

This is a prototype of a programming language that implements a few features
I've been thinking about for some time.

## Proposed feature listing

### Core features

These features are core to the design, and are very unlikely to change very
much.

- No keywords, everything is a function.
- Literals of anything, with the exeptions of primitive literals, are syntax
modules.
  - These modules are included at the begginning of the file, much like imports
- Any operator may be applied to any variable, at all.
  - EX: while `7*10` returns `70`, a function that returns 7 multipled by a
function that returns 10 returns a function that returns the result of that
operator applied to each value, respectively.
- A sleek, secure way of having per-file syntax overloading.

### Other features

- Object inheritance
  - Planned to be much like TypeScript, but with functions at the top of the
tree.
  - To be implemented by importing a library
- Functions
  - Much like TypeScript `=>` arrow functions.
  - No native support for `function` keyword.
- Keywords, inline xml, obj syntax sugar all to be syntax modules.

## Breakdown

- No keywords
- Everything? is a function (including would-be keywords)

### Reserved functions

#### if

- Takes 2 args, a boolean and a function called when boolean is true.
- Returns an object with these methods:
  - *elif* Takes 2 args, a boolean and a function called when boolean is
ue, and no previous items in this chain are called.
  - *else* Takes 1 arg, a function called when no previous items in this
chain are called.

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

Same idea as *export*, but takes 1 arg, a function that takes the entire file
following the *useSyntax* call as it's only argument, returning the modified
code to be ran.

OR:

same idea as *export* but it can only take an object as its argument. See
*useSyntax* for more info. The object is as follows:

- *regexp* an instance of *Regexp*
- ??

#### Regexp
