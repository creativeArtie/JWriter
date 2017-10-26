package com.creativeartie.jwriter.lang.markup;

import java.util.*;

import com.google.common.collect.*;

import com.creativeartie.jwriter.lang.*;

public abstract class LinedSpanPoint extends LinedSpan implements Catalogued{

    LinedSpanPoint(List<Span> children){
        super(children);
    }

    public abstract DirectoryType getDirectoryType();

    @Override
    public List<DetailStyle> getBranchStyles(){
        ImmutableList.Builder<DetailStyle> builder = ImmutableList.builder();
        return builder.addAll(super.getBranchStyles()).add(getIdStatus()).build();
    }

    @Override
    public Optional<CatalogueIdentity> getSpanIdentity(){
        return spanFromFirst(DirectorySpan.class).map(span -> span.buildId());
    }

    @Override
    public boolean isId(){
        return true;
    }
}
