# Contribution Guidelines

## Source code ownership

Contribution takes place using pull requests. Prior to a pull request, a change should be discussed on the issue tracker.

By contributing source code to this project, the contributor does agree to publish his/her contribution under the [license terms](./LICENSE) of this project.

## Design decisions

Vavr is influenced by Scala, we align to the [Scala language](https://www.scala-lang.org).

Java is missing essential features known from Scala, most notably higher-kinded types and lazy evaluation. Instead of trying to solve these issues by providing incomplete workarounds, we leave features away that depend on such language features.

Java is not the right language for purely functional programming. For now, Vavr will not provide algebraic abstractions known from [Scalaz](https://github.com/scalaz/scalaz) and [Cats](https://typelevel.org/cats/).

## Development

Developing Vavr requires forking and cloning the Github project.

* Run the tests with `./gradlew check`. We strive for 99.99% code coverage (using black box tests).

Local reports:

| vavr-control |
| --- |
| [tests](./vavr-control/build/reports/tests/test/index.html), [coverage](./vavr-control/build/reports/jacoco/test/html/index.html) |
