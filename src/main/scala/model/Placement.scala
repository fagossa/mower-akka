package model

sealed trait Instruction

sealed trait Command extends Instruction

case object Right extends Command

case object Left extends Command

case object Forward extends Command


sealed trait Orientation

case object North extends Orientation

case object East extends Orientation

case object West extends Orientation

case object South extends Orientation
