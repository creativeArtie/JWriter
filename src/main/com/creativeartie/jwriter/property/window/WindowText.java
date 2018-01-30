package com.creativeartie.jwriter.property.window;

import java.util.*;
import java.time.*;

import static com.google.common.base.CaseFormat.*;
import static com.google.common.base.Preconditions.*;

import com.creativeartie.jwriter.lang.markup.*;
import com.creativeartie.jwriter.lang.Span;
import com.creativeartie.jwriter.main.*;

public enum WindowText {
    PROGRAM_NAME("MainWindow.Title"),

    HEADING_NO_TEXT("DisplayHeading.HeadingNoText"),
    HEADING_PLACEHOLDER("DisplayHeading.HeadingPlaceholder"),

    MENU_FILE("MainMenu.File"),           MENU_FILE_NEW("MainMenu.FileCreate"),
    MENU_FILE_SAVE("MainMenu.FileSave"),  MENU_FILE_OPEN("MainMenu.FileOpen"),
    MENU_FILE_EXIT("MainMenu.FileExit"),  MENU_HELP("MainMenu.Help"),
    MENU_HELP_ABOUT("MainMenu.HelpAbout"),

    MENU_STATS("MainMenu.Stats"), MENU_STATS_GOALS("MainMenu.StatsGoal"),

    AGENDA_NOTEXT("AgendaTable.NoText"), AGENDA_LINE("AgenaTable.LineColumn"),
    AGENDA_TYPE("AgendaTable.TypeColumn"), AGENDA_SECTION("AgenaTable.SectionColumn"),
    AGENDA_TEXT("AgendaTable.TextColumn"),

    CALENDAR_NO_RECORD("Calendar.NoRecord"),

    HEADING_TITLE("TreeList.HeadingTitle"),
    OUTLINE_TITLE("TreeList.OutlineTitle"),

    GOALS_TITLE("StatsScreen.Title"),   GOAL_WORD_TEXT("StatsScreen.WordGoal"),
    GOALS_TIME_TEXT("StatsScreen.TimeGoal"), HOUR_UNIT("StatsScreen.HourUnit"),
    MINUTE_UNIT("StatsScreen.MinuteUnit"),

    LINK_TAB("NoteTabs.TitleLink"),

    ID_LIST_TITLE("UserLists.HeadingTypes"),
    NO_SPAN_LOC("UserLists.NoLocation"),
    NO_SPAN_FOUND("UserLists.NoItems"),
    NO_ID_CATEGORY("UserLists.NoCategory"),
    NO_SPAN_ID("UserLists.NoReference"),
    COLUMN_CAT("UserLists.ColumnCategory"),
    COLUMN_NAME("UserLists.ColumnName"),
    COLUMN_LOC("UserLists.ColumnLocation"),
    COLUMN_REF("UserLists.ColumnReference"),
    COLUMN_SPAN("UserLists.ColumnSpan"),

    FOOTNOTE_LABEL("NoteDisplay.FootnoteLabel"),
    IN_TEXT_LABEL("NoteDisplay.InTextLabel"),
    SOURCE_LABEL("NoteDisplay.SourceLabel"),
    NO_NOTE_TITLE("NoteDisplay.NoNoteTitle"),
    NO_NOTE_SELECTED("NoteDisplay.NoNoteSelected"),
    NO_NOTE_PLACEHOLDER("NoteDisplay.TitlePlaceholder"),
    NO_NOTE_FOUND("NoteDisplay.NoNoteTarget"),

    SYNTAX_MODE("WriteScene.SyntaxMode"),
    PARSED_MODE("WriteScene.ParsedMode"),

    ABOUT_TITLE("AboutWindow.Title"), ABOUT_LICENSE("AboutWindow.License"),
    ABOUT_ITEXT("AboutWindow.IText"), ABOUT_RICH_TEXT("AboutWindow.RichText"),
    ABOUT_GUAVA("AboutWindow.Guava"), ABOUT_LIBRARIES("AboutWindow.ThirdParty");

    public static String getText(DirectoryType type){
        return getDisplay(getText("UserLists.", type.name()) + "ListName");
    }

    public static String getText(ButtonIcon icon){
        return getDisplay(getText("Icon.", icon.name()));
    }

    public static String getText(Month month){
        return getDisplay(getText("Calendar.", month.name()));
    }
    public static String getText(DayOfWeek day){
        return getDisplay(getText("Calendar.", day.name()));
    }

    public static String getText(EditionType type){
        return getDisplay(getText("DisplayHeading.Edition", type.name()));
    }

    private static String getText(String base, String name){
        return base + UPPER_UNDERSCORE.to(UPPER_CAMEL, name);
    }

    private static String getDisplay(String key){
        return getWindowText().getString(key);
    }

    private String bundleKey;
    private WindowText(String key){
        bundleKey = key;
    }

    private static ResourceBundle texts;
    private static ResourceBundle getWindowText(){
        if (texts == null){
            texts = PropertyResourceBundle.getBundle("data.displayText",
                Locale.ENGLISH);
        }
        return texts;
    }

    public String getText(){
        return getDisplay(bundleKey);
    }
}
