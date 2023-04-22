package computerdatabase;

//import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
//import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
//import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class RecordedSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("https://computer-database.gatling.io")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0");
  
  private Map<CharSequence, String> headers_0 = Map.ofEntries(
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "none"),
    Map.entry("Sec-Fetch-User", "?1")
  );
  
  private Map<CharSequence, String> headers_1 = Map.ofEntries(
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("Sec-Fetch-User", "?1")
  );
  
  private Map<CharSequence, String> headers_5 = Map.ofEntries(
    Map.entry("Origin", "https://computer-database.gatling.io"),
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("Sec-Fetch-User", "?1")
  );


  private ScenarioBuilder scn = scenario("RecordedSimulation")
    .// HOMEPAGE
    exec(
      http("request_0")
        .get("/computers")
        .headers(headers_0)
    )
    .pause(13)
    // SEARCH
    .exec(
      http("request_1")
        .get("/computers?f=macbook")
        .headers(headers_1)
    )
    .pause(12)
    // SELECT_COMPUTER
    .exec(
      http("request_2")
        .get("/computers/89")
        .headers(headers_1)
    )
    .pause(59)
    .exec(
      http("request_3")
        .get("/computers")
        .headers(headers_1)
    )
    .pause(11)
    // CREATE_COMPUTER
    .exec(
      http("request_4")
        .get("/computers/new")
        .headers(headers_1)
    )
    .pause(736)
    .exec(
      http("request_5")
        .post("/computers")
        .headers(headers_5)
        .formParam("name", "IBM-8000")
        .formParam("introduced", "")
        .formParam("discontinued", "")
        .formParam("company", "13")
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
