# Naming

- names are a tool to communicate meaning to humans
- a human can only comprehend so many names at one time
- a context is a tool to only look at names currently relevant to a human
- a hierarchy is a collection of names organized by context

## Organizing your code with good names

Using JVM terminology of "package", "class", and "method". Different languages have different terms for the same
concepts.

Names are a tool used to communicate meaning to humans. Class names focus on the specific, while package names create
categories and hierarchies of categories. Since humans can only keep track of so many classes and their relationships at
a time, filtering names by package allows humans to focus on only a particular subset of names. Every bit of code is an
address, starting with the package name, continuing to the class name, and ending with the method name. While dependency
analysis can not tell you what good names are, it can tell you if something is wrong with where your methods and classes
are located within your hierarchy. This analysis is useful at several levels of detail, which in order of importance
are:

- Direct cycles
- Group cycles
- Ancestors depend on descendants
- Descendants depend on ancestors

### example: direct cycles

The stable dependencies principle is that software components should only depend on other software components that are more stable than they are.
Code that is likely to have a lot of other code depending on it should rarely break backwards compatibility (be stable).
Code that needs the flexibility to break backwards compatibility (not stable) should not have a lot of other code depending on it.
While this tool does not attempt to measure how stable any particular code is, it can detect violations of the stable dependency principle with absolute certainty and decent precision.
If you have a direct cycle, you know that there must be a violation of the stable dependency principle.
While this tool can not tell you which dependency is going in the wrong direction, it can narrow the culprits down to one of the dependencies participating in the direct cycle. 
Once the culprits are narrowed down like this, it is easy for a software engineer to determine where the problem specifically lies and how to fix it. 

Say you have the following dependency structure:

```
digraph dependencies {
    HexFormatter -> StringUtil
    StringUtil -> HexFormatter
}
```

![direct cycle 1](/docs/direct-cycle-1.svg)

And suppose this is caused by having a stringBytesFormattedAsHex() method in StringUtil, which calls byteToHex() in
HexFormatter, which calls interleaveSpaces() in StringUtil

The computer can't tell you which of stringBytesFormattedAsHex, byteToHex, or interleaveSpaces is in the wrong class,
but it can tell you that one of them is because you have a dependency cycle, and thus a violation of the stable
dependencies principle.

It takes a human to realize the problem is that stringBytesFormattedAsHex belongs in HexFormatter, not StringUtil, which
results in the following, fixed, dependency structure:

```
digraph dependencies {
    HexFormatter -> StringUtil
}
```

![direct cycle 2](/docs/direct-cycle-2.svg)

### example: group cycles

If you apply the same reasoning for avoiding cycles among classes to packages, you can make the code more comprehensible.
Software engineers rarely leverage the power of a well organized name hierarchy, but I do not believe this is a skill issue.
It is a human limitation issue, a human can only keep so many names in their head at one time.
The solution is tooling, such as this application.
Cycles detected at the package level provides an objective alert that something is wrong, without burdening the human with keeping track of every name.
Once attention is drawn to the proper place, the human can decide to break up packages or consolidate packages as appropriate.

Say you have the following dependency structure:

```
digraph dependencies {
    "login.LoginService" -> "authorization.PasswordVerifier"
    "authorization.PasswordVerifier" -> "login.OneWayHash"
}
```

![group cycle 1](/docs/group-cycle-1.svg)

While there are no direct cycles between classes, when you look at it from a higher level, you see a cycle between login
and authorization

```
digraph dependencies {
    login -> authorization
    authorization -> login
}
```

![group cycle 2](/docs/group-cycle-2.svg)

This tells you that at the group level, there must be at most one violation of the stable dependencies principle.

A human looking at which class is in which package, will quickly notice that OneWayHash is a generic class not specific
to login, yet it resides in the login package.

We can fix this without changing any logic, by moving OneWayHash into the more generic authorization package.

```
digraph dependencies {
    "login.LoginService" -> "authorization.PasswordVerifier"
    "authorization.PasswordVerifier" -> "authorization.OneWayHash"
}
```

![group cycle 3](/docs/group-cycle-3.svg)

Which at the group level, results in the simplified

```
digraph dependencies {
    login -> authorization
}
```

![group cycle 4](/docs/group-cycle-4.svg)

### example: ancestors depend on descendants

When a package gets too big, it makes sense to break it up into subpackages, giving each subpackage a more specific name to add more detailed context.
However, when you only move some code into subpackages, and leave some code in the parent package, you have given more qualified names to some code and not other code.
Within the context of the parent package, it is now harder to reason about the dependency structure, because you have left the context for the parent package unnamed.
The solution is to avoid this half measure, and give all code a more qualified context.
This forces you to think about good context names for all code in the package, and will lead you to more precise and well named categorization than otherwise.
Now the dependency structure is easier to reason about at the higher level, because each sub-package has a name.
Effectively this means you only have two types of packages.
Organizing packages, which contain no direct code, but rather organize other packages.
Code packages, which contain no sub-packages, only code.
This tool will detect an ancestor depending on a descendant, so that the software engineer can push the ancestor code down to a more qualified package.

Say you have the following dependency structure:

```
digraph dependencies {
    "app.login.Login"       -> "app.StringUtil"
    "app.register.Register" -> "app.StringUtil"
    "app.login.Login"       -> "app.PageUtil"
    "app.login.Login"       -> "app.Page"
    "app.register.Register" -> "app.Page"
    "app.home.Home"         -> "app.Page"
    "app.home.Home"         -> "app.PageUtil"
    "app.register.Register" -> "app.PageUtil"
    "app.Factory"           -> "app.login.Login"
    "app.Factory"           -> "app.register.Register"
    "app.Factory"           -> "app.home.Home"
}
```

