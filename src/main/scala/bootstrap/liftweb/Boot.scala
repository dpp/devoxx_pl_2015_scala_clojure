package bootstrap.liftweb


import ch.qos.logback.classic.Level
import sb.lib.ClojureInterop
import net.liftweb._
import net.liftweb.common.{Empty, Full}
import org.slf4j.{Logger, LoggerFactory}
import util._
import Helpers._


import http._

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {

    LiftRules.securityRules = () => SecurityRules(https = None,
      content = None,
      frameRestrictions = None,
      enforceInDevMode = false,
      logInDevMode = false
    )

    val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME).asInstanceOf[ch.qos.logback.classic.Logger];
    root.setLevel(Level.INFO)

    // where to search snippet
    LiftRules.addToPackages("sb")


    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    // get rid of the content security policy warnings
    LiftRules.contentSecurityPolicyViolationReport = x => Empty


    ClojureInterop.boot()
  }
}
