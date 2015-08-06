package model

import org.scalatest.{FunSpec, Matchers}

class MowerSpec extends FunSpec with Matchers {

  describe("A Mower") {

    describe("when moving in blocking positions") {

      val surface = Surface(Dimension(5, 5))

      it("should stay if no space left going north") {
        // given
        val mower = Mower(1, surface, Position(0, surface.dim.height), ori = North)
        // when
        val response = mower.execute(Forward)
        // then
        response should equal(Mower(1, surface, Position(0, surface.dim.height), ori = North))
      }

      it("should stay if no space left going west") {
        // given
        val mower = Mower(1, surface, Position(0, 0), ori = West)
        // when
        val response = mower.execute(Forward)
        // then
        response should equal(Mower(1, surface, Position(0, 0), ori = West))
      }

      it("should stay if no space left going east") {
        // given
        val mower = Mower(1, surface, Position(surface.dim.width, 0), ori = East)
        // when
        val response = mower.execute(Forward)
        // then
        response should equal(Mower(1, surface, Position(surface.dim.width, 0), ori = East))
      }

      it("should stay if no space left going south") {
        // given
        val mower = Mower(1, surface, Position(0, 0), ori = South)
        // when
        val response = mower.execute(Forward)
        // then
        response should equal(Mower(1, surface, Position(0, 0), ori = South))
      }

    }

    describe("when moving in normal positions") {
      val surface = Surface(Dimension(5, 5))

      it("should move south") {
        // given
        val mower = Mower(1, surface, Position(0, 1), ori = South)
        // when
        val response = mower.execute(Forward)
        // then
        response should equal(Mower(1, surface, Position(0, 0), ori = South))
      }

      it("should move north") {
        // given
        val mower = Mower(1, surface, Position(2, 2), ori = North)
        // when
        val response = mower.execute(Forward)
        // then
        response should equal(Mower(1, surface, Position(2, 3), ori = North))
      }
    }
  }

}
