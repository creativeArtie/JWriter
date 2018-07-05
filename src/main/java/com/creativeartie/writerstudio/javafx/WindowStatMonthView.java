package com.creativeartie.writerstudio.javafx;

import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.time.*;

import java.util.*;

import com.creativeartie.writerstudio.lang.*;
import com.creativeartie.writerstudio.lang.markup.*;
import com.creativeartie.writerstudio.main.*;

import com.creativeartie.writerstudio.resource.*;

abstract class WindowStatMonthView extends GridPane{
    /// %Part 1: Constructor and Class Fields

    private Button firstButton;
    private Button pastButton;
    private Button nextButton;
    private Button endButton;
    private Label yearMonthLabel;
    private WindowStatDayControl[] dayPanes;

    private SimpleObjectProperty<YearMonth> currentMonth;

    WindowStatMonthView(){
        setColumnConstraints();
        add(buildPastNav(), 0 ,0, 2, 1);
        add(buildMonthTitle(), 2, 0, 3, 1);
        add(buildNextNav(), 5, 0, 2, 1);

        int col = 0;
        for(DayOfWeek day: DayOfWeek.values()){
            add(buildDayLabel(day), col, 1);
            col++;
        }

        buildDayPanes();

        currentMonth = new SimpleObjectProperty<>(this, "currentMonth");
    }

    /// %Part 2: Layout

    private void setColumnConstraints(){
        ArrayList<ColumnConstraints> columns = new ArrayList<>();
        int size = DayOfWeek.values().length;
        for(int i = 0; i < size; i++){
            ColumnConstraints column = new ColumnConstraints();
            column.setPrefWidth(WindowStatView.WIDTH / size);
            column.setMaxWidth(WindowStatView.WIDTH / size);
            columns.add(column);
        }
        getColumnConstraints().addAll(columns);
    }

    private FlowPane buildPastNav(){
        firstButton = new Button();
        firstButton.setGraphic(ButtonIcon.START_MONTH.getIcon());

        pastButton = new Button();
        pastButton.setGraphic(ButtonIcon.PAST_MONTH.getIcon());

        FlowPane pane = new FlowPane(firstButton, pastButton);
        pane.setAlignment(Pos.BOTTOM_LEFT);
        return pane;
    }

    private StackPane buildMonthTitle(){
        yearMonthLabel = new Label();
        yearMonthLabel.getStyleClass().add("month");
        return new StackPane(yearMonthLabel);
    }

    private FlowPane buildNextNav(){
        nextButton = new Button();
        nextButton.setGraphic(ButtonIcon.NEXT_MONTH.getIcon());
        endButton = new Button();
        endButton.setGraphic(ButtonIcon.END_MONTH.getIcon());

        FlowPane pane = new FlowPane(nextButton, endButton);
        pane.setAlignment(Pos.BOTTOM_RIGHT);
        return pane;
    }

    private StackPane buildDayLabel(DayOfWeek day){
        Label header = new Label(WindowText.getText(day));
        header.getStyleClass().add("weekday");

        StackPane pane = new StackPane(header);
        pane.getStyleClass().add("weekday-box");
        return pane;
    }

    private void buildDayPanes(){
        dayPanes = new WindowStatDayControl[7 * 6];
        for (int i = 0; i < dayPanes.length; i++){
            dayPanes[i] = new WindowStatDayControl();
        }

        int ptr = 0;
        for (int i = 0; i < 6; i++){
            for (int j = 0; j < 7; j++){
                add(dayPanes[ptr++], j, i + 2);
            }
        }
    }

    /// %Part 3: Setup Properties

    public void setupProperties(WindowStatControl control){
        setupChildern(control);
    }

    protected abstract void setupChildern(WindowStatControl control);

    /// %Part 4: Properties

    public SimpleObjectProperty<YearMonth> currentMonthProperty(){
        return currentMonth;
    }

    public YearMonth getCurrentMonth(){
        return currentMonth.getValue();
    }

    public void setCurrentMonth(YearMonth value){
        currentMonth.setValue(value);
    }

    /// %Part 5: Get Child Methods
    Button getFirstButton(){
        return firstButton;
    }

    Button getPastButton(){
        return pastButton;
    }

    Button getNextButton(){
        return nextButton;
    }

    Button getEndButton(){
        return endButton;
    }

    Label getYearMonthLabel(){
        return yearMonthLabel;
    }

    WindowStatDayControl[] getDayPanes(){
        return dayPanes;
    }
}