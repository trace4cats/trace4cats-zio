package io.janstenpickle.trace4cats.inject

import _root_.zio.{Has, RIO}
import _root_.zio.clock.Clock
import _root_.zio.blocking.Blocking
import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.context.zio.ZIOContextInstances

package object zio extends ZIOTraceInstances with ZIOContextInstances {
  type SpannedRIO[-R <: Clock with Blocking with Has[Span[RIO[Clock with Blocking, *]]], +A] = RIO[R, A]
}
