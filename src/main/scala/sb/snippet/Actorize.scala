package sb
package snippet

import net.liftweb.actor.LiftActor
import net.liftweb.http.RoundTripHandlerFunc
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds.{JsCrVar, Script}
import net.liftweb.json.JsonAST.JString

import net.liftweb.common._
import sb.lib._
import ClojureInterop._
import net.liftweb.util.{Helpers, Schedule}

object Actorize extends InSession {
  def postMsg = findVar("sb.server.chat", "post-msg")


  def render = {
    <tail>
      {val clientProxy =
      session.serverActorForClient("sb.client.core.receive",
        shutdownFunc = Full(actor => postMsg.invoke('remove -> actor)),
        dataFilter = transitWrite(_))

    postMsg.invoke('add -> clientProxy) // register with the chat server

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

