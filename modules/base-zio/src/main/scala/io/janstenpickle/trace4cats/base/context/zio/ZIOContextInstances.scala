package io.janstenpickle.trace4cats.base.context.zio

import _root_.zio.{interop, Has, IO, NeedsEnv, ZIO}
import cats.{~>, Monad}
import io.janstenpickle.trace4cats.base.context.{Provide, Unlift}
import izumi.reflect.Tag

trait ZIOContextInstances extends ZIOContextInstancesLowPriority {
  implicit def zioProvide[E, R: NeedsEnv]: Provide[IO[E, *], ZIO[R, E, *], R] =
    new Provide[IO[E, *], ZIO[R, E, *], R] {
      def Low: Monad[IO[E, *]] = interop.catz.monadErrorInstance
      def F: Monad[ZIO[R, E, *]] = interop.catz.monadErrorInstance

      def ask[R2 >: R]: ZIO[R, E, R2] = ZIO.environment
      def local[A](fa: ZIO[R, E, A])(f: R => R): ZIO[R, E, A] = fa.provideSome(f)
      def lift[A](la: IO[E, A]): ZIO[R, E, A] = la
      def provide[A](fa: ZIO[R, E, A])(r: R): IO[E, A] = fa.provide(r)

      override def access[A](f: R => A): ZIO[R, E, A] = ZIO.access(f)
      override def accessF[A](f: R => ZIO[R, E, A]): ZIO[R, E, A] = ZIO.accessM(f)
      override def kleislift[A](f: R => IO[E, A]): ZIO[R, E, A] = ZIO.accessM(f)
    }

  implicit def zioUnliftSome[R, R1 <: R: NeedsEnv, E]: Unlift[ZIO[R, E, *], ZIO[R1, E, *]] =
    new Unlift[ZIO[R, E, *], ZIO[R1, E, *]] {
      def Low: Monad[ZIO[R, E, *]] = interop.catz.monadErrorInstance
      def F: Monad[ZIO[R1, E, *]] = interop.catz.monadErrorInstance

      def lift[A](la: ZIO[R, E, A]): ZIO[R1, E, A] = la
      def askUnlift: ZIO[R1, E, ZIO[R1, E, *] ~> ZIO[R, E, *]] =
        ZIO.access[R1](r1 =>
          new ZIO[R1, E, *] ~> ZIO[R, E, *] {
            def apply[A](fa: ZIO[R1, E, A]): ZIO[R, E, A] = fa.provide(r1)
          }
        )
    }
}

trait ZIOContextInstancesLowPriority {
  implicit def zioProvideSome[R <: Has[_], R1 <: Has[_], E, C: Tag](implicit
    ev1: R1 <:< R with Has[C],
    ev2: R with Has[C] <:< R1
  ): Provide[ZIO[R, E, *], ZIO[R1, E, *], C] =
    new Provide[ZIO[R, E, *], ZIO[R1, E, *], C] {
      def Low: Monad[ZIO[R, E, *]] = interop.catz.monadErrorInstance
      def F: Monad[ZIO[R1, E, *]] = interop.catz.monadErrorInstance

      def ask[C2 >: C]: ZIO[R1, E, C2] = ZIO.service[C].provideSome(ev1)
      def local[A](fa: ZIO[R1, E, A])(f: C => C): ZIO[R1, E, A] = fa.provideSome[R1](_.update(f))
      def lift[A](la: ZIO[R, E, A]): ZIO[R1, E, A] = la.provideSome(ev1)
      def provide[A](fa: ZIO[R1, E, A])(c: C): ZIO[R, E, A] = fa.provideSome[R](_.add(c))
    }
}
