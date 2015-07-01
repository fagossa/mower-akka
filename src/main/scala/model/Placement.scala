package model

sealed trait Instruction

// Direction
sealed trait Command extends Instruction

case object Right extends Command

case object Left extends Command

case object Forward extends Command

object Command {

  def unapply(dir: Char): Option[Command] = dir match {
    case 'D' => Some(Right)
    case 'G' => Some(Left)
    case 'A' => Some(Forward)
  }

}

// Orientation
sealed trait Orientation extends Instruction

case object North extends Orientation

case object East extends Orientation

case object West extends Orientation

case object South extends Orientation

object Orientation {

  def unapply(dir: Char): Option[Orientation] = dir match {
    case 'N' => Some(North)
    case 'E' => Some(East)
    case 'W' => Some(West)
    case 'S' => Some(South)
  }

}
