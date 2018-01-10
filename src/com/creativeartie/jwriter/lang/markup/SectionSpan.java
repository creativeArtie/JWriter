package com.creativeartie.jwriter.lang.markup;

import java.util.*;
import java.util.Optional;
import com.google.common.base.*;
import com.google.common.collect.*;

import com.creativeartie.jwriter.lang.*;
import static com.creativeartie.jwriter.lang.markup.AuxiliaryData.*;

/**
 * Section with notes and content
 */
abstract class SectionSpan extends SpanBranch implements Catalogued{
    private Optional<Optional<SectionSpan>> cacheUpper;
    private Optional<Optional<LinedSpanLevelSection>> cacheHeading;
    private Optional<Integer> cacheLevel;
    private Optional<EditionType> cacheEdition;
    private Optional<Integer> cachePublish;
    private Optional<Integer> cacheNote;
    private Optional<Optional<CatalogueIdentity>> cacheId;
    private Optional<List<LinedSpan>> cacheLines;
    private Optional<List<NoteCardSpan>> cacheNotes;
    private Optional<Boolean> cacheLast;
    private final SectionParser spanReparser;

    SectionSpan(List<Span> children, SectionParser reparser){
        super(children);
        spanReparser = reparser;
    }

    protected final  SectionParser getParser(){
        return spanReparser;
    }

    public final Optional<SectionSpan> getUpperLevel(){
        cacheUpper = getCache(cacheUpper, () -> Optional.of(getParent())
            .filter(span -> span instanceof SectionSpan)
            .map(span -> (SectionSpan) span));
        return cacheUpper.get();
    }

    public final Optional<LinedSpanLevelSection> getHeading(){
        cacheHeading = getCache(cacheHeading, () -> spanAtFirst(
            LinedSpanLevelSection.class));
        return cacheHeading.get();
    }

    public final int getLevel(){
        cacheLevel = getCache(cacheLevel, () -> getHeading()
            .map(span -> span.getLevel()).orElse(1));
        return cacheLevel.get();
    }

    public final EditionType getEdition(){
        cacheEdition = getCache(cacheEdition, () -> getHeading()
            .map(span -> span.getEdition()).orElse(EditionType.NONE));
        return cacheEdition.get();
    }

    public final int getPublishTotal(){
        cachePublish = getCache(cachePublish, () -> {
            int count = 0;
            for (Span span: this){
                count += span instanceof LinedSpan?
                    ((LinedSpan)span).getPublishTotal():
                    (span instanceof NoteCardSpan?
                        0: ((SectionSpan)span).getPublishTotal());
            }
            return count;
        });
        return cachePublish.get();
    }

    @Override
    public final Optional<CatalogueIdentity> getSpanIdentity(){
        cacheId = getCache(cacheId, () ->
            Optional.of(new CatalogueIdentity(TYPE_SECTION, this)));
        return cacheId.get();
    }

    @Override
    public final boolean isId(){
        return true;
    }

    public final int getNoteTotal(){
        cacheNote = getCache(cacheNote, () -> {
            int count = 0;
            for (Span span: this){
                count += span instanceof LinedSpan?
                    ((LinedSpan)span).getNoteTotal():
                    (span instanceof NoteCardSpan?
                        0: ((SectionSpan)span).getNoteTotal());
            }
            return count;
        });
        return cacheNote.get();
    }

    public final List<LinedSpan> getLines(){
        cacheLines = getCache(cacheLines, () -> {
            ImmutableList.Builder<LinedSpan> lines = ImmutableList.builder();
            for (Span child: this){
                if (child instanceof LinedSpan){
                    lines.add((LinedSpan) child);
                }
            }
            return lines.build();
        });
        return cacheLines.get();
    }

    protected <T> List<T> getChildren(Class<T> getting){
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (Span span: this){
            if (getting.isInstance(span)){
                builder.add(getting.cast(span));
            }
        }
        return builder.build();
    }

    public final List<NoteCardSpan> getNotes(){
        cacheNotes = getCache(cacheNotes, () -> {
            ImmutableList.Builder<NoteCardSpan> lines = ImmutableList.builder();
            for (Span child: this){
                if (child instanceof NoteCardSpan){
                    lines.add((NoteCardSpan) child);
                }
            }
            return lines.build();
        });
        return cacheNotes.get();
    }

    protected final boolean canParse(String text, SectionParser[] values){

        boolean check = true;
        for (String line : Splitter.on(LINED_END)
                .split(text.replace(CHAR_ESCAPE + LINED_END, ""))
            ){
            for(SectionParser value: values){
                if (line.startsWith(value.getStarter())){
                    return false;
                }
                if (value == spanReparser){
                    check = false;
                }
                if (spanReparser instanceof SectionParseScene){
                    for (String str: getLevelTokens(LinedParseLevel.HEADING)){
                        if (line.startsWith(str)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void childEdited(){
        cacheUpper = Optional.empty();
        cacheHeading = Optional.empty();
        cacheLevel = Optional.empty();
        cacheEdition = Optional.empty();
        cacheNote = Optional.empty();
        cachePublish = Optional.empty();
        cacheLines = Optional.empty();
        cacheNotes = Optional.empty();
        cacheLast = Optional.empty();
    }

    @Override
    protected void docEdited(){
        cacheId = Optional.empty();
    }
}