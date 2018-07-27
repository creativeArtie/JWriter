package com.creativeartie.writerstudio.export;

import java.util.*;

import static com.creativeartie.writerstudio.main.ParameterChecker.*;

/** Export a span of text */
final class ExportContentText<T extends Number> {

    private final BridgeContent inputText;
    private final DataLineType lineType;
    private final RenderContent<T> contentRender;
    private String outputText;
    private boolean isStart;

    public ExportContentText(BridgeContent text, DataLineType type,
        RenderContent<T> render
    ){
        inputText = text;
        lineType = type;
        contentRender = render;
        outputText = text.getText();
        isStart = true;
    }

    private ExportContentText(ExportContentText<T> self, String text){
        contentRender = self.contentRender;
        inputText = self.inputText;
        lineType = self.lineType;
        outputText = text;
    }

    Optional<ExportContentText<T>> splitContent(T width){
        String[] text = contentRender.splitContent(this, width);
        stateCheck(text.length == 2, "Unexpected split length: " + text.length);
        outputText = text[0];
        if (text[1].length() > 0){
            return Optional.of(new ExportContentText<>(this, text[1]));
        }
        return Optional.empty();
    }

    String getText(){
        return outputText;
    }

    BridgeContent getStyle(){
        return inputText;
    }

    DataLineType getLineType(){
        return lineType;
    }

    boolean isEmpty(){
        return outputText.length() == 0;
    }

    public T getWidth(){
        return contentRender.calculateWidth(this);
    }

    public T getHeight(){
        return contentRender.calcuateHeight(this);
    }

    public RenderContent<T> getRender(){
        return contentRender;
    }

    @Override
    public String toString(){
        return "\"" + outputText + "\"";
    }
}