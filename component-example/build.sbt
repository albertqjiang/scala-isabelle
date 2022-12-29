name := "isabelle-example-component"

Global / onChangedBuildSource := ReloadOnSourceChanges

//version := "snapshot"

scalaVersion := "2.13.4"

val isabelleHome = file("/home/qj213/Isabelle2021")

Compile / packageBin / artifactPath :=
  baseDirectory.value / "isabelle-example-component.jar"

unmanagedJars in Compile :=
  ((isabelleHome / "lib" / "classes" +++ isabelleHome / "contrib") ** "*.jar").classpath
