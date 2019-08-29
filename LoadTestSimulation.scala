import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._


class LoadTestSimulation extends Simulation {

  val t_iterations = Integer.getInteger("iterations", 100).toInt
  val t_concurrency = Integer.getInteger("concurrency", 10).toInt
  val t_rampUp = Integer.getInteger("ramp-up", 1).toInt
  val t_holdFor = Integer.getInteger("hold-for", 60).toInt
  val t_throughput = Integer.getInteger("throughput", 100).toInt


  val sentHeaders = Map("Content-Type" -> "application/json", "at" -> "CFAPP:cdc95bf2-24a8-4c3d-84f4-1a60a921d92b")
  val httpProtocol = http
    .baseUrl("https://alpha.cure.fit/api/page/hometab") // Here is the root for all relative URLs
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // Here are the common headers
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn: ScenarioBuilder = scenario("Page HomeTab") // A scenario is a chain of requests and pauses
    .exec {
      http("Page HomeTab").get("/").headers(sentHeaders).header("Keep-Alive", "150")
    }

  val execution = scn
    .inject(rampUsers(100) during  5)
    .protocols(httpProtocol)

  setUp(execution).
    throttle(jumpToRps(200), holdFor(10)).
    maxDuration(5 + 10)
}
