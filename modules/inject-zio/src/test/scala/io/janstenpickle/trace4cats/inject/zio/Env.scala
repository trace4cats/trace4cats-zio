package io.janstenpickle.trace4cats.inject.zio

import io.janstenpickle.trace4cats.Span
import io.janstenpickle.trace4cats.base.optics.Lens
import zio.RIO
import zio.blocking.Blocking
import zio.clock.Clock

case class Env(dummy: String, span: Span[RIO[Clock with Blocking, *]])
object Env {
  def span: Lens[Env, Span[RIO[Clock with Blocking, *]]] =
    Lens[Env, Span[RIO[Clock with Blocking, *]]](_.span)(s => _.copy(span = s))
}
