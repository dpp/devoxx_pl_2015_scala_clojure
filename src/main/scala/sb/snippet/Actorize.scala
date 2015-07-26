package sb
package snippet

import net.liftweb.actor.LiftActor
import net.liftweb.http.js.{JsExp, JsCmds, JsCmd}
import net.liftweb.http._
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds.{JsCrVar, Script}
import net.liftweb.json
import net.liftweb.json.{NoTypeHints, Serialization, JsonAST}
import net.liftweb.json.JsonAST.JString

import net.liftweb.common._
import sb.lib._
import ClojureInterop._
import net.liftweb.util.{Helpers, Schedule}

import scala.xml.NodeSeq

object Actorize extends InSession {
  def postMsg = findVar("sb.server.chat", "post-msg")


  def render = {
    <tail>
      {val toCall = "sb.client.core.receive"


    session.buildAndStoreComet(x => Full(new CometActor {
      def render: RenderOut = NodeSeq.Empty


      override def localSetup(): Unit = {
        super.localSetup()
        postMsg.invoke('add -> this)
      }

      override def localShutdown(): Unit = {
        super.localShutdown()
        postMsg.invoke('remove -> this)
      }

      override def lifespan = Full(LiftRules.clientActorLifespan.vend.apply(this))

      override def hasOuter = false

      override def parentTag = <div style="display: none"/>

      override def lowPriority: PartialFunction[Any, Unit] = new PartialFunction[Any, Unit] {
        def isDefinedAt(x: Any) = true

        def apply(x: Any): Unit = {
          transitWrite(x) match {
            case x: AnyRef => {
              import json._
              implicit val formats = Serialization.formats(NoTypeHints)

              val ser: Box[String] = Helpers.tryo(Serialization.write(x))

              ser.foreach(s => partialUpdate(JsCmds.JsSchedule(JsCmds.JsTry(JsRaw(toCall + "(" + s + ")").cmd, false))))

            }

            case _ => // this will never happen because the message is boxed

          }
        }
      }
    }))(CometCreationInfo("ClientProxy", Empty, Nil, Map.empty, session))

    // Create a server-side Actor that will receive messages when
    // a function on the client is called
    val serverActor = new LiftActor {
      override protected def messageHandler = {
        case JString(str) => postMsg.invoke(ClojureInterop.transitRead(str))
      }
    }

    Script(JsRaw("var sendToServer = " + session.clientActorFor(serverActor).toJsCmd).cmd)}
    </tail>

  }

}

