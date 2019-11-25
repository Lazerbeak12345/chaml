# Contributing guide for CHAML

Thanks for thinking about contributing!

## Code of Conduct

See `CODE_OF_CONDUCT.md`.

## Things you should know

This repository is home to three things:

- The specifications for the CHAML programming language/enviroment
  - This is found in `README.md`
- An implimentation of the CHAML programmming language/enviroment
  - At the time of writing, this is still a work-in-progress.
- The library files that one would consider "core" to the language.
  - At the time of writing, this is still a work-in-progress.

## How to contribute

> TODO: Add note about issue guidelines

### Bug reporting

Report bugs on github.

### Enhancements/Pull requests

> Don't make any edits directly on the master branch (especially not your own
> master branch).
> 
> Doing so can mess up my master branch, and requires 30 minutes for me to fix,
> whereas it  should only take you a few seconds to make a fork of master, and
> change that to your working branch.

## Code style

There is no formal code style on most files excluding MarkDown files.
Be willing to accept suggestions on modifying the style. When it comes to
style, all I really care about is readability.

### MarkDown Code Style

With the exeption of links, keep all lines less than 80 characters long.

When it comes to links, if they would push the character count over 80, keep
them on their own line.

## Git style

This shares the same rules as [atoms contributing guide]:

> * Use the present tense ("Add feature" not "Added feature")
> * Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
> * Limit the first line to 72 characters or less
> * Reference issues and pull requests liberally after the first line
> * When only changing documentation, include `[ci skip]` in the commit title
> * Consider starting the commit message with an applicable emoji:
>     * :art: `:art:` when improving the format/structure of the code
>     * :racehorse: `:racehorse:` when improving performance
>     * :non-potable_water: `:non-potable_water:` when plugging memory leaks
>     * :memo: `:memo:` when writing docs
>     * :penguin: `:penguin:` when fixing something on Linux
>     * :apple: `:apple:` when fixing something on macOS
>     * :checkered_flag: `:checkered_flag:` when fixing something on Windows
>     * :bug: `:bug:` when fixing a bug
>     * :fire: `:fire:` when removing code or files
>     * :green_heart: `:green_heart:` when fixing the CI build
>     * :white_check_mark: `:white_check_mark:` when adding tests
>     * :lock: `:lock:` when dealing with security
>     * :arrow_up: `:arrow_up:` when upgrading dependencies
>     * :arrow_down: `:arrow_down:` when downgrading dependencies
>     * :shirt: `:shirt:` when removing linter warnings

> TODO: Add section about issue and PR tags

[atoms contributing guide]: https://github.com/atom/atom/blob/master/CONTRIBUTING.md
