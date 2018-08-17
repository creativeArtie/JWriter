package com.creativeartie.writerstudio.javafx;

import java.util.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.property.*;
import javafx.scene.text.*;

import com.creativeartie.writerstudio.lang.*;
import com.creativeartie.writerstudio.lang.markup.*;
import com.creativeartie.writerstudio.resource.*;

class NoteCardDetailPaneControl extends NoteCardDetailPaneView{
    private ObjectProperty<SpanBranch> lastSelected;
    private BooleanProperty refocusText;

    private final Label footnoteLabel;
    private final Label inTextLabel;
    private final Label sourceLabel;

    private final Label noCardTitleLabel;
    private final Label noCardDetailLabel;

    private final Label noTitleTextLabel;
    private final Label noContentTextLabel;

    private final Button goToButton;

    NoteCardDetailPaneControl(){
        sourceLabel = new Label(WindowText.NOTE_CARD_SOURCE.getText());
        footnoteLabel = new Label(WindowText.NOTE_CARD_FOOTNOTE.getText());
        inTextLabel = new Label(WindowText.NOTE_CARD_IN_TEXT.getText());

        noCardTitleLabel = new Label(WindowText.NOTE_CARD_PLACEHOLDER_TITLE
            .getText());
        noCardDetailLabel = new Label(WindowText.NOTE_CARD_PLACHOLDER_DETAIL
            .getText());

        noTitleTextLabel = new Label(WindowText.NOTE_CARD_EMPTY_TITLE.getText());
        noContentTextLabel = new Label(WindowText.NOTE_CARD_EMTPY_DETAIL
            .getText());

        goToButton = new Button(WindowText.NOTE_CARD_EDIT.getText());

        StyleClass.NOT_FOUND.addClass(noCardTitleLabel);
        StyleClass.NOT_FOUND.addClass(noCardDetailLabel);
        StyleClass.NO_TEXT.addClass(noTitleTextLabel);
    }

    @Override
    protected void setupChildern(WriterSceneControl control){
        lastSelected = control.lastSelectedProperty();
        refocusText = control.refocusTextProperty();

        goToButton.setOnAction(evt -> goToCard());

        showCardProperty().addListener((d, o, n) -> showCard(n));

        clearContent();
    }

    private void goToCard(){
        lastSelected.setValue(getShowCard());
        refocusText.setValue(true);
    }

    private void showCard(NoteCardSpan show){
        if (show == null){
            clearContent();
        } else {
            showContent(show);
        }
    }

    private void clearContent(){
        setGraphic(noCardTitleLabel);
        setContent(noCardDetailLabel);
    }

    private void showContent(NoteCardSpan span){
        assert span != null: "Null span";
        /// Add the title
        Optional<FormattedSpan> title = span.getTitle();
        if (title.isPresent()){
            setGraphic(TextFlowBuilder.loadFormatText(span.getTitle()));
        } else {
            setGraphic(noTitleTextLabel);
        }

        /// Add the content
        Node content;
        Collection<FormattedSpan> lines = span.getContent();
        if (lines.isEmpty()){
            content = noContentTextLabel;
        } else {
            TextFlow text = new TextFlow();
            for (FormattedSpan line: lines){
                TextFlowBuilder.loadFormatText(text, Optional.of(line));
                text.getChildren().add(new Text("\n"));
            }
            ScrollPane pane = new ScrollPane(text);
            pane.setFitToHeight(true);
            pane.setFitToWidth(true);
            content = pane;
        }

        GridPane bottom = new GridPane();
        bottom.add(new StackPane(new Label(span.getLookupText())), 0, 2);
        bottom.add(new StackPane(goToButton), 1, 2);

        BorderPane ans = new BorderPane();
        ans.setCenter(content);
        ans.setBottom(bottom);
        setContent(ans);
    }
}
