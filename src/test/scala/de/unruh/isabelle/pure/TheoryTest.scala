package de.unruh.isabelle.pure

import java.nio.file.{Files, Path, Paths}

import de.unruh.isabelle.control.{IsabelleMLException, IsabelleTest}
import de.unruh.isabelle.control.IsabelleTest.setup
import de.unruh.isabelle.mlvalue.MLValue
import org.scalatest.funsuite.AnyFunSuite

// Implicits
import de.unruh.isabelle.control.IsabelleTest.isabelle
import de.unruh.isabelle.mlvalue.Implicits._

class TheoryTest extends AnyFunSuite {
  test("import structure") {
    assertThrows[IsabelleMLException] {
      isabelle.executeMLCodeNow("HOLogic.boolT") }
    val thy = Theory("Main")
    val struct = thy.importMLStructureNow("HOLogic")
    println(struct)
    isabelle.executeMLCodeNow(s"${struct}.boolT")
  }

  test("importMLStructureNow example") {
    val thy : Theory = Theory(Path.of("ImportMeThy.thy"))                         // load the theory TestThy
    val num1 : MLValue[Int] = MLValue.compileValue("ImportMe.num")     // fails
    assertThrows[IsabelleMLException] { num1.retrieveNow }
    val importMe : String = thy.importMLStructureNow("ImportMe")     // import the structure Test into the ML toplevel
    val num2 : MLValue[Int] = MLValue.compileValue(s"${importMe}.num") // access Test (under new name) in compiled ML code
    assert(num2.retrieveNow == 123)                                              // ==> 123
  }

  test("load theory outside heap") {
    Theory.registerSessionDirectoriesNow("HOL-Library" -> setup.isabelleHome.resolve("src/HOL/Library"))
    Theory("HOL-Library.BigO").force
  }

  test("load theory inside heap") {
    Theory("HOL.Set").force
  }

  test("load theory by path") {
    val thyPath = Paths.get("Empty.thy")
    assert(Files.exists(setup.workingDirectory.resolve(thyPath)))
    Theory(thyPath).force
  }

  // smt did, at least at some point with 2021-1-RC1, give an error when loaded here
  test("theory with SMT call") {
    val thyPath = Paths.get("Theory_With_Smt.thy")
    assert(Files.exists(setup.workingDirectory.resolve(thyPath)))
    Theory(thyPath).force
  }

  test("load theory by path, nested") {
    val thyPath = Paths.get("Subdir/B.thy")
    assert(Files.exists(setup.workingDirectory.resolve(thyPath)))
    Theory(thyPath).force
  }

  test("registerSessionDirectories loaded session") {
    Theory.registerSessionDirectoriesNow("HOL" -> setup.isabelleHome.resolve("src/HOL"))
    Theory("HOL.Filter").force
  }

  test("registerSessionDirectories loaded session, wrong session dir") {
    val badHOL = IsabelleTest.scalaIsabelleDir.resolve("src/test/isabelle/Bad-HOL").toAbsolutePath
    assert(Files.isDirectory(badHOL))
    Theory.registerSessionDirectoriesNow("HOL" -> badHOL)
    val thy = Theory("HOL.Filter").force
    val ctxt = Context(thy)
    val thm = Thm(ctxt, "Filter.filter_eq_iff").force
    println(thm.pretty(ctxt))
  }

  test("mergeTheories") {
    val gcd = Theory("HOL.GCD")
    val filter = Theory("HOL.Filter")
    Thm(Context(gcd), "gcd_lcm").force
    assertThrows[IsabelleMLException] { Thm(Context(filter), "gcd_lcm").force }
    assertThrows[IsabelleMLException] { Thm(Context(gcd), "eventually_Abs_filter").force }
    Thm(Context(filter), "eventually_Abs_filter").force
    val merged = Theory.mergeTheories(gcd, filter)
    Thm(Context(merged), "gcd_lcm").force
    Thm(Context(merged), "eventually_Abs_filter").force
  }
}
