package com.creativeartie.writerstudio.lang.markup;

import static org.junit.Assert.*;
import static com.creativeartie.writerstudio.lang.DocumentAssert.*;
import static com.creativeartie.writerstudio.lang.markup.BranchLineTest.*;
import static com.creativeartie.writerstudio.lang.markup.BranchDataTest.*;
import static com.creativeartie.writerstudio.lang.markup.BranchTest.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.File;
import java.util.Optional;

import com.creativeartie.writerstudio.lang.*;

@RunWith(JUnit4.class)
public class LinedCiteDebug {

    private static final SetupParser[] parsers = new SetupParser[]{
            LinedParseCite.INSTANCE};

    @Test
    public void basicInText(){
        String raw = "!>in-text:a";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.IN_TEXT)
            .setDataSpan(doc, 0, 3).setNoteTotal(1);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.IN_TEXT);
        ContentDataTest data = new ContentDataTest()
            .setData(doc, 0, 3, 0);

        cite.test(        doc,  4, raw,       0);
        doc.assertKeyLeaf(  0,  2, "!>",      0, 0);
        field.test(       doc,  1, "in-text", 0, 1);
        doc.assertFieldLeaf(2,  9, "in-text", 0, 1, 0);
        doc.assertKeyLeaf(  9, 10, ":",       0, 2);
        data.test(        doc,  1, "a",       0, 3);
        doc.assertChild(    1,     "a",       0, 3, 0);
        doc.assertDataLeaf(10, 11, "a",       0, 3, 0, 0);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void inTextColonNewline(){
        String raw = "!>in-text:\n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.IN_TEXT);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.IN_TEXT);

        cite.test(        doc,  4, raw,       0);
        doc.assertKeyLeaf(  0,  2, "!>",      0, 0);
        field.test(       doc,  1, "in-text", 0, 1);
        doc.assertFieldLeaf(2,  9, "in-text", 0, 1, 0);
        doc.assertKeyLeaf(  9, 10, ":",       0, 2);
        doc.assertKeyLeaf(10, 11, "\n",       0, 3);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void inTextNoColon(){
        String raw = "!>in-text";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.IN_TEXT);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.IN_TEXT);

        cite.test(        doc,  2, raw,       0);
        doc.assertKeyLeaf(  0,  2, "!>",      0, 0);
        field.test(       doc,  1, "in-text", 0, 1);
        doc.assertFieldLeaf(2,  9, "in-text", 0, 1, 0);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void errorNoField(){
        String raw = "!>:a\n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.ERROR);

        cite.test(       doc, 4, raw,  0);
        doc.assertKeyLeaf( 0, 2, "!>", 0, 0);
        doc.assertKeyLeaf( 2, 3, ":",  0, 1);
        doc.assertTextLeaf(3, 4, "a",  0, 2);
        doc.assertKeyLeaf( 4, 5, "\n", 0, 3);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void inTextNoColonData(){
        String raw = "!>in-texta";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.IN_TEXT)
            .setDataSpan(doc, 0, 2).setNoteTotal(1);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.IN_TEXT);
        ContentDataTest data = new ContentDataTest()
            .setData(doc, 0, 2, 0);

        cite.test(        doc,  3, raw,       0);
        doc.assertKeyLeaf(  0,  2, "!>",      0, 0);
        field.test(       doc,  1, "in-text", 0, 1);
        doc.assertFieldLeaf(2,  9, "in-text", 0, 1, 0);
        data.test(        doc,  1, "a",       0, 2);
        doc.assertChild(    1,     "a",       0, 2, 0);
        doc.assertDataLeaf( 9, 10, "a",       0, 2, 0, 0);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void errorWithUnparsed(){
        ///           01 23456
        String raw = "!>\nabc";
        DocumentAssert doc = assertDoc(2, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.ERROR);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.ERROR);

        cite.test(      doc,  2, "!>\n", 0);
        doc.assertKeyLeaf(0,  2, "!>",   0, 0);
        doc.assertKeyLeaf(2,  3, "\n",   0, 1);
        doc.assertLast("abc");
        doc.assertIds();

    }

    @Test
    public void errorEmptyData(){
        String raw = "!>sdaf";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.ERROR);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.ERROR);

        cite.test(        doc,  2, raw,    0);
        doc.assertKeyLeaf(  0,  2, "!>",   0, 0);
        field.test(        doc, 1, "sdaf", 0, 1);
        doc.assertFieldLeaf(2,  6, "sdaf", 0, 1, 0);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void errorEmptyDataNewLine(){
        String raw = "!>sdaf\n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.ERROR);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.ERROR);

        doc.assertKeyLeaf(  0, 2, "!>",   0, 0);
        field.test(       doc, 1, "sdaf", 0, 1);
        doc.assertFieldLeaf(2, 6, "sdaf", 0, 1, 0);
        doc.assertKeyLeaf(  6, 7, "\n",   0, 2);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void errorEmptyDataWithColon(){
        String raw = "!>sdaf:\n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.ERROR);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.ERROR);

        doc.assertKeyLeaf(  0,  2, "!>",   0, 0);
        field.test(        doc, 1, "sdaf", 0, 1);
        doc.assertFieldLeaf(2,  6, "sdaf", 0, 1, 0);
        doc.assertKeyLeaf(  6,  7, ":",    0, 2);
        doc.assertKeyLeaf(  7,  8, "\n",   0, 3);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void errorEmptyDataWithExraSpaces(){
        String raw = "!>sdaf:  \n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.ERROR);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.ERROR);

        doc.assertKeyLeaf(  0, 2, "!>",   0, 0);
        field.test(       doc, 1, "sdaf", 0, 1);
        doc.assertFieldLeaf(2, 6, "sdaf", 0, 1, 0);
        doc.assertKeyLeaf(  6, 7, ":",    0, 2);
        doc.assertTextLeaf( 7, 9, "  ",   0, 3);
        doc.assertKeyLeaf(  9, 10, "\n",  0, 4);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void errorUsingError(){
        String raw = "!>error:text\n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.ERROR);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.ERROR);

        cite.test(        doc,  5, raw,     0);
        doc.assertKeyLeaf(  0,  2, "!>",    0, 0);
        field.test(       doc,  1, "error", 0, 1);
        doc.assertFieldLeaf(2,  7, "error", 0, 1, 0);
        doc.assertKeyLeaf(  7,  8, ":",     0, 2);
        doc.assertTextLeaf( 8, 12, "text",  0, 3);
        doc.assertKeyLeaf( 12, 13, "\n",    0, 4);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void basicFootnote(){
        String raw = "!>footnote:abc\\\n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.FOOTNOTE)
            .setDataSpan(doc, 0, 3).setNoteTotal(1);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.FOOTNOTE);
        FormatDataTest data = new FormatDataTest()
            .setData(doc, 0, 3, 0);

        cite.test(        doc,  4, raw,        0);
        doc.assertKeyLeaf(  0,  2, "!>",       0, 0);
        field.test(       doc,  1, "footnote", 0, 1);
        doc.assertFieldLeaf(2, 10, "footnote", 0, 1, 0);
        doc.assertKeyLeaf( 10, 11, ":",        0, 2);
        data.test(        doc,  1, "abc\\\n",  0, 3);
        doc.assertChild(        1, "abc\\\n",  0, 3, 0);
        doc.assertChild(        2, "abc\\\n",  0, 3, 0, 0);
        doc.assertDataLeaf(11, 14, "abc",      0, 3, 0, 0, 0);
        doc.assertChild(        2, "\\\n",     0, 3, 0, 0, 1);
        doc.assertKeyLeaf( 14, 15, "\\",       0, 3, 0, 0, 1, 0);
        doc.assertDataLeaf(15, 16, "\n",       0, 3, 0, 0, 1, 1);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void basicSource(){
        String find = "Henry** Reads**";
        String raw = "!>source:" + find + "\n";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.SOURCE)
            .setDataSpan(doc, 0, 3).setNoteTotal(2);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.SOURCE);
        FormatDataTest data = new FormatDataTest()
            .setData(doc, 0, 3, 0);

        cite.test(        doc,  5, raw,      0);
        doc.assertKeyLeaf(  0,  2, "!>",     0, 0);
        field.test(       doc,  1, "source", 0, 1);
        doc.assertFieldLeaf(2,  8, "source", 0, 1, 0);
        doc.assertKeyLeaf(  8,  9, ":",      0, 2);
        data.test(        doc,  1, find,     0, 3);
        doc.assertChild(        4, find,     0, 3, 0);
        doc.assertChild(        1, "Henry",  0, 3, 0, 0);
        doc.assertDataLeaf( 9, 14, "Henry",  0, 3, 0, 0, 0);
        doc.assertKeyLeaf( 14, 16, "**",     0, 3, 0, 1);
        doc.assertChild(        1, " Reads", 0, 3, 0, 2);
        doc.assertDataLeaf(16, 22, " Reads", 0, 3, 0, 2, 0);
        doc.assertKeyLeaf( 22, 24, "**",     0, 3, 0, 3);
        doc.assertKeyLeaf( 24, 25, "\n",     0, 4);
        doc.assertLast();
        doc.assertIds();
    }

    @Test
    public void editAddField(){
        String before = "!>:abc";
        DocumentAssert doc = assertDoc(1, before, parsers);
        doc.insert(2, "in-text", 0);
        editCommon(doc);
    }

    @Test
    public void editContent(){
        ///              0123456789012
        String before = "!>in-text:abec";
        DocumentAssert doc = assertDoc(1, before, parsers);
        doc.delete(12, 13, 0);
        ///             01234567890123
        String after = "!>in-text:abc";
        doc.assertDoc(1, after, parsers);
        editCommon(doc);
    }

    @Test
    public void editRemoveCite(){
        ///              0123456789012
        String before = "!>in-text:abec";
        DocumentAssert doc = assertDoc(1, before, parsers);
        doc.delete(0, 1);
        ///             01234567890123
        String after = ">in-text:abec";
        doc.assertDoc(1, after, parsers);

        doc.assertLast(after);
        doc.assertIds();
    }

    private void editCommon(DocumentAssert doc){
        ///             01234567890123
        String after = "!>in-text:abc";
        doc.assertDoc(1, after);

        CiteLineTest cite = new CiteLineTest()
            .setInfoType(InfoFieldType.IN_TEXT)
            .setDataSpan(doc, 0, 3).setNoteTotal(1);
        FieldTest field = new FieldTest()
            .setType(InfoFieldType.IN_TEXT);
        ContentDataTest data = new ContentDataTest()
            .setData(doc, 0, 3, 0);

        cite.test(        doc,  4, after,     0);
        doc.assertKeyLeaf(  0,  2, "!>",      0, 0);
        field.test(       doc,  1, "in-text", 0, 1);
        doc.assertFieldLeaf(2,  9, "in-text", 0, 1, 0);
        doc.assertKeyLeaf(  9, 10, ":",       0, 2);
        data.test(        doc,  1, "abc",     0, 3);
        doc.assertChild(    1,     "abc",     0, 3, 0);
        doc.assertDataLeaf(10, 13, "abc",     0, 3, 0, 0);
        doc.assertLast();
        doc.assertIds();
    }
}
