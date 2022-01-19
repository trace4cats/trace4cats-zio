package io.janstenpickle.trace4cats.inject.zio

import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.Local
import io.janstenpickle.trace4cats.inject.Trace
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.interop.catz._
import zio.random.Random
import zio.{Has, RIO, ZEnv}

object ZIOTraceInstanceSummonTest {
  type Effect[+A] = RIO[Clock with Blocking, A]

  type F[x] = SpannedRIO[Clock with Blocking with Has[Span[Effect]], x]
  implicitly[Trace[F]]

  type ZSpan = Has[Span[Effect]]
  type G[x] = RIO[ZEnv with ZSpan, x]
  implicit val rioLayeredLocalSpan: Local[G, Span[Effect]] =
    zioProvideSome[ZEnv, ZEnv with ZSpan, Throwable, Span[Effect]]

  implicitly[Trace[G]]

  type H[x] = RIO[Clock with Blocking with Has[Env], x]
  implicit val rioLocalSpan: Local[H, Span[RIO[Clock with Blocking, *]]] =
    Local[H, Env].focus(Env.span)
  implicitly[Trace[H]]

  type I[x] = RIO[Clock with Blocking with Console with Random with Has[Span[Effect]], x]
  implicitly[Trace[I]].span("Hello")(RIO.succeed("World"))
}
