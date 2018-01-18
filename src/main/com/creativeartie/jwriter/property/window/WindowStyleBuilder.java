package com.creativeartie.jwriter.property.window;

import java.util.*;
import java.io.*;
import javafx.scene.*;
import javafx.scene.text.*;
import javafx.scene.control.*;

import static com.google.common.base.CaseFormat.*;
import static com.google.common.base.Preconditions.*;

import com.creativeartie.jwriter.lang.markup.*;
import com.creativeartie.jwriter.lang.Span;
import com.creativeartie.jwriter.main.*;
import com.creativeartie.jwriter.property.*;
public final class WindowStyleBuilder{
    private List<StyleProperty> properties;

    public WindowStyleBuilder(){
        properties = new ArrayList<StyleProperty>();
    }

    public WindowStyleBuilder add(String base, String name){
        return add(base + "." + UPPER_UNDERSCORE.to(UPPER_CAMEL, name));
    }

    public WindowStyleBuilder add(String key){
        properties.add(WindowStyle.buildStyle(key));
        return this;
    }

    public WindowStyleBuilder add(WindowStyle style){
        properties.add(style.getProperty());
        return this;
    }

    @Override
    public String toString(){
        return StyleProperty.toCss(properties);
    }
}