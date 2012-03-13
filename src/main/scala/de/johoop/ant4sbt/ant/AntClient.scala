package de.johoop.ant4sbt.ant
import java.net.Socket
import java.io.PrintStream
import java.io.BufferedReader
import java.io.InputStreamReader
import scala.annotation.tailrec
import sbt.Logger

class AntClient(port: Int) {
  import de.johoop.ant4sbt.util.Predef._

  def targets = {
    withSocketStreams(new Socket("localhost", port)) { (in, out) =>
      out.println("targets")
      readLines(in)
    }
  }

  @tailrec
  private def readLines(in: BufferedReader, acc: Seq[String] = Seq()) : Seq[String] = {
    val line = in.readLine
    line match {
      case `done` => acc
      case line => readLines(in, acc :+ line)
    }
  }

  def runTarget(target: String, logger: Logger) = {
    withSocketStreams(new Socket("localhost", port)) { (in, out) =>
      out.println("ant " + target)
      logLines(in, logger)
    }
  }

  @tailrec
  private def logLines(in: BufferedReader, logger: Logger) : Unit = {
    val line = in.readLine
    line match {
      case `done` => ()
      case line => {
        logger info line
        logLines(in, logger)
      }
    }
  }
}
