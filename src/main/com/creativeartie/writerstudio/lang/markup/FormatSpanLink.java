package com.creativeartie.writerstudio.lang.markup;

import java.util.*;
import com.creativeartie.writerstudio.lang.*;

/**
 * Formatted {@link Span} for common methods in {@link FormatSpanLinkDirect}
 * and {@link FormatSpanLinkRef}.
 */
public abstract class FormatSpanLink extends FormatSpan {

    FormatSpanLink(List<Span> children, boolean[] formats){
        super(children, formats);
    }

    /**
     * Return the path of the link.
     */
    public abstract Optional<SpanBranch> getPathSpan();

    /**
     * Return the display output text.
     */
    public abstract String getText();


    public abstract boolean isExternal();

    @Override
    public final String getOutput(){
        return "text: " + SpanLeaf.escapeText(getText()) + " link: " +
            getPathSpan().map(s -> s.toString()).orElse("null");
    }
}
