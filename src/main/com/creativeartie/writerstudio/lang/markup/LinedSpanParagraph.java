package com.creativeartie.writerstudio.lang.markup;

import java.util.*;

import com.creativeartie.writerstudio.lang.*;
import static com.creativeartie.writerstudio.lang.markup.AuxiliaryData.*;

/**
 * Line representing a basic paragraph. Represented in design/ebnf.txt as
 * {@code LinedParagraph}
 */
public class LinedSpanParagraph extends LinedSpan {

    private final CacheKeyOptional<FormattedSpan> cacheFormatted;
    private final CacheKeyMain<Integer> cachePublish;
    private final CacheKeyMain<Integer> cacheNote;

    LinedSpanParagraph(List<Span> children){
        super(children);
        cacheFormatted = new CacheKeyOptional<>(FormattedSpan.class);
        cachePublish = CacheKey.integerKey();
        cacheNote = CacheKey.integerKey();
    }

    public Optional<FormattedSpan> getFormattedSpan(){
        return getLocalCache(cacheFormatted, () -> spanAtFirst(
            FormattedSpan.class).orElse(null));
    }

    @Override
    public int getPublishTotal(){
        return getLocalCache(cachePublish, () ->
            getFormattedSpan().map(span -> span.getPublishTotal()).orElse(0));
    }

    @Override
    public int getNoteTotal(){
        return getLocalCache(cacheNote, () ->
            getFormattedSpan().map(span -> span.getNoteTotal()).orElse(0));
    }

    @Override
    protected SetupParser getParser(String text){
        /// Make sure it turn into a different line type
        for (String token: getLinedTokens()){
            if (text.startsWith(token)){
                return null;
            }
        }

        return AuxiliaryChecker.checkLineEnd(text, isLast())?
            LinedParseRest.PARAGRAPH: null;
    }
}
