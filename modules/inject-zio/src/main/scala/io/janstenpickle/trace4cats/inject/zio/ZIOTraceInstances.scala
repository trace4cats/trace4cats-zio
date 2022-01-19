package io.janstenpickle.trace4cats.inject.zio

import io.janstenpickle.trace4cats.Span
import zio.blocking.Blocking
import zio.clock.Clock
import zio.{Has, RIO}

trait ZIOTraceInstances {
  implicit def spannedRIOTrace[R <: Clock with Blocking with Has[Span[RIO[Clock with Blocking, *]]]]: SpannedRIOTracer[
    R
  ] = new SpannedRIOTracer[R]
}
