name := "example-project"
version := "0.1"
scalaVersion := "2.12.8"

scalacOptions += "-Ypartial-unification"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

resolvers += Resolver.sonatypeRepo("releases")