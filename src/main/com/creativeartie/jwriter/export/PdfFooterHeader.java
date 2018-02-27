package com.creativeartie.jwriter.export;

import java.io.*;
import java.util.*;
import java.util.function.*;
import com.google.common.collect.*;

import static com.creativeartie.jwriter.main.Checker.*;
import com.creativeartie.jwriter.lang.markup.*;
import com.creativeartie.jwriter.file.*;
import com.creativeartie.jwriter.lang.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.*;
import com.itextpdf.layout.renderer.*;
import com.itextpdf.kernel.geom.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.List;
import com.itextpdf.io.font.constants.*;
import com.itextpdf.layout.property.*;
import com.itextpdf.kernel.pdf.action.*;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.events.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.*;
import com.itextpdf.kernel.pdf.canvas.draw.*;

class PdfFooterHeader implements IEventHandler{
    private Optional<Div> curHeader;
    private Optional<Div> curFootnote;
    private Optional<Div> lastFootnote;
    private final PdfDocument pdfDocument;
    private final Document pdfMargin;

    PdfFooterHeader(Document margin, PdfDocument doc){
        pdfDocument = doc;
        lastFootnote = Optional.empty();
        curFootnote = Optional.empty();
        pdfMargin = margin;
        curHeader = Optional.empty();
    }

    public void setHeader(Div div){
        curHeader = Optional.of(div);
    }

    public void handleEvent(Event event){
        if (lastFootnote.isPresent()){
            Div adding = lastFootnote.get();
            PdfDocumentEvent use = (PdfDocumentEvent) event;
            PdfPage page = use.getPage();
            Rectangle size = page.getPageSize();
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(),
                page.getResources(), pdfDocument);
            float footnotes = getFootnotesHeight(lastFootnote);
            new Canvas(canvas, pdfDocument,  new Rectangle(
                size.getX() + pdfMargin.getLeftMargin(),
                size.getY() + pdfMargin.getBottomMargin() + footnotes,
                size.getWidth() - pdfMargin.getLeftMargin()
                    - pdfMargin.getRightMargin(),
                footnotes
            )).add(lastFootnote.get());
        }
        lastFootnote = curFootnote;
        curFootnote = Optional.empty();
    }

    public boolean isFootnotePresent(){
        return curFootnote.isPresent();
    }

    public void addFootnoteLine(IBlockElement element){
        if (! curFootnote.isPresent()){
            curFootnote = Optional.of(new Div());
        }
        curFootnote.get().add(element);
    }

    float getFootnotesHeight(){
        return getFootnotesHeight(curFootnote);
    }

    private float getFootnotesHeight(Optional<Div> footnote){
        if (footnote.isPresent()){
            DivRenderer renderer = (DivRenderer) footnote.get()
                .createRendererSubTree();
            renderer.setParent(new Document(new PdfDocument(new PdfWriter(
                new ByteArrayOutputStream()))).getRenderer());
            return renderer.layout(new LayoutContext(
                new LayoutArea(0, PageSize.A4))).getOccupiedArea().getBBox()
                .getHeight();
        }
        return 0f;
    }
}