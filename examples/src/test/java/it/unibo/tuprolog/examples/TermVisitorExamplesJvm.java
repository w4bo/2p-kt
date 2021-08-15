package it.unibo.tuprolog.examples;

import it.unibo.tuprolog.core.Clause;
import it.unibo.tuprolog.core.Directive;
import it.unibo.tuprolog.core.Rule;
import it.unibo.tuprolog.core.Struct;
import it.unibo.tuprolog.core.Term;
import it.unibo.tuprolog.core.Tuple;
import it.unibo.tuprolog.core.TermVisitor;
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor;
import it.unibo.tuprolog.theory.Theory;
import it.unibo.tuprolog.theory.parsing.ClausesParser;

import kotlin.sequences.Sequence;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

import static kotlin.sequences.SequencesKt.map;

public class TermVisitorExamplesJvm {

    private final TermVisitor<Term> ruleRenamer = new DefaultTermVisitor<Term>() {

        @Override
        public Term defaultValue(@NotNull Term term) { return term; }

        @Override
        public Term visitStruct(@NotNull Struct term) {
            // if the current struct is one of '->'/2, ';'/2, or ','/2  the visitor must be recursively re-applied
            if (Clause.getNotableFunctors().contains(term.getFunctor())) {
                return Struct.of(
                        term.getFunctor(),
                        term.getArgAt(0).accept(this),
                        term.getArgAt(1).accept(this)
                );
                // otherwise, if the struct is len/2 it must be renamed
            } else if (term.getFunctor().equals("len") && term.getArity() == 2) {
                return Struct.of("len_internal", term.getArgs());
            } else {
                return term;
            }
        }

        @Override
        public Term visitRule(@NotNull Rule term) {
            // rules require renaming (i.e. the visitor) to be applied to both head and bodies
            return Rule.of(
                    term.getHead().accept(this).accept(this).asStruct(),
                    term.getBody().accept(this)
            );
        }

        @Override
        public Term visitDirective(@NotNull Directive term) {
            // directives require renaming (i.e. the visitor) to be applied to their whole body
            return Directive.of(term.getBody().accept(this));
        }

        @Override
        public Term visitTuple(@NotNull Tuple term) {
            // if the current term is a conjunction of goals (A, B, ...) the visitor must be recursively re-applied
            return Tuple.of(
                    term.toList().stream().map(t -> t.accept(this)).collect(Collectors.toList())
            );
        }
    };


    @Test
    public void clauseRenaming() {
        Sequence<Clause> clauses = ClausesParser.getWithDefaultOperators().parseClausesLazily(
                "len([], 0).\n" +
                    "len([_|T], I) :- len(T, J), I is J + 1."
        );
        Sequence<Clause> renamedClauses = map(clauses, clause -> clause.accept(ruleRenamer).castToClause());
        Theory renamedTheory = Theory.of(renamedClauses);

        Theory expectedResult = ClausesParser.getWithDefaultOperators().parseTheory(
                "len_internal([], 0).\n" +
                    "len_internal([_|T], I) :- len_internal(T, J), I is J + 1."
        );
        Assert.assertTrue(
                expectedResult.equals(renamedTheory, false)
        );
    }
}
