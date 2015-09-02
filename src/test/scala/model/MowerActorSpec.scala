package model

import actors.MowerActor
import actors.MowerMessages._
import akka.actor.ActorSystem
import akka.testkit.TestProbe
import com.typesafe.config.ConfigFactory
import model.MowerActorSpec._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scala.concurrent.duration._
import scala.util.Random

class MowerActorSpec extends FunSpec with ScalaFutures with Matchers with BeforeAndAfterEach {

  val config = ConfigFactory.parseString(
    s"""
       |akka {
       |  loggers = ["akka.event.Logging$$DefaultLogger"]
       |  loglevel = "INFO"
       |}
       |kafka{
       |  zookeeper.connect = "127.0.0.1"
       |}
     """.stripMargin)

  implicit val system = ActorSystem("A_Mower_System", config)

  describe("A mower actor") {

    it("should inform that there are no commands left") {
      // given
      val mower = aMower()
      val noMoreCommands = ExecuteCommands(mower = mower, commands = Nil, 0)
      val expectedResponse = AllCommandsExecutedOn(mower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! noMoreCommands

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

    it("should rotate and finish") {
      // given
      val mower = aMowerFacing(North)
      val commands = ExecuteCommands(mower = mower, commands = List(Right, Right, Left), 0)

      val expectedMower = mower.withOrientation(East)
      val expectedResponse = AllCommandsExecutedOn(expectedMower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! commands

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

  }

  describe("A mower actor asking authorisation") {

    it("should rotate, ask for permission, move and finish") {
      // given
      val mower = aMowerFacing(North)
      val commands = ExecuteCommands(mower = mower, commands = List(Right, Right, Left, Forward), 5)

      val previousMower = mower.withOrientation(East)
      val expectedMower = mower.withOrientation(East).withPosition(Position(1, 0))
      val expectedResponse = RequestAuthorisation(previousMower, expectedMower, List(Forward), 0)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! commands

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

    it("should rotate, ask for permission, stay and finish") {
      // given
      val mower = aMowerFacing(South).withPosition(Position(0, 1))
      val commands = ExecuteCommands(mower = mower, commands = List(Right, Right, Left, Left, Forward), 5)

      val previousMower = mower.withOrientation(South)
      val expectedMower = mower.withOrientation(South).withPosition(Position(0, 0))
      val expectedResponse = RequestAuthorisation(previousMower, expectedMower, List(Forward), 0)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! commands

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

  }


  describe("A mower actor with authorisation granted") {

    it("should move forward and ask for authorisation") {
      // given
      val mower = aMower()
      val moveForward = ExecuteCommands(mower = mower, commands = List(Forward), 0)
      val mowerResult: Mower = mower.forward
      val expectedResponse = RequestAuthorisation(mower, mowerResult, List(Forward), 0)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! moveForward

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

    it("should receive position allowed and then finish") {
      // given
      val mower = aMower()
      val question = PositionAllowed(mower = mower, commands = List(Forward))

      val expectedResponse = AllCommandsExecutedOn(mower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! question

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

  }

  describe("A mower actor with authorisation rejected") {

    it("should reply by AllCommandsExecuted") {
      // given
      val mower = aMower()
      val question = PositionRejected(mower = mower, commands = Nil, 0)

      val expectedResponse = AllCommandsExecutedOn(mower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! question

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

  }

  describe("A mower actor that stops itself") {

    it("should not answer any more messages") {
      // given
      val mower = aMower()
      val question = TerminateProcessing(mower = mower)

      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! question

      // then
      probe.expectNoMsg(5.seconds)
    }

  }

}

object MowerActorSpec {

  val surface = Surface(Position(5, 5))

  def aMower(id: Int = Random.nextInt()) = Mower(id = id, surface = surface, pos = Position(0, 0), ori = North)

  def aMowerFacing(ori: Orientation) = aMower().copy(ori = ori)

  implicit class MowerOperations(mower: Mower) {

    def withPosition(newPos: Position) = mower.copy(pos = newPos)

    def withOrientation(newOri: Orientation) = mower.copy(ori = newOri)
  }

}
