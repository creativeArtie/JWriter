package com.creativeartie.writerstudio.lang.markup;

import java.util.*;

import com.creativeartie.writerstudio.lang.*;
import static com.creativeartie.writerstudio.lang.markup.AuxiliaryData.*;

/**
 * Line representing a basic paragraph. Represented in design/ebnf.txt as
 * {@code LinedParagraph}
 */
public class LinedSpanParagraph extends LinedSpan {

    private Optional<Optional<FormatSpanMain>> cacheFormatted;
    private Optional<Integer> cachePublish;
    private Optional<Integer> cacheNote;

    LinedSpanParagraph(List<Span> children){
        super(children);
    }

    public Optional<FormatSpanMain> getFormattedSpan(){
        cacheFormatted = getCache(cacheFormatted, () -> spanAtFirst(
            FormatSpanMain.class));
        return cacheFormatted.get();
    }

    @Override
    public int getPublishTotal(){
        cachePublish = getCache(cachePublish, () ->
            getFormattedSpan().map(span -> span.getPublishTotal()).orElse(0));
        return cachePublish.get();
    }

    @Override
    public int getNoteTotal(){
        cacheNote = getCache(cacheNote, () ->
            getFormattedSpan().map(span -> span.getNoteTotal()).orElse(0));
        return cacheNote.get();
    }

    @Override
    protected SetupParser getParser(String text){
        for (String token: getLinedTokens()){
            if (text.startsWith(token)){
                return null;
            }
        }
        return AuxiliaryChecker.checkLineEnd(isLast(), text)?
            LinedParseRest.PARAGRAPH: null;
    }

    @Override
    protected void childEdited(){
        cacheFormatted = Optional.empty();
        cachePublish = Optional.empty();
        cacheNote = Optional.empty();
    }

    @Override
    protected void docEdited(){}
}