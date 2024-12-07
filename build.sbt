import org.scalajs.linker.interface.ModuleSplitStyle

val circeVer  = "0.14.10"
val http4sVer = "0.23.29"

lazy val buttonFootballFrontEnd = project.in(file("."))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    scalaVersion := "3.5.2",

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
      "org.scala-js"  %%% "scalajs-dom" % "2.8.0",
      "com.raquo"     %%% "laminar"     % "17.1.0",
      "org.scalameta" %%% "munit"       % "1.0.3" % Test,

      // Http4s (backend and database stuff)
      "io.circe"   %%% "circe-core"      % circeVer,
      "io.circe"   %%% "circe-generic"   % circeVer,
      "io.circe"   %%% "circe-parser"    % circeVer,
      "org.http4s" %%% "http4s-circe"    % http4sVer,
      "org.http4s" %%% "http4s-client"   % http4sVer,
      "org.http4s" %%% "http4s-dsl"      % http4sVer,
      "org.http4s" %%% "http4s-dom"      % "0.2.11", // this is maintained by Arman Bilge
      "io.monix"   %%% "monix-execution" % "3.4.1",
    ),

    // Tell ScalablyTyped that we manage `npm install` ourselves
    externalNpm := baseDirectory.value,
  )