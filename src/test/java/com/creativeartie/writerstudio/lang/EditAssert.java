package com.creativeartie.writerstudio.lang;

import org.junit.jupiter.api.function.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/// Tests for listeners
public class EditAssert{

    /// %Part 1: Listup tests ###################################################

    private boolean showEdits;
    private final Document useDoc;

    private final ArrayList<SpanNode<?>> expectedEdited;
    private final ArrayList<SpanNode<?>> actualEdited;

    private final ArrayList<SpanNode<?>> expectedParents;
    private final ArrayList<SpanNode<?>> actualParents;

    private final ArrayList<SpanNode<?>> expectedSpans;
    private final ArrayList<SpanNode<?>> actualSpans;

    private final ArrayList<SpanNode<?>> expectedRemoves;
    private final ArrayList<SpanNode<?>> actualRemoves;

    EditAssert(boolean show, Document doc, SpanNode<?> ... edits){
        showEdits = show;
        useDoc = doc;

        expectedEdited = new ArrayList<>();
        for (SpanNode<?> edit: edits){
            expectedEdited.add(edit);
        }
        actualEdited = new ArrayList<>();

        expectedParents = new ArrayList<>();
        for (SpanNode<?> edit: edits){
            SpanNode<?> ptr = edit;
            while (ptr instanceof SpanBranch){
                ptr = ptr.getParent();
                if (! expectedParents.contains(ptr)) expectedParents.add(ptr);
            }
        }
        actualParents = new ArrayList<>();

        expectedSpans = addAll(doc);
        expectedSpans.add(doc);
        actualSpans = new ArrayList<>();

        expectedRemoves = new ArrayList<>();
        for (SpanNode<?> edit: edits){
            for (SpanNode<?> span: addAll(edit)){
                if (! expectedRemoves.contains(span)) expectedRemoves.add(span);
                expectedParents.remove(span);
                expectedEdited.remove(span);
            }
        }
        actualRemoves = new ArrayList<>();

        addListeners(doc);
    }

    /// Add the children and grade children to the list
    private ArrayList<SpanNode<?>> addAll(SpanNode<?> from){
        ArrayList<SpanNode<?>> ans = new ArrayList<>();
        for (Span span: from){
            if (span instanceof SpanNode){
                ans.add((SpanNode<?>)span);
                ans.addAll(addAll((SpanBranch) span));
            }
        }
        return ans;
    }

    /// Adds the listeners
    private void addListeners(SpanNode<?> span){
        span.addSpanEdited(this::edited);
        span.addChildEdited(this::childed);
        span.addDocEdited(this::doc);
        span.addSpanRemoved(this::removed);
        for (Span child: span){
            if (child instanceof SpanNode){
                addListeners((SpanNode<?>) child);
            }
        }
    }

    /// %Part 2: Listener functions ############################################

    private void edited(SpanNode<?> span){
        actualEdited.add(span);
    }

    private void childed(SpanNode<?> span){
        actualParents.add(span);
    }

    private void doc(SpanNode<?> span){
        actualSpans.add(span);
    }

    private void removed(SpanNode<?> span){
        actualRemoves.add(span);
    }

    /// %Part 3: Assertion Calls ###############################################

    void testRest(){
        if (showEdits) {
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++");
            System.out.println(useDoc);
            System.out.println(expectedEdited);

            System.out.println("Edits-----------------");
            print(expectedEdited, actualEdited);

            System.out.println("Parents-----------------");
            print(expectedParents, actualParents);

            System.out.println("Spans-----------------");
            print(expectedSpans, actualSpans);

            System.out.println("Removes-----------------");
            print(expectedRemoves, actualRemoves);
        }

        ArrayList<Executable> tests = new ArrayList<>();

        /// Tests
        tests.add(search("edited", expectedEdited, actualEdited));

        tests.add(search("parent", expectedParents, actualParents));

        tests.add(search("spans", expectedSpans, actualSpans));

        tests.add(search("remove", expectedRemoves, actualRemoves));

        assertAll("edit listeners", tests);
    }

    private void print(List<SpanNode<?>> expects, List<SpanNode<?>> actual){
        print("expect", expects);
        print("actual", actual);
    }

    private void print(String type, Iterable<SpanNode<?>> list){
        int i = 1;
        for (SpanNode<?> span: list){
            System.out.println(type + "[" + i++ + "]" + span);
        }
        if (i == 1){
            System.out.println("[]");
        }
    }

    private Executable search(String name, List<SpanNode<?>> expect,
            List<SpanNode<?>> actual){
        ArrayList<Executable> list = new ArrayList<>();
        for (SpanNode<?> got: actual){
            list.add(() -> {
                assertTrue(expect.contains(got),
                    () -> "Unexpected: "  + got);
                expect.remove(got);
            });
        }

        /// since the one called are removed from `expect`:
        list.add(() ->  assertTrue(expect.isEmpty(), () -> "Not called: " + expect));
        return () -> assertAll(name, list);
    }
}
