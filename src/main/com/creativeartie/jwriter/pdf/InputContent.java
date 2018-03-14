package com.creativeartie.jwriter.pdf;

import java.util.*;
import com.creativeartie.jwriter.file.*;
import com.creativeartie.jwriter.resource.*;
import com.creativeartie.jwriter.lang.markup.*;

import com.creativeartie.jwriter.pdf.value.*;

public final class InputContent implements Input{
    private InputWriting baseData;

    public InputContent(InputWriting InputWriting){
        baseData = InputWriting;
    }

    public InputWriting getBaseData(){
        return baseData;
    }
}