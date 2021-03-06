package load

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._


class LoadScript2 extends Simulation {

  val httpProtocol = http
    .baseUrl("http://computer-database.gatling.io")
    .inferHtmlResources()
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0")
    .proxy(
      Proxy("127.0.0.1", 8888)
        .httpsPort(8888))

  //  val users = scenario("Users").asLongAs(true) {
  //    exec(Search.search, Browse.browse)
  //  }

  // val users = scenario("Users").exec(Search.search, Browse.browse)
  //val admins = scenario("Admins").exec(Search.search, Browse.browse, Edit.edit)

  val admins = scenario("Admins").repeat(100,"repA")
  {
    pace(10)
      .exec(Search.search, Browse.browse, Edit.edit)
  }

  val users = scenario("Users").repeat(100,"repU") {
    pace(10)
      .exec(Search.search, Browse.browse)
  }


  //  setUp(
  //    users.inject(atOnceUsers(1)).protocols(httpProtocol),
  //    admins.inject(atOnceUsers(1)).protocols(httpProtocol)
  //  )


  setUp(
    users.inject(
      nothingFor(2 seconds),
      atOnceUsers(2),
      nothingFor(1 minutes),
      constantUsersPerSec(5) during (15 seconds),
      nothingFor(1 minutes),
      constantUsersPerSec(5) during (15 seconds),
      nothingFor(1 minutes),
      constantUsersPerSec(5) during (15 seconds),
      nothingFor(1 minutes),
    ),
    admins.inject(rampUsers(3) during (5 minutes))
  ).protocols(httpProtocol).assertions(Seq(
    global.responseTime.percentile3.lte(1000)))
}

object Search {

  val feeder = csv("user-files\\search.csv").random // 1, 2
  val testHeader = Map("Accept" -> "image/webp,*/*")

  //  val search = exec(http("Startpage")
  //    .get("/")
  //    .check(status.is(200)))
  //    //.headers(headers_1)
  //    .pause(1)
  //    .exec(http("Search")
  //      .get("/computers?f=macbook"))
  //  //.headers(headers_1)

  val search = exec(http("Home")
    .get("/")
    .headers(testHeader)
    .check(status.is(200))
    .check(css("title").saveAs("nartov"))
    .check(regex("<title>(.*?)</").saveAs("nartov2"))
  )
    .pause(1)
    .exec({ session =>
      println(session("nartov").as[String] + " eto test")
      session
    })
    .feed(feeder)
    .exec(http("Search")
      .get("/computers?f=${searchCriterion}")
      .check(css("a:contains('${searchComputerName}')", "href").saveAs("computerURL")))
    .pause(1)
    .exec(http("Select")
      .get("${computerURL}"))
    .pause(1)
}

object Browse {

  def gotoPage(page: String) =
    exec(http("Page " + page)
      .get("/computers?p=" + page))
      .pause(1)

  val browse = repeat(5, "n") {
    exec(gotoPage("${n}"))
  }

  //  val browse = repeat(5, "n")
  //  {
  //    exec(http("Page ${n}")
  //      .get("/computers?p=${n}"))
  //      .pause(1)
  //  }
}

object Edit {

  val edit = exec(http("Form")
    .get("/computers/new"))
    .pause(1)
    .exec(http("Post")
      .post("/computers")
      .check(status.is(session => 200)))
}



