package com.creativeartie.writerstudio.lang;

import java.util.*;

import static org.junit.Assert.*;

public class EditAssert{

    private final Span targetSpan;
    private int totalSpan;
    private int totalRemoved;
    private int totalParent;
    private int countSpan;
    private int countRemoved;
    private int countParent;
    private boolean isPass;

    EditAssert(Document doc, SpanNode<?> edited){
        totalParent = 0;
        if (edited instanceof SpanBranch){
            totalParent++;
            SpanNode<?> parent = edited.getParent();
            while (parent instanceof SpanBranch){
                totalParent++;
                parent = parent.getParent();
            }
        }

        totalSpan = 0;
        totalRemoved = countRemoved(edited);
        targetSpan = edited;
        isPass = false;
        addListeners(doc, doc == targetSpan);
    }

    private int countRemoved(SpanNode<?> target){
        int ans = 0;
        for (Span child: target){
            if (child instanceof SpanNode<?>){
                ans += countRemoved((SpanNode<?>) child) + 1;
            }
        }
        return ans;
    }

    private void addListeners(SpanNode<?> span, boolean found){
        found = span == targetSpan? true : found;
        span.addSpanEdited(this::edited);
        span.addChildEdited(this::childed);
        if (! found) {
            totalSpan++;
            span.addDocEdited(this::doc);
        }
        span.addSpanRemoved(this::removed);
        for (Span child: span){
            if (child instanceof SpanNode){
                addListeners((SpanNode<?>) child, found);
            }
        }
    }

    private void doc(SpanNode<?> span){
        countSpan++;
    }

    private void edited(SpanNode<?> span){
        assertSame("Wrong span's edit fired.", targetSpan, span);
        isPass = true;
    }

    private void childed(SpanNode<?> span){
        assertTrue("Wrong span's child edited fired: " + span, findChild(span));
        countParent++;
    }

    private boolean findChild(SpanNode<?> span){
        if (span == targetSpan) return true;
        for (Span child: span){
            if (child == targetSpan){
                return true;
            }
            if (child instanceof SpanNode){
                if (findChild((SpanNode<?>) child)){
                    return true;
                }
            }
        }
        return false;
    }

    private void removed(SpanNode<?> span){
        countRemoved++;
        assertTrue("Span not removed: " + span, span.isRemoved());
    }

    void testRest(){
        assertTrue("spanEdited not fired: " + targetSpan.getClass(), isPass);
        assertEquals("Parent count", totalParent, countParent);
        assertEquals("Span count", totalSpan, countSpan);
        assertEquals("Removed count", totalRemoved, countRemoved);
    }
}