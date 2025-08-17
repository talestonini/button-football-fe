import sbt.internal.util.ManagedLogger
import org.scalajs.linker.interface.ModuleSplitStyle

val scalaVer  = "3.7.2" // update prep_public.sh to match this version
val circeVer  = "0.14.14"
val http4sVer = "0.23.30"

lazy val buttonFootballFrontEnd = project.in(file("."))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    version := "0.1.0",
    scalaVersion := scalaVer,

    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "livechart" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
        .withModuleSplitStyle(
          ModuleSplitStyle.SmallModulesFor(List("button-football-fe")))
    },

    /* Depend on the scalajs-dom library.
     * It provides static types for the browser DOM APIs.
     */
    libraryDependencies ++= Seq(
      "org.scala-js"  %%% "scalajs-dom" % "2.8.1",
      "com.raquo"     %%% "laminar"     % "17.2.1",
      "org.scalameta" %%% "munit"       % "1.1.1" % Test,

      // Http4s (backend and database stuff)
      "io.circe"   %%% "circe-core"      % circeVer,
      "io.circe"   %%% "circe-generic"   % circeVer,
      "io.circe"   %%% "circe-parser"    % circeVer,
      "org.http4s" %%% "http4s-circe"    % http4sVer,
      "org.http4s" %%% "http4s-client"   % http4sVer,
      "org.http4s" %%% "http4s-dsl"      % http4sVer,
      "org.http4s" %%% "http4s-dom"      % "0.2.12", // this is maintained by Arman Bilge
      "io.monix"   %%% "monix-execution" % "3.4.1",
    ),

    // Tell ScalablyTyped that we manage `npm install` ourselves
    externalNpm := baseDirectory.value,

    // BuilfInfoPlugin
    buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "com.talestonini",
  )

// ---------------------------------------------------------------------------------------------------------------------
// Config
// Tasks fastLinkJS and fullLinkJS to replace code secrets from files .secrets-dev and .secrets-prod
// ---------------------------------------------------------------------------------------------------------------------
lazy val replaceDevSecrets = taskKey[Unit]("Replaces secret references in the code for fast linking")
replaceDevSecrets := {
  val log = streams.value.log
  log.info("Replacing DEV secret references:")
  loadSecretsFrom(baseDirectory.value / ".secrets-dev").foreach { entry =>
    replaceString(
      log,
      baseDirectory.value / s"target/scala-$scalaVer/buttonfootballfrontend-fastopt",
      "main.js",
      entry._1,
      entry._2
    )
  }
}

lazy val replaceTestSecrets = taskKey[Unit]("Replaces secret references in the code for test fast linking")
replaceTestSecrets := {
  val log = streams.value.log
  log.info("Replacing TEST secret references:")
  loadSecretsFrom(baseDirectory.value / ".secrets-dev").foreach { entry =>
    replaceString(
      log,
      baseDirectory.value / s"target/scala-$scalaVer/buttonfootballfrontend-test-fastopt",
      "main.js",
      entry._1,
      entry._2
    )
  }
}

lazy val replaceProdSecrets = taskKey[Unit]("Replaces secret references in the code for full linking")
replaceProdSecrets := {
  val log = streams.value.log
  log.info("Replacing PROD secret references:")
  loadSecretsFrom(baseDirectory.value / ".secrets-prod").foreach { entry =>
    replaceString(
      log,
      baseDirectory.value / s"target/scala-$scalaVer/buttonfootballfrontend-opt",
      "main.js",
      entry._1,
      entry._2
    )
  }
}

def replaceString(log: ManagedLogger, dir: File, fileFilter: String, from: String, to: String) = {
  val toReplace        = s"@$from@"
  val files: Seq[File] = Option.apply((dir ** fileFilter).get).getOrElse(Seq.empty[File])
  log.info(s"* ${files.size} files to check for config $from")
  files.foreach { f =>
    val content = IO.read(f)
    if (content.contains(toReplace)) {
      log.info(s"* replacing $from in file ${f.name}")
      val replacement = content.replace(toReplace, to)
      IO.write(f, replacement)
    }
  }
}

def loadSecretsFrom(file: File): Seq[(String, String)] = {
  scala.io.Source
    .fromFile(file)
    .getLines()
    .filter(_.contains("="))
    .map(line => {
      val entry = line.split('=').toList
      (entry.head, entry.tail.head)
    })
    .toSeq
}

fastLinkJS := (Def.taskDyn {
  val fljs = (Compile / fastLinkJS).value
  Def.task {
    val rs = replaceDevSecrets.value
    fljs
  }
}).value

fullLinkJS := (Def.taskDyn {
  val fljs = (Compile / fullLinkJS).value
  Def.task {
    val rs = replaceProdSecrets.value
    fljs
  }
}).value