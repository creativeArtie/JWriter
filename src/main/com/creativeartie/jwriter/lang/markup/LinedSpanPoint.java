package com.creativeartie.jwriter.lang.markup;

import java.util.*;

import com.google.common.collect.*;

import com.creativeartie.jwriter.lang.*;
import static com.creativeartie.jwriter.lang.markup.AuxiliaryData.*;

/**
 * Base class of {@code LinedSpanPoint*} classes.
 */
public abstract class LinedSpanPoint extends LinedSpan implements Catalogued{

    private Optional<List<StyleInfo>> cacheStyles;
    private Optional<Optional<CatalogueIdentity>> cacheId;

    LinedSpanPoint(List<Span> children){
        super(children);
    }

    public abstract DirectoryType getDirectoryType();

    @Override
    public List<StyleInfo> getBranchStyles(){
         cacheStyles = getCache(cacheStyles, () -> {
            ImmutableList.Builder<StyleInfo> builder = ImmutableList.builder();
            return builder.addAll(super.getBranchStyles()).add(getIdStatus())
                .build();
        });
        return cacheStyles.get();
    }

    @Override
    public Optional<CatalogueIdentity> getSpanIdentity(){
        cacheId = getCache(cacheId, () ->
            spanFromFirst(DirectorySpan.class).map(span -> span.buildId())
        );
        return cacheId.get();
    }

    @Override
    public boolean isId(){
        return true;
    }

    @Override
    protected void clearLocalCache(){
        cacheStyles = Optional.empty();
        cacheId = Optional.empty();
    }
}
