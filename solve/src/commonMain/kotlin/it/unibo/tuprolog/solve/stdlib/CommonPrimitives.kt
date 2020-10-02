package it.unibo.tuprolog.solve.stdlib

import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.primitive.Primitive
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.*

object CommonPrimitives {
    val wrappers: Sequence<PrimitiveWrapper<*>> =
        sequenceOf(
            Abolish,
            Arg,
            ArithmeticEqual,
            ArithmeticGreaterThan,
            ArithmeticGreaterThanOrEqualTo,
            ArithmeticLowerThan,
            ArithmeticLowerThanOrEqualTo,
            ArithmeticNotEqual,
            Assert,
            AssertA,
            AssertZ,
            Atom,
            AtomChars,
            AtomCodes,
            AtomConcat,
            Atomic,
            AtomLength,
            Between,
            Callable,
            CharCode,
            Clause,
            Compound,
            CopyTerm,
            CurrentOp,
            CurrentPrologFlag,
            EnsureExecutable,
            FindAll,
            Float,
            Functor,
            Ground,
            Halt,
            Integer,
            Is,
            Natural,
            NewLine,
            NonVar,
            NotUnifiableWith,
            Number,
            NumberChars,
            NumberCodes,
            Op,
            Repeat,
            Retract,
            RetractAll,
            Sleep,
            SubAtom,
            TermGreaterThan,
            TermGreaterThanOrEqualTo,
            TermIdentical,
            TermLowerThan,
            TermLowerThanOrEqualTo,
            TermNotIdentical,
            TermNotSame,
            TermSame,
            UnifiesWith,
            Univ,
            Var,
            Write
        )

    val primitives: Map<Signature, Primitive> = wrappers.map { it.descriptionPair }.toMap()
}
