package com.creativeartie.jwriter.lang.markup;

import com.creativeartie.jwriter.lang.*;

import static com.creativeartie.jwriter.lang.markup.AuxiliaryData.*;

/**
 * Used by {@link DirectorySpan} to show main categories.
 */
public enum DirectoryType implements DetailStyle{
    /// Enum value order mandated by LinedParsePointer and in interface
    COMMENT(TYPE_COMMENT, "", ""), NOTE(TYPE_NOTE, CURLY_CITE), 
    FOOTNOTE(TYPE_FOOTNOTE, CURLY_FOOTNOTE), 
    ENDNOTE(TYPE_ENDNOTE, CURLY_ENDNOTE), LINK(TYPE_LINK, LINK_REF, LINK_END);

    private final String startString;
    private final String endString;
    private final String baseCategory;

    private DirectoryType(String category, String start){
        this (category, start, CURLY_END);
    }
    
    private DirectoryType(String category, String start, String end){
        baseCategory = category;
        startString = start;
        endString = end;
    }
    
    public String getStart(){
        return startString;
    }
    
    public String getEnd(){
        return endString;
    }

    public String getCategory(){
        return baseCategory;
    }

    @Override
    public String getStyleClass(){
        return DetailStyle.styleFromEnum(STYLE_CATEGORY, name());
    }
}
