package com.creativeartie.writerstudio.pdf;

import com.creativeartie.writerstudio.file.*;
import com.creativeartie.writerstudio.lang.markup.*;

import com.creativeartie.writerstudio.pdf.value.*;

public interface Data{

    public static float inchToPoint(float inches){
        return inches * 72;
    }

    public static float cmToPoint(float cm){
        return cm * 28.3465f;
    }

    public DataWriting getBaseData();

    public default SizedFont getBaseFont(){
        return getBaseData().getBaseFont();
    }

    public default Margin getMargin(){
        return getBaseData().getMargin();
    }

    public default ManuscriptFile getOutputDoc(){
        return getBaseData().getOutputDoc();
    }

    public default WritingText getWritingText(){
        return getBaseData().getWritingText();
    }
}