- names are a tool to communicate meaning to humans
- a human can only comprehend so many names at one time
- a context is a tool to only look at names currently relevant to a human
- a hierarchy is a kind of name that communicates context to humans
- a computer can not tell if you have good names, but it can tell you if you have a name for every context


## Organizing your code with good names
Using JVM terminology of "package", "class", and "method".
Different languages have different terms for the same concepts.

Names are a tool used to communicate meaning to humans.
Class names focus on the specific, while package names create categories and hierarchies of categories.
Since humans can only keep track of so many classes and their relationships at a time,
filtering names by package allows humans to focus on only a particular subset of names.
Every bit of code as an address, starting with the package name, continuing to the class name, and ending with the method name.
While dependency analysis can not tell you what good names are,
it can tell you if something is wrong with where your methods and classes are located within your hierarchy.
This analysis is useful at several levels of detail, which in order of importance are:
- Direct cycles
- Group cycles
- Ancestors depend on descendants
- Descendants depend on ancestors

### example: direct cycles
Say you have the following dependency structure:
```
digraph dependencies {
    HexFormatter -> StringUtil
    StringUtil -> HexFormatter
}
```
![direct cycle 1](/docs/direct-cycle-1.svg)

And suppose this is caused by having a stringBytesFormattedAsHex() method in StringUtil,
which calls byteToHex() in HexFormatter,
which calls interleaveSpaces() in StringUtil

The computer can't tell you which of stringBytesFormattedAsHex, byteToHex, or interleaveSpaces is in the wrong class,
but it can tell you that one of them is because you have a dependency cycle, and thus a violation of the stable dependencies principle.

It takes a human to realize the problem is that stringBytesFormattedAsHex belongs in HexFormatter, not StringUtil,
which results in the following, fixed, dependency structure:
```
digraph dependencies {
    HexFormatter -> StringUtil
}
```
![direct cycle 2](/docs/direct-cycle-2.svg)
