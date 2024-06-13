import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.16"
    val scala213 = "2.13.8"
    val scala3 = "3.1.3"

    val trace4cats = "0.14.0"

    val catsEffect = "3.3.14"
    val zioInteropCats = "3.3.0"

    val kindProjector = "0.13.2"
    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsContextUtils = "io.janstenpickle"     %% "trace4cats-context-utils"      % Versions.trace4cats
  lazy val trace4catsContextUtilsLaws = "io.janstenpickle" %% "trace4cats-context-utils-laws" % Versions.trace4cats
  lazy val trace4catsCore = "io.janstenpickle"             %% "trace4cats-core"               % Versions.trace4cats
  lazy val trace4catsTestkit = "io.janstenpickle"          %% "trace4cats-testkit"            % Versions.trace4cats

  lazy val catsEffect = "org.typelevel" %% "cats-effect"      % Versions.catsEffect
  lazy val zioInteropCats = "dev.zio"   %% "zio-interop-cats" % Versions.zioInteropCats

  lazy val kindProjector = ("org.typelevel" % "kind-projector"     % Versions.kindProjector).cross(CrossVersion.full)
  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
}
