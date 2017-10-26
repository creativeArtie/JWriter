package com.creativeartie.jwriter.lang.markup;

import java.util.*;

import com.creativeartie.jwriter.lang.*;

public abstract class Section {
    Section(){}

    public abstract List<? extends Section> getChildren();

    public abstract Optional<LinedSpanSection> getLine();

    public Optional<MainSpanSection> getSection(){
        return getLine().map(span -> (MainSpanSection) span.getParent());
    }
}
