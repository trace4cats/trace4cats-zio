package io.janstenpickle.trace4cats.inject.zio

import cats.syntax.show._
import io.janstenpickle.trace4cats.inject.Trace
import io.janstenpickle.trace4cats.model.{AttributeValue, SpanKind, SpanStatus, TraceHeaders}
import io.janstenpickle.trace4cats.{ErrorHandler, Span, ToHeaders}
import zio.blocking.Blocking
import zio.clock.Clock
import zio.interop.catz._
import zio.{Has, RIO, ZIO}

class SpannedRIOTracer[Env <: Clock with Blocking with Has[Span[RIO[Clock with Blocking, *]]]]
    extends Trace[SpannedRIO[Env, *]] {
  override def put(key: String, value: AttributeValue): SpannedRIO[Env, Unit] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .flatMap(_.put(key, value))

  override def putAll(fields: (String, AttributeValue)*): SpannedRIO[Env, Unit] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .flatMap(_.putAll(fields: _*))

  override def span[A](name: String, kind: SpanKind, errorHandler: ErrorHandler)(
    fa: SpannedRIO[Env, A]
  ): SpannedRIO[Env, A] = {
    for {
      env <- ZIO.environment[Env]
      span <- ZIO.service[Span[RIO[Clock with Blocking, *]]]
      result <- span
        .child(name, kind, errorHandler)
        .use { childSpan =>
          val deps = env ++ Has(childSpan)
          fa.provide(deps)
        }
    } yield result
  }

  override def headers(toHeaders: ToHeaders): SpannedRIO[Env, TraceHeaders] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .map(s => toHeaders.fromContext(s.context))

  override def setStatus(status: SpanStatus): SpannedRIO[Env, Unit] =
    ZIO
      .service[Span[RIO[Clock with Blocking, *]]]
      .flatMap(_.setStatus(status))

  override def traceId: SpannedRIO[Env, Option[String]] =
    ZIO.service[Span[RIO[Clock with Blocking, *]]].map { s =>
      Some(s.context.traceId.show)
    }

  def lens[R](f: R => Span[RIO[R, *]], g: (R, Span[RIO[R, *]]) => R): Trace[RIO[R, *]] =
    new Trace[RIO[R, *]] {
      override def put(key: String, value: AttributeValue): RIO[R, Unit] =
        ZIO.environment[R].flatMap { r =>
          f(r).put(key, value)
        }

      override def putAll(fields: (String, AttributeValue)*): RIO[R, Unit] =
        ZIO.environment[R].flatMap { r =>
          f(r).putAll(fields: _*)
        }

      override def span[A](name: String, kind: SpanKind, errorHandler: ErrorHandler)(fa: RIO[R, A]): RIO[R, A] =
        ZIO.environment[R].flatMap { r =>
          f(r).child(name, kind, errorHandler).use(s => fa.provide(g(r, s)))
        }

      override def headers(toHeaders: ToHeaders): RIO[R, TraceHeaders] =
        ZIO.environment[R].flatMap { r =>
          ZIO.effectTotal(toHeaders.fromContext(f(r).context))
        }

      override def setStatus(status: SpanStatus): RIO[R, Unit] =
        ZIO.environment[R].flatMap { r =>
          f(r).setStatus(status)
        }

      override def traceId: RIO[R, Option[String]] =
        ZIO.environment[R].flatMap { r =>
          ZIO.effectTotal(Some(f(r).context.traceId.show))
        }
    }
}
