package io.janstenpickle.trace4cats.base.context.zio

import _root_.zio.{Has, ZEnv, ZIO}
import io.janstenpickle.trace4cats.base.context._
import io.janstenpickle.trace4cats.base.context.zio.instances._

object ZIOLayeredUnliftInstanceSummonTest {
  class R()
  type ZR = Has[R]
  type E
  type Low[x] = ZIO[ZEnv, E, x]

  type F[x] = ZIO[ZEnv with ZR, E, x]
  implicitly[Lift[Low, F]]
  implicitly[Unlift[Low, F]]

  type G[x] = ZIO[ZR with ZEnv, E, x]
  implicitly[Lift[Low, G]]
  implicitly[Unlift[Low, G]]
}
