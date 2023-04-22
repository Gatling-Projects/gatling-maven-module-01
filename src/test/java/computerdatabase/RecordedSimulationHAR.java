package computerdatabase;

//import java.time.Duration;
//import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
//import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
//import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class RecordedSimulationHAR extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
          .baseUrl("https://computer-database.gatling.io")
          .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*", ".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
          .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
          .acceptEncodingHeader("gzip, deflate")
          .acceptLanguageHeader("en-US,en;q=0.9")
          .upgradeInsecureRequestsHeader("1")
          .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36");


  // ADD FEEDER TO RETRIEVE SEARCH TERMS
  FeederBuilder.Batchable<String> searchFeeder = csv("data/search.csv").random();
  
  // ADD FEEDER TO RETRIEVE CREATE COMPUTER DETAILS
  FeederBuilder.Batchable<String> updateFeeder = csv("data/createComputerList.csv").random();

  // ADD CHAINBUILDERS: ACTION DESIGN
  // SEARCH COMPUTER ACTION
  ChainBuilder searchComputer =
        exec(http("LoadHomePage")
                .get("/computers"))
                .pause(2)
                .feed(searchFeeder)
                // SEARCH FOR COMPUTER
                .exec(http("SearchComputers_#{searchCriterion}")
                        .get("/computers?f=#{searchCriterion}")
                        .check(css("a:contains('#{searchComputerName}')", "href").saveAs("computerURL")))
                .pause(2)
                // OPEN SPECIFIC COMPUTER DETAIL PAGE
                .exec(http("LoadComputerDetails_#{searchComputerName}")
                        .get("#{computerURL}"))
                .pause(2);

  // BROWSE COMPUTERS LIST ACTION
  ChainBuilder browseComputersList =
        repeat(5, "n").on(
                exec(http("LoadComputersList#{n}")
                        .get("/computers?p=#{n}"))
                        .pause(2)
        );

  // CREATE A NEW COMPUTER ACTION
  ChainBuilder createComputer =
        // OPEN PAGE TO CREATE A NEW COMPUTER
        feed(updateFeeder)
                .exec(http("LoadCreateComputerPage")
                        .get("/computers/new")
                        .check(css("option:contains('#{company}')", "value").saveAs("companyId")))
                .pause(2)
                .exec(http("CreateNewComputer_#{name}")
                        .post("/computers")
                        .formParam("name", "#{name}")
                        .formParam("introduced", "#{introduced}")
                        .formParam("discontinued", "#{discontinued}")
                        .formParam("company", "#{companyId}")
                        .check(status().is(200)));

  // SCENARIO DESIGN
  private ScenarioBuilder adminUsers = scenario("AdminUserSccenario")
          .exec(searchComputer, browseComputersList, createComputer);
  private ScenarioBuilder regUsers = scenario("RegularUserScenario")
          .exec(searchComputer, browseComputersList);

  // SCENARIO SIMULATION DESIGN
  {
    setUp(
            adminUsers.injectOpen(atOnceUsers(1)),
            //regUsers.injectOpen(atOnceUsers(1))
            regUsers.injectOpen(
                        nothingFor(10),
                        atOnceUsers(1),
                        rampUsers(5).during(10),
                        constantUsersPerSec(2).during(20)
                        //constantUsersPerSec(3).during(10).randomized()
                        //rampUsersPerSec(2).to(8).during(10)
                )
        ).protocols(httpProtocol);
  }
}
