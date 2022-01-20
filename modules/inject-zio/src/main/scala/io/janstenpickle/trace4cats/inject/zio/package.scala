package io.janstenpickle.trace4cats.inject

import _root_.zio.{Has, RIO, Task}
import _root_.zio.clock.Clock
import _root_.zio.blocking.Blocking
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.zio.ZIOContextInstances

package object zio extends ZIOTraceInstances with ZIOHasTraceInstances with ZIOContextInstances {
  // For use with ZLayers
  type SpannedEnvRIO[-R <: Clock with Blocking with Has[Span[RIO[Clock with Blocking, *]]], +A] = RIO[R, A]

  // For use with Tagless Final style/no ZLayers
  type SpannedRIO[+A] = RIO[Span[Task], A]
}
