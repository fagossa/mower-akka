package model

import model.Mower.rotations

case class Dimension(width: Int, height: Int)

case class Position(x: Int, y: Int)

object Position {
  // Note that because `Ordering[A]` is not contravariant, the declaration
  // must be type-parametrized in the event that you want the implicit
  // ordering to apply to subclasses of `Employee`.
  implicit def orderingByPos[A <: Position]: Ordering[A] =
    Ordering.by(e => (e.x, e.y))
}

case class Surface(dim: Dimension) {
  def contains(position: Position): Boolean =
    position.x >= 0 && position.y >= 0 &&
      position.x <= dim.width && position.y <= dim.height

}

case class Mower(id: Int, surface: Surface, pos: Position, ori: Orientation) {

  def rotate(command: Command): Mower = {
    val nextOrientation = rotations((ori, command))
    copy(ori = nextOrientation)
  }

  def forward: Mower = {
    val doNotMove = this
    val newState = ori match {
      case North => copy(pos = Position(pos.x, pos.y + 1))
      case East => copy(pos = Position(pos.x + 1, pos.y))
      case West => copy(pos = Position(pos.x - 1, pos.y))
      case South => copy(pos = Position(pos.x, pos.y - 1))
    }
    if (surface contains newState.pos) newState else doNotMove
  }

  def execute(commands: Command): Mower = commands match {
    case Forward => forward
    case _ => rotate(commands)
  }

}

object Mower {

  val rotations = Map[(Orientation, Command), Orientation](
    ((North, Right), East), ((North, Left), West),
    ((West, Right), North), ((West, Left), South),
    ((South, Right), West), ((South, Left), East),
    ((East, Right), South), ((East, Left), North))

}

