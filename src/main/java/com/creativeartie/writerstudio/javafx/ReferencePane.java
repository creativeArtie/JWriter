package com.creativeartie.writerstudio.javafx;

import javafx.collections.*;
import javafx.scene.control.*;

import com.creativeartie.writerstudio.lang.markup.*;
import com.creativeartie.writerstudio.resource.*;

class ReferencePane extends TableView<ReferenceData>{

    ReferencePane(){
        setFixedCellSize(30);
        buildColumns();
        fillRows();
    }

    @SuppressWarnings("unchecked") /// For getColumns().addAdd(TableColumn ...)
    private void buildColumns(){
        TableColumn<ReferenceData, String> name = TableDataHelper
            .getTextColumn(WindowText.REF_NAME, d ->
                d.referenceNameProperty(), WindowText.EMPTY_NA);
        TableDataHelper.setPrecentWidth(name, this, 20.0);

        TableColumn<ReferenceData, String> id = TableDataHelper
            .getTextColumn(WindowText.REF_ID, d ->
                d.referenceIdProperty(), WindowText.EMPTY_NA);
        TableDataHelper.setPrecentWidth(id, this, 20.0);

        TableColumn<ReferenceData, String> describe = TableDataHelper
            .getTextColumn(WindowText.REF_LONG, d ->
                d.referenceDescriptionProperty(), WindowText.EMPTY_NA);
        TableDataHelper.setPrecentWidth(describe, this, 50.0);

        TableColumn<ReferenceData, String> example = TableDataHelper
            .getTextColumn(WindowText.REF_EXAMPLE, d ->
                d.referenceExampleProperty(), WindowText.EMPTY_NA);
        TableDataHelper.setPrecentWidth(example, this, 10.0);

        getColumns().addAll(name, id, describe, example);
    }

    private void fillRows(){
        ObservableList<ReferenceData> data = FXCollections.observableArrayList();
        for (FormatTypeField type: FormatTypeField.values()){
            if (type != FormatTypeField.ERROR){
                data.add(new ReferenceData(type));
            }
        }
        setItems(data);
    }


}