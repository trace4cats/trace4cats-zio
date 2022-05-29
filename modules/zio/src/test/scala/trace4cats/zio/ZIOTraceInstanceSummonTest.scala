package trace4cats.zio

import trace4cats.Span
import trace4cats.context.Local
import trace4cats.Trace
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.interop.catz._
import zio.random.Random
import zio.{Has, RIO, Task, ZEnv}

object ZIOTraceInstanceSummonTest {
  type Effect[+A] = RIO[Clock with Blocking, A]

  type F[x] = SpannedEnvRIO[Clock with Blocking with Has[Span[Effect]], x]
  implicitly[Trace[F]]

  type G[x] = RIO[ZEnv with Has[Span[Task]], x]
  implicit val rioLayeredLocalSpan: Local[G, Span[Task]] =
    zioProvideSome[ZEnv, ZEnv with Has[Span[Task]], Throwable, Span[Task]]

  implicitly[Trace[G]]

  // Lens behavior relies on the non ZLayer instances
  type H[x] = RIO[Env, x]
  implicit val rioLocalSpan: Local[H, Span[Task]] =
    Local[H, Env].focus(Env.span)
  implicitly[Trace[H]]

  type I[x] = RIO[Clock with Blocking with Console with Random with Has[Span[Effect]], x]
  implicitly[Trace[I]].span("Hello")(RIO.succeed("World"))
}
