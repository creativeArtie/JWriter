package com.creativeartie.writerstudio.window;

import java.util.*;
import java.time.*;
import java.time.format.*;
import javafx.scene.control.*;
import javafx.beans.property.*;

import org.fxmisc.richtext.*;

import com.google.common.base.*;

import com.creativeartie.writerstudio.file.*;
import com.creativeartie.writerstudio.main.*;
import com.creativeartie.writerstudio.lang.*;
import com.creativeartie.writerstudio.lang.markup.*;
import com.creativeartie.writerstudio.resource.*;

/**
 * Controller for the main text area.
 *
 * @see TextPaneView
 */
class TextPaneControl extends TextPaneView {

    @Override
    void updateTime(Label show){
        String text = DateTimeFormatter.ofPattern("HH:mm:ss")
            .format(LocalTime.now());
        show.setText(text);
    }

    void updateStats(Record record){
        int wordCount = record.getPublishTotal();
        double wordPrecent = (wordCount / (double) record.getPublishGoal()) * 100;

        Duration timer = record.getWriteTime();
        long hours = timer.toHours();
        long minutes = timer.toMinutes() % 60;
        long seconds = timer.getSeconds() % 60;
        double timePrecent = (timer.getSeconds() / (double) record.getTimeGoal()
            .getSeconds()) * 100;
        String text = String.format(
            "Publish: %d (%#.2f%%); Time: %d:%02d:%02d (%#.2f%%)",
            wordCount, wordPrecent, hours, minutes, seconds, timePrecent);
        getCurrentStatsLabel().setText(text);
    }

    void loadDocumentText(WritingText writing){
        getTextArea().replaceText(0, getTextArea().getLength(),
            writing.getRaw());
        setStyle(writing.getLeaves());
    }

    void setStyle(Collection<SpanLeaf> leaves){
        leaves.stream().filter(leaf -> leaf.isInUsed())
            .forEach(leaf -> getTextArea().setStyle(leaf.getStart(),
                leaf.getEnd(), CodeStyleBuilder.toCss(leaf))
            );
    }


    public void moveTo(int position){
        if (position == getTextArea().getLength()){
            getTextArea().end(NavigationActions.SelectionPolicy.CLEAR);
        } else {
            getTextArea().moveTo(position);
        }
    }

    public void returnFocus(){
        getTextArea().requestFollowCaret();
        getTextArea().requestFocus();
    }

    @Override
    public WindowText setNextMode(WindowText last){
        WindowText ans = null;
        if (last == null){
            ans = WindowText.SYNTAX_MODE;
        } else {
            switch (last){
                case SYNTAX_MODE:
                    ans = WindowText.PARSED_MODE;
                    break;
                case PARSED_MODE:
                    ans = WindowText.SYNTAX_MODE;
                    break;
            }
        }
        getViewModeButton().setText(ans.getText());
        return ans;
    }

    @Override
    void updatePosition(ReadOnlyIntegerWrapper caret){
        if (isReady()){
            caret.setValue(getTextArea().getCaretPosition());
        }
    }
}