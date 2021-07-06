import sbt._

object Dependencies {
  object Versions {
    val scala212 = "2.12.14"
    val scala213 = "2.13.6"

    val trace4cats = "0.12.0-RC2"

    val cats = "2.6.1"
    val catsEffect = "3.1.1"
    val zioInterop = "3.1.1.0"

    val kindProjector = "0.13.0"
    val betterMonadicFor = "0.3.1"
  }

  lazy val trace4catsBase = "io.janstenpickle"     %% "trace4cats-base"      % Versions.trace4cats
  lazy val trace4catsBaseLaws = "io.janstenpickle" %% "trace4cats-base-laws" % Versions.trace4cats
  lazy val trace4catsInject = "io.janstenpickle"   %% "trace4cats-inject"    % Versions.trace4cats
  lazy val trace4catsTestkit = "io.janstenpickle"  %% "trace4cats-testkit"   % Versions.trace4cats

  lazy val zioInterop = "dev.zio" %% "zio-interop-cats" % Versions.zioInterop

  lazy val kindProjector = ("org.typelevel" % "kind-projector"     % Versions.kindProjector).cross(CrossVersion.full)
  lazy val betterMonadicFor = "com.olegpy" %% "better-monadic-for" % Versions.betterMonadicFor
}
