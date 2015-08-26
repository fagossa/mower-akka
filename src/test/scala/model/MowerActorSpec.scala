package model

import actors.MowerActor
import actors.MowerMessages._
import akka.actor.{ActorRef, ActorSystem}
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
      val mower = sampleMowerFacing(North)
      val commands = ExecuteCommands(mower = mower, commands = List(Right, Right, Left), 0)

      val expectedMower = mower.copy(ori = East)
      val expectedResponse = AllCommandsExecutedOn(expectedMower)
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
      val mower = sampleMower
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
      val mower = sampleMower
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
      val mower = sampleMower
      val question = TerminateProcessing(mower = mower)

      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(MowerActor.props(probe.ref))
      mowerRef ! question

      // then
      probe.expectNoMsg(5.seconds)
    }

  }

  override protected def afterEach(): Unit = {
    
  }

}

object MowerActorSpec {

  val surface = Surface(Dimension(1, 1))

  def sampleMower =
    Mower(id = Random.nextInt(), surface = surface, pos = Position(0, 0), ori = North)

  def sampleMowerFacing(ori: Orientation) = sampleMower.copy(ori = ori)

}
