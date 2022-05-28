package trace4cats.context.zio

import trace4cats.context._
import trace4cats.context.zio.instances._
import _root_.zio.{IO, ZIO}

object ZIOInstanceSummonTest {
  class R()
  type E
  type F[x] = ZIO[R, E, x]
  type Low[x] = IO[E, x]

  implicitly[Lift[Low, F]]
  implicitly[Unlift[Low, F]]
  implicitly[Ask[F, R]]
  implicitly[Local[F, R]]
  implicitly[Provide[Low, F, R]]
}
