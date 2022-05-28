package trace4cats.context.zio

import _root_.zio.{Has, ZEnv, ZIO}
import trace4cats.context.zio.instances._
import trace4cats.context.{Lift, Unlift}

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
