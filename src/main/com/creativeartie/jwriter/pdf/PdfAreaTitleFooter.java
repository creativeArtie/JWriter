package com.creativeartie.jwriter.pdf;

import java.io.*;
import java.util.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;

import com.creativeartie.jwriter.pdf.value.*;

import com.google.common.collect.*;

/**
 * Prints the title page footer
 */
class PdfAreaTitleFooter extends PdfArea{
    private float baseMargins;
    private PDPage outputPage;
    private ArrayList<PdfBlock> outputLines;
    private float startX;
    private float startY;

    public PdfAreaTitleFooter(DataTitle file, OutputPdfFile doc) throws IOException{
        super(file, doc);
        baseMargins = file.getMargin();
        startY = baseMargins;
        startX = baseMargins;

        PDFont font = file.getBaseFontType();
        int size = file.getBaseFontSize();

        outputLines = file.getTitleBottomText(getWidth());
        for (PdfBlock line: outputLines){
            startY += line.getHeight();
        }
    }

    @Override
    public float getXLocation(){
        return startX;
    }

    @Override
    public float getYLocation(){
        return startY;
    }

    @Override
    protected List<PdfBlock> delegate(){
        return ImmutableList.copyOf(outputLines);
    }
}