import it.unibo.tuprolog.core.*

val t =  clauseOf("fact a"(1))


fun main() {
    println(t)
    println(t["X" / 3, "Y" / "a"])
    println(Atom.of("a string"))
}