package com.creativeartie.writerstudio.lang.markup;

import java.util.*;

import com.creativeartie.writerstudio.lang.*;
import static com.creativeartie.writerstudio.lang.markup.AuxiliaryData.*;
import static com.creativeartie.writerstudio.main.Checker.*;

/**
 * Parser for {@link LinedSpanPoint}. {@code LinedSpanPoint} is the base class
 * of {@link LinedSpanPointLink} and {@link LinedSpanPointNote}
 */
enum LinedParsePointer implements SetupParser {
    FOOTNOTE(LINED_FOOTNOTE), ENDNOTE(LINED_ENDNOTE), LINK(LINED_LINK){

        @Override
        public Optional<SpanBranch> parse(SetupPointer pointer){
            checkNotNull(pointer, "pointer");
            ArrayList<Span> children = new ArrayList<>();
            if (pointer.startsWith(children, LINED_LINK)){
                parseCommon(children, pointer);
                if (pointer.startsWith(children, LINED_DATA)){
                    CONTENT_DIR_LINK.parse(pointer, children);
                }
                pointer.startsWith(children, LINED_END);
                LinedSpanPointLink ans = new LinedSpanPointLink(children);
                return Optional.of(ans);
            }
            return Optional.empty();
        }

    };

    private final String spanStart;

    private LinedParsePointer(String start){
        spanStart = start;
    }

    void parseCommon(ArrayList<Span> children, SetupPointer pointer){
        checkNotNull(pointer, "childPointer");
        checkNotNull(children, "spanChildren");
        DirectoryType idType = DirectoryType.values()[ordinal() + 2];
        DirectoryParser.getIDParser(idType).parse(pointer, children);
    }

    @Override
    public Optional<SpanBranch> parse(SetupPointer pointer){
        checkNotNull(pointer, "pointer");
        ArrayList<Span> children = new ArrayList<>();
        if (pointer.startsWith(children, spanStart)){

            parseCommon(children, pointer);

            if (pointer.startsWith(children, LINED_DATA)){
                FORMATTED_TEXT.parse(pointer, children);
            }
            pointer.startsWith(children, LINED_END);

            return Optional.of(new LinedSpanPointNote(children));
        }
        return Optional.empty();
    }
}
