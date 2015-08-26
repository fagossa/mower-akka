package model

import actors.MowerMessages.RequestAuthorisation
import actors.{MowerActor, MowerMessages}
import akka.actor._
import akka.testkit.TestProbe
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.duration._
import scala.util.Random

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

  implicit val system = ActorSystem("TheHatefulEight", config)

  val surface = Surface(Dimension(1, 1))

  def sampleMower(id: Int) = Mower(id = id, surface = surface, pos = Position(0, 0), ori = North)

  describe("A mower actor") {

    it("should inform that there are no commands left") {
      // given
      val mower = sampleMower(Random.nextInt())
      val noMoreCommands = MowerMessages.ExecuteCommands(mower = mower, commands = Nil, 0)
      val expectedResponse = MowerMessages.AllCommandsExecutedOn(mower)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(Props(classOf[MowerActor], probe.ref))
      mowerRef ! noMoreCommands

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

    it("should move forward and ask for authorisation") {
      // given
      val mower = sampleMower(Random.nextInt())
      val moveForward = MowerMessages.ExecuteCommands(mower = mower, commands = List(Forward), 0)
      val mowerResult: Mower = mower.forward
      val expectedResponse = RequestAuthorisation(mower, mowerResult, List(Forward), 0)
      val probe = TestProbe()(system)

      // when
      val mowerRef = system.actorOf(Props(classOf[MowerActor], probe.ref))
      mowerRef ! moveForward

      // then
      probe.receiveOne(max = 5.seconds) shouldBe expectedResponse
    }

  }
}
