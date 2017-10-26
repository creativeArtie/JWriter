package com.creativeartie.jwriter.lang.markup;

import java.util.*;

import com.creativeartie.jwriter.lang.*;
import static com.creativeartie.jwriter.lang.markup.AuxiliaryData.*;
import com.creativeartie.jwriter.main.Checker;

/**
 * A {@link ContentSpan} with format for {@link FormatSpanMain}. 
 */
public class FormatSpanContent extends FormatSpan implements BasicText{
    
    /// Stuff for reparsing
    private final List<String> reparseEnders;
    private final SetupLeafStyle leafStyle;
    private final boolean willReparse;
    
    FormatSpanContent(List<Span> spanChildren, boolean[] formats, 
        List<String> enders, SetupLeafStyle style, boolean reparse
    ){
        super(spanChildren, formats);
        reparseEnders = enders;
        leafStyle = style;
        willReparse = reparse;
    }
    
    /* // TODO Speed up preformance by edit only some of the text
    @Override
    protected DetailUpdater getUpdater(int index, String text){
        Checker.checkNotNull(text, "text");
        if(willReparse && search(text, CHAR_ESCAPE, reparseEnders) == -1){
            return DetailUpdater.replace(new FormatParseContent(leafStyle, 
                getFormats(), willReparse, reparseEnders));
        }
        return DetailUpdater.unable();
    }
    */
    
    @Override
    public String getOutput(){
        return getText();
    }
}