![ancestry 1](/docs/ancestry-1.svg)

At the group level, we see that the parent group is collecting everything that does not end up in a child package,
resulting in a disorganized structure. What do we expect to find in the parent group? Just about anything could be
there.

```
digraph dependencies {
    "app.login"    -> "app"
    "app.register" -> "app"
    "app.home"     -> "app"
    "app"          -> "app.login"
    "app"          -> "app.register"
    "app"          -> "app.home"
}
```

![ancestry 2](/docs/ancestry-2.svg)

Since we have the overall context of "app", both depending on, and being depended on by its children, we can start by
distinguishing the two cases from each other. Since parents have shorter names than their children, it makes more sense
to put more general code in parents, and more specific code in children. So we start by looking at all the cases where
ancestors are depending on descendants, and push that code down into a more specific package. In this case, the
factories seem to be the culprit, so we create a separate factories package.

```
digraph dependencies {
    "app.login.Login"       -> "app.StringUtil"
    "app.register.Register" -> "app.StringUtil"
    "app.login.Login"       -> "app.PageUtil"
    "app.login.Login"       -> "app.Page"
    "app.register.Register" -> "app.Page"
    "app.home.Home"         -> "app.Page"
    "app.home.Home"         -> "app.PageUtil"
    "app.register.Register" -> "app.PageUtil"
    "app.factory.Factory"   -> "app.login.Login"
    "app.factory.Factory"   -> "app.register.Register"
    "app.factory.Factory"   -> "app.home.Home"
}
```

![ancestry 3](/docs/ancestry-3.svg)

Which at the group level, results in the simplified

```
digraph dependencies {
    "app.login"    -> "app"
    "app.register" -> "app"
    "app.home"     -> "app"
    "app.factory"  -> "app.login"
    "app.factory"  -> "app.register"
    "app.factory"  -> "app.home"
}
```

![ancestry 4](/docs/ancestry-4.svg)

### example: descendants depend on ancestors

The same rationale for detecting when an ancestor depends on a descendant, also applies to when a descendant depends on an ancestor.
This tool will detect a descendant depending on an ancestor, so that the software engineer can push the ancestor code down to a more qualified package.

At the end of the previous example, we are in pretty good shape. These examples are listed in order of importance.
However, we can still do better. The idea behind this last example is that categorization and dependency structure are
different kinds of things. Categories define a scope of interest, while dependency structure defines relationships
within a given scope. Having a parent category that all children depend on, lacks unique name within that category, and
is vulnerable to becoming a dumping ground for unrelated classes. Instead, if we pull everything from the parent group
into uniquely named sibling categories, we actually see new structure that was buried before, and can more clearly
separate unrelated classes by clumping related classes together more specifically. Continuing from the last example,
lets remove everything from the parent package.

```
digraph dependencies {
    "app.login.Login"       -> "lib.util.StringUtil"
    "app.register.Register" -> "lib.util.StringUtil"
    "app.login.Login"       -> "app.util.PageUtil"
    "app.login.Login"       -> "app.contract.Page"
    "app.register.Register" -> "app.contract.Page"
    "app.home.Home"         -> "app.contract.Page"
    "app.home.Home"         -> "app.util.PageUtil"
    "app.register.Register" -> "app.util.PageUtil"
    "app.factory.Factory"   -> "app.login.Login"
    "app.factory.Factory"   -> "app.register.Register"
    "app.factory.Factory"   -> "app.home.Home"
}
```

![ancestry 5](/docs/ancestry-5.svg)

Which at the group level, results in the simplified

```
digraph dependencies {
    "app.login"    -> "lib.util"
    "app.register" -> "lib.util"
    "app.login"    -> "app.util"
    "app.login"    -> "app.contract"
    "app.register" -> "app.contract"
    "app.home"     -> "app.contract"
    "app.home"     -> "app.util"
    "app.register" -> "app.util"
    "app.factory"  -> "app.login"
    "app.factory"  -> "app.register"
    "app.factory"  -> "app.home"
}
```

![ancestry 6](/docs/ancestry-6.svg)

Here, we have a more detailed understanding of what was previously in the parent package, and how those named packages
relate to the rest of the structure within the same context. We also discover that some of the classes didn't belong in
the parent context at all, they actually belong to a different context. We would not have noticed this unless we were
forced to think about the names of the sibling packages we were moving these classes to.

Now that we know lib.util is in a different context, we can remove it from our consideration.

```
digraph dependencies {
    "app.login.Login"       -> "app.util.PageUtil"
    "app.login.Login"       -> "app.contract.Page"
    "app.register.Register" -> "app.contract.Page"
    "app.home.Home"         -> "app.contract.Page"
    "app.home.Home"         -> "app.util.PageUtil"
    "app.register.Register" -> "app.util.PageUtil"
    "app.factory.Factory"   -> "app.login.Login"
    "app.factory.Factory"   -> "app.register.Register"
    "app.factory.Factory"   -> "app.home.Home"
}
```

And now that everything here is within the "app" context, we can drop the context from the names and end up with a
simplified yet more informative graph

```
digraph dependencies {
    "login"    -> "util"
    "login"    -> "contract"
    "register" -> "contract"
    "home"     -> "contract"
    "home"     -> "util"
    "register" -> "util"
    "factory"  -> "login"
    "factory"  -> "register"
    "factory"  -> "home"
}
```

![ancestry 8](/docs/ancestry-8.svg)
