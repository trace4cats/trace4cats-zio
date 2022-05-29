package trace4cats.zio

import trace4cats.Span
import trace4cats.optics.Lens
import zio.Task

final case class Env(dummy: String, span: Span[Task])
object Env {
  def span: Lens[Env, Span[Task]] = Lens[Env, Span[Task]](_.span)(s => _.copy(span = s))
}
