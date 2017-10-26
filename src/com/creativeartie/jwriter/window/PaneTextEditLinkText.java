package com.creativeartie.jwriter.window;

import javafx.scene.layout.*;
import javafx.beans.property.*;
import javafx.scene.image.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.geometry.*;
import javafx.event.*;
import javafx.collections.*;
import javafx.collections.*;
import javafx.beans.binding.*;
import javafx.util.*;
import java.util.*;
import java.util.Optional;
import java.util.function.*;
import java.util.function.Function;

import com.creativeartie.jwriter.lang.*;
import com.creativeartie.jwriter.lang.markup.*;
import com.creativeartie.jwriter.main.*;

import com.google.common.collect.*;

class PaneTextEditLinkText extends PaneTextEditCommon{

    public PaneTextEditLinkText(){
        super(EditIcon.LINK_TEXT);
    }

    public void update(EditUpdated list){
        if(list.findBranch(FormatSpanLink.class).isPresent()){
            setDisable(false);
            setSelected(list.getLeafStyle() == SetupLeafStyle.TEXT);
        } else {
            setDisable(true);
            setSelected(false);
        }
    }

}