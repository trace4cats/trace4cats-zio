package io.janstenpickle.trace4cats.inject.zio

import cats.syntax.show._
import io.janstenpickle.trace4cats.inject.Trace
import io.janstenpickle.trace4cats.model.{AttributeValue, SpanKind, SpanStatus, TraceHeaders}
import io.janstenpickle.trace4cats.{ErrorHandler, Span, ToHeaders}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.{Has, RIO, ZIO}

/** For use with ZLayers
  */
class SpannedEnvRIOTracer[Env <: Clock with Blocking with Has[Span[RIO[Clock with Blocking, *]]]]
    extends Trace[SpannedEnvRIO[Env, *]] {
  override def put(key: String, value: AttributeValue): SpannedEnvRIO[Env, Unit] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .flatMap(_.put(key, value))

  override def putAll(fields: (String, AttributeValue)*): SpannedEnvRIO[Env, Unit] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .flatMap(_.putAll(fields: _*))

  override def span[A](name: String, kind: SpanKind, errorHandler: ErrorHandler)(
    fa: SpannedEnvRIO[Env, A]
  ): SpannedEnvRIO[Env, A] = {
    for {
      env <- ZIO.environment[Env]
      span <- ZIO.service[Span[RIO[Clock with Blocking, *]]]
      result <- span
        .child(name, kind, errorHandler)
        .use { (childSpan: Span[RIO[Clock with Blocking, *]]) =>
          val deps = env ++ Has(childSpan)
          fa.provide(deps)
        }
    } yield result
  }

  override def headers(toHeaders: ToHeaders): SpannedEnvRIO[Env, TraceHeaders] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .map(s => toHeaders.fromContext(s.context))

  override def setStatus(status: SpanStatus): SpannedEnvRIO[Env, Unit] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .flatMap(_.setStatus(status))

  override def traceId: SpannedEnvRIO[Env, Option[String]] =
    ZIO.service[Span[RIO[Clock with Blocking, *]]].map { s =>
      Some(s.context.traceId.show)
    }
}
