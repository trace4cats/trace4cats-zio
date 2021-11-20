lazy val commonSettings = Seq(
  Compile / compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, _)) =>
        Seq(compilerPlugin(Dependencies.kindProjector), compilerPlugin(Dependencies.betterMonadicFor))
      case _ => Seq.empty
    }
  },
  scalacOptions += "-Wconf:any:wv",
  Test / fork := true,
  resolvers += Resolver.sonatypeRepo("releases"),
)

lazy val noPublishSettings =
  commonSettings ++ Seq(publish := {}, publishArtifact := false, publishTo := None, publish / skip := true)

lazy val publishSettings = commonSettings ++ Seq(
  publishMavenStyle := true,
  pomIncludeRepository := { _ =>
    false
  },
  Test / publishArtifact := false
)

lazy val root = (project in file("."))
  .settings(noPublishSettings)
  .settings(name := "Trace4Cats ZIO")
  .aggregate(`base-zio`, `inject-zio`)

lazy val `base-zio` =
  (project in file("modules/base-zio"))
    .settings(publishSettings)
    .settings(
      name := "trace4cats-base-zio",
      libraryDependencies ++= Seq(Dependencies.zioInterop, Dependencies.trace4catsBase),
      libraryDependencies ++= Seq(Dependencies.trace4catsBaseLaws, Dependencies.trace4catsTestkit).map(_ % Test)
    )

lazy val `inject-zio` = (project in file("modules/inject-zio"))
  .settings(publishSettings)
  .settings(name := "trace4cats-inject-zio", libraryDependencies ++= Seq(Dependencies.trace4catsInject))
  .dependsOn(`base-zio`)
