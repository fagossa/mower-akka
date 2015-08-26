package model

import actors.MowerMessages.RequestAuthorisation
import actors.{MowerActor, MowerMessages}
import akka.actor.ActorSystem

//import akka.actor._
import akka.testkit.TestProbe
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.duration._
import scala.util.Random
import MowerActorSpec._

class MowerActorSpec extends FunSpec with ScalaFutures with Matchers {

  val config = ConfigFactory.parseString(
    s"""
       |akka {
       |  loggers = ["akka.event.Logging$$DefaultLogger"]
       |  loglevel = "DEBUG"
       |}
       |kafka{
       |  zookeeper.connect = "127.0.0.1"
       |}
     """.stripMargin)

  implicit val system = ActorSystem("A_Mower_System", config)

  describe("A mower actor") {

    it("should inform that there are no commands left") {
      // given
      val mower = sampleMower
      val noMoreCommands = MowerMessages.ExecuteCommands(mower = mower, commands = Nil, 0)
      val expectedResponse = MowerMessages.AllCommandsExecutedOn(mower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! noMoreCommands

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

    it("should rotate and finish") {
      // given
      val mower = sampleMowerFacing(North)
      val commands = MowerMessages.ExecuteCommands(mower = mower, commands = List(Right, Right, Left), 0)

      val expectedMower = mower.copy(ori = East)
      val expectedResponse = MowerMessages.AllCommandsExecutedOn(expectedMower)
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
      val mower = sampleMower
      val moveForward = MowerMessages.ExecuteCommands(mower = mower, commands = List(Forward), 0)
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
      val mower = sampleMower
      val question = MowerMessages.PositionAllowed(mower = mower, commands = List(Forward))

      val expectedResponse = MowerMessages.AllCommandsExecutedOn(mower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! question

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

  }

  describe("A mower actor with authorisation rejected") {

    it("should receive position rejected") {
      // given
      val mower = sampleMower
      val question = MowerMessages.PositionRejected(mower = mower, commands = Nil, 0)

      val expectedResponse = MowerMessages.AllCommandsExecutedOn(mower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! question

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

  }
}

object MowerActorSpec {

  val surface = Surface(Dimension(1, 1))

  def sampleMower =
    Mower(id = Random.nextInt(), surface = surface, pos = Position(0, 0), ori = North)

  def sampleMowerFacing(ori: Orientation) = sampleMower.copy(ori = ori)

}
