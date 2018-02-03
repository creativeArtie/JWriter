package com.creativeartie.jwriter.window;

import java.util.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.beans.property.*;
import javafx.beans.binding.*;
import javafx.scene.control.*;
import org.fxmisc.richtext.model.*;
import javafx.animation.*;

import com.creativeartie.jwriter.main.*;
import com.creativeartie.jwriter.file.*;
import com.creativeartie.jwriter.lang.*;
import com.creativeartie.jwriter.lang.markup.*;
import com.creativeartie.jwriter.resource.*;

import com.google.common.collect.*;

abstract class WriterTabView extends TabPane{
    private List<TableDataControl<?>> tableTabs;

    public WriterTabView(){
        tableTabs = Arrays.asList(
            initAgendaPane(), initLinksPane()
        );
    }

    private TableAgendaPane initAgendaPane(){
        TableAgendaPane ans = new TableAgendaPane();
        Tab tab = new Tab(WindowText.TAB_AGENDA.getText(), ans);
        getTabs().add(tab);
        return ans;
    }

    private TableLinkPane initLinksPane(){
        TableLinkPane ans = new TableLinkPane();
        Tab tab = new Tab(WindowText.TAB_LINK.getText(), ans);
        getTabs().add(tab);
        return ans;
    }

    public List<TableDataControl<?>> getTableTabs(){
        return tableTabs;
    }

    public TableAgendaPane getAgendaPane(){
        return (TableAgendaPane) tableTabs.get(0);
    }

    public TableLinkPane getLinksPane(){
        return (TableLinkPane) tableTabs.get(1);
    }
}