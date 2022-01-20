package io.janstenpickle.trace4cats.inject.zio

import io.janstenpickle.trace4cats.Span
import zio.blocking.Blocking
import zio.clock.Clock
import zio.{Has, RIO}

trait ZIOTraceInstances {
  implicit val spannedRIOTrace: SpannedRIOTracer = new SpannedRIOTracer
}

trait ZIOHasTraceInstances {
  implicit def spannedEnvRIOTrace[
    R <: Clock with Blocking with Has[Span[RIO[Clock with Blocking, *]]]
  ]: SpannedEnvRIOTracer[R] = new SpannedEnvRIOTracer[R]
}
