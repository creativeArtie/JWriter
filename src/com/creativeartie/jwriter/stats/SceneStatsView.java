package com.creativeartie.jwriter.stats;

import java.util.*;
import java.time.*;
import javafx.scene.layout.*;
import javafx.beans.property.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.*;
import java.io.*;

import com.creativeartie.jwriter.file.*;
import com.creativeartie.jwriter.main.*;
import com.creativeartie.jwriter.property.*;

public abstract class SceneStatsView extends BorderPane{
    protected static int WIDTH = 490;
    protected static int HEIGHT = 490;

    public static Stage createStage(SceneStatsControl pane){
        SceneStatsControl writer = new SceneStatsControl();
        Stage ans = new Stage();
        ans.setTitle(getString("Title"));
        ans.setResizable(false);
        ans.setScene(new Scene(pane, WIDTH, HEIGHT));
        ans.initModality(Modality.APPLICATION_MODAL);
        return ans;
    }

    private static String getString(String key){
        return Utilities.getString("Stats." + key);
    }

    private SimpleObjectProperty<RecordTable> statTable;
    private PaneMonthControl calendar;
    private Spinner<Integer> wordGoal;
    private Spinner<Integer> hourGoal;
    private Spinner<Integer> minuteGoal;

    public SceneStatsView(){
        getStylesheets().add("data/stats.css");

        statTable = new SimpleObjectProperty<>(this, "statTable");
        wordGoal = new Spinner<>(0, 10000, 100);
        hourGoal = new Spinner<>(0, 23, 0);
        minuteGoal = new Spinner<>(0, 59, 30);
        calendar = new PaneMonthControl();

        calendar.recordTableProperty().bind(statTable);
        statTable.addListener((data, oldValue, newValue) -> listenTable(newValue));
        wordGoal.focusedProperty().addListener((data, oldValue, newValue) ->
            listenWord(newValue));
        hourGoal.focusedProperty().addListener((data, oldValue, newValue) ->
            listenHour(newValue));
        minuteGoal.focusedProperty().addListener((data, oldValue, newValue) ->
            listenMinute(newValue));

        layoutCalender();
        layoutGoal();
    }

    /// Getters
    public Spinner<Integer> getWordGoal(){
        return wordGoal;
    }

    public Spinner<Integer> getHourGoal(){
        return hourGoal;
    }

    public Spinner<Integer> getMinuteGoal(){
        return minuteGoal;
    }

    /// Layout Node
    private void layoutCalender(){
        calendar.setMaxWidth(WIDTH);
        setCenter(calendar);
    }

    private void layoutGoal(){
        GridPane info = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(30);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70);
        info.getColumnConstraints().addAll(column1, column2);

        info.add(new Label(getString("WordGoal")), 0, 0);
        wordGoal.setEditable(true);
        info.add(wordGoal, 1, 0);
        FlowPane pane = new FlowPane();
        info.add(new Label(getString("TimeGoal")), 0, 1);
        minuteGoal.setEditable(true);
        minuteGoal.setPrefWidth(100.0);
        hourGoal.setEditable(true);
        hourGoal.setPrefWidth(100.0);
        pane.getChildren().addAll(hourGoal, new Label(getString("Hours")),
            minuteGoal, new Label(getString("Minutes")));
        info.add(pane, 1, 1);
        setBottom(info);
    }


    /// Node Properties
    public ObjectProperty<RecordTable> statTableProperty(){
        return statTable;
    }

    public RecordTable getStatTable(){
        return statTable.getValue();
    }

    public void setStatTable(RecordTable doc){
        statTable.setValue(doc);
    }

    /// Control Methods
    protected abstract void listenTable(RecordTable table);

    protected abstract void listenWord(boolean value);

    protected abstract void listenHour(boolean value);

    protected abstract void listenMinute(boolean value);
}