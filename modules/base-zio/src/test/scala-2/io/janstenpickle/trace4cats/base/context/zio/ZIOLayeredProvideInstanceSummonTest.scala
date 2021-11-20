package io.janstenpickle.trace4cats.base.context.zio

import _root_.zio.{Has, ZEnv, ZIO}
import io.janstenpickle.trace4cats.base.context._
import io.janstenpickle.trace4cats.base.context.zio.instances._

// These summon tests work only on Scala 2.
// As of Scala 3.1.0, the compiler fails to provide evidence for `(R & Has[C]) <:< R1`.
// In user code the instances must be summoned explicitly via `zioProvideSome`.
object ZIOLayeredProvideInstanceSummonTest {
  class R()
  type ZR = Has[R]
  type E
  type Low[x] = ZIO[ZEnv, E, x]

  type F[x] = ZIO[ZEnv with ZR, E, x]
  implicitly[Ask[F, R]]
  implicitly[Local[F, R]]
  implicitly[Provide[Low, F, R]]

  type G[x] = ZIO[ZR with ZEnv, E, x]
  implicitly[Ask[G, R]]
  implicitly[Local[G, R]]
  implicitly[Provide[Low, G, R]]
}
