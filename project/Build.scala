import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "tradefast"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "mysql" % "mysql-connector-java" % "5.1.19", 
      "com.loicdescotte.coffeebean" % "html5tags_2.9.1" % "1.0-SNAPSHOT"   
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here 
      resolvers += Resolver.url("My GitHub Repository", url("http://vinodhrajagopal.github.com/repository/"))(Resolver.ivyStylePatterns))
      //resolvers += "Local Play Repository" at "file:///home/vinodh/play-2.0.3/repository"
      //resolvers += "Local Play Repository" at "file:///Users/vrajagopal/softwares/play-2.0.3/repository"     
    )
    
}
