package com.creativeartie.jwriter.lang.markup;

import static org.junit.Assert.*;
import static com.creativeartie.jwriter.lang.DocumentAssert.*;
import static com.creativeartie.jwriter.lang.markup.BranchTest.*;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import org.junit.rules.*;

import java.io.File;
import java.util.Optional;

import java.util.Arrays;
import java.util.ArrayList;

import com.creativeartie.jwriter.lang.*;

@RunWith(JUnit4.class)
public class DirectoryDebug{

    static void assertBuildId(LinedSpan read, IDBuilder expected,
        CatalogueIdentity test)
    {
        if (expected == null){
            assertFalse("Identity should not be build.", test != null);
        } else {
            assertTrue("Identity should not be build", test != null);
            assertEquals(getError("create id", read), expected.build(), test);
        }
    }

    static void assertId(SpanBranch span, DirectoryType type,
        IDBuilder produces)
    {
        DirectorySpan test = assertClass(span, DirectorySpan.class);

        CatalogueIdentity id = produces.build();

        assertEquals(getError("purpose", test), type, test.getPurpose());
        assertEquals(getError("id",      test), id,   test.buildId());
    }

    private static final SetupParser[] parsers = new SetupParser[]{
        new DirectoryParser(DirectoryType.NOTE)};

    private static IDBuilder buildId(String id){
        return new IDBuilder().addCategory("note").setId(id);
    }

    @Test
    public void basic(){
        ///           012345
        String raw = "Hello";
        DocumentAssert doc  = assertDoc(1, raw, parsers);

        IDBuilder builder = buildId("hello");

        DirectoryTest id = new DirectoryTest()
            .setPurpose(DirectoryType.NOTE)
            .setIdentity(builder);
        ContentTest content = new ContentTest()
            .setText(raw) .setBegin(false)
            .setEnd(false).setCount(1);

        id.test(       doc, 1,            raw, 0);
        content.test(  doc, 1,            raw, 0, 0);
        doc.assertIdLeaf(0, raw.length(), raw, 0, 0, 0);

        doc.assertIds();
    }

    @Test
    public void singleCategory(){
        ///           0123456
        String raw = "cat-hi";
        DocumentAssert doc  = assertDoc(1, raw, parsers);

        IDBuilder builder = buildId("hi").addCategory("cat");

        DirectoryTest id = new DirectoryTest()
            .setPurpose(DirectoryType.NOTE)
            .setIdentity(builder);
        ContentTest content1 = new ContentTest()
            .setText("cat").setBegin(false)
            .setEnd(false) .setCount(1);

        ContentTest content2 = new ContentTest()
            .setText("hi").setBegin(false)
            .setEnd(false).setCount(1);

        id.test(        doc, 3, raw,   0);
        content1.test(  doc, 1, "cat", 0, 0);
        doc.assertIdLeaf( 0, 3, "cat", 0, 0, 0);
        doc.assertKeyLeaf(3, 4, "-",   0, 1);
        content2.test(  doc, 1, "hi",  0, 2);
        doc.assertIdLeaf( 4, 6, "hi",  0, 2, 0);

        doc.assertIds();
    }

    @Test
    public void twoSubcategories(){
        ///           0123456
        String raw = "a-b-c ";
        DocumentAssert doc  = assertDoc(1, raw, parsers);

        IDBuilder builder = buildId("c").addCategory("a", "b");

        DirectoryTest id = new DirectoryTest()
            .setPurpose(DirectoryType.NOTE)
            .setIdentity(builder);
        ContentTest content1 = new ContentTest()
            .setText("a") .setBegin(false)
            .setEnd(false).setCount(1);
        ContentTest content2 = new ContentTest()
            .setText("b") .setBegin(false)
            .setEnd(false).setCount(1);
        ContentTest content3 = new ContentTest()
            .setText("c").setBegin(false)
            .setEnd(true).setCount(1);

        id.test(        doc, 5, raw,  0);
        content1.test(  doc, 1, "a",  0, 0);
        doc.assertIdLeaf( 0, 1, "a",  0, 0, 0);
        doc.assertKeyLeaf(1, 2, "-",  0, 1);
        content2.test(  doc, 1, "b",  0, 2);
        doc.assertIdLeaf( 2, 3, "b",  0, 2, 0);
        doc.assertKeyLeaf(3, 4, "-",  0, 3);
        content3.test(  doc, 1, "c ", 0, 4);
        doc.assertIdLeaf( 4, 6, "c ", 0, 4, 0);

        doc.assertIds();
    }

    @Test
    public void emptySubcategory(){
        ///           01234
        String raw = "-see";
        DocumentAssert doc  = assertDoc(1, raw, parsers);

        IDBuilder builder = buildId("see");

        DirectoryTest id = new DirectoryTest()
            .setPurpose(DirectoryType.NOTE)
            .setIdentity(builder.addCategory(""));

        ContentTest content = new ContentTest()
            .setText("see").setBegin(false)
            .setEnd(false) .setCount(1);

        id.test(        doc, 2, raw,   0);
        doc.assertKeyLeaf(0, 1, "-",   0, 0);
        content.test(   doc, 1, "see", 0, 1);
        doc.assertIdLeaf( 1, 4, "see", 0, 1, 0);

        doc.assertIds();
    }

    @Test
    public void emptySecondSubcategory(){
        ///           012345678901234
        String raw = "yes  sir- -WEE";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        IDBuilder builder = buildId("wee").addCategory("yes sir" , "");

        DirectoryTest id = new DirectoryTest()
            .setPurpose(DirectoryType.NOTE)
            .setIdentity(builder);
        ContentTest content1 = new ContentTest()
            .setText("yes sir").setBegin(false)
            .setEnd(false)     .setCount(2);
        ContentTest content2 = new ContentTest()
            .setText("")   .setBegin(true)
            .setEnd(true)  .setCount(0);
        ContentTest content3 = new ContentTest()
            .setText("WEE").setBegin(false)
            .setEnd(false) .setCount(1);

        id.test(        doc,   5, raw,        0);
        content1.test(  doc,   1, "yes  sir", 0, 0);
        doc.assertIdLeaf (0,   8, "yes  sir", 0, 0, 0);
        doc.assertKeyLeaf(8,   9, "-",        0, 1);
        content2.test(  doc,   1, " ",        0, 2);
        doc.assertIdLeaf (9,  10, " ",        0, 2, 0);
        doc.assertKeyLeaf(10, 11, "-",        0, 3);
        content3.test(   doc,  1, "WEE",      0, 4);
        doc.assertIdLeaf (11, 14, "WEE",      0, 4, 0);

        doc.assertIds();
    }

    @Test
    public void categoryEscape(){
        ///            0123
        String raw = "\\-c";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        IDBuilder builder = buildId("-c");

        DirectoryTest id = new DirectoryTest()
            .setPurpose(DirectoryType.NOTE)
            .setIdentity(builder);
        ContentTest content = new ContentTest()
            .setText("-c").setBegin(false)
            .setEnd(false).setCount(1);
        EscapeTest escape = new EscapeTest().setEscape("-");

        id.test(        doc, 1, raw,    0);
        content.test(   doc, 2, "\\-c", 0, 0);
        escape.test(    doc, 2, "\\-",  0, 0, 0);
        doc.assertKeyLeaf(0, 1, "\\",   0, 0, 0, 0);
        doc.assertIdLeaf( 1, 2, "-",    0, 0, 0, 1);
        doc.assertIdLeaf( 2, 3, "c",    0, 0, 1);

        doc.assertIds();
    }

    @Test
    public void noId(){
        ///            0123
        String raw = "no-";
        DocumentAssert doc = assertDoc(1, raw, parsers);

        IDBuilder builder = buildId("").addCategory("no");

        DirectoryTest id = new DirectoryTest()
            .setPurpose(DirectoryType.NOTE)
            .setIdentity(builder);
        ContentTest content = new ContentTest()
            .setText("no").setBegin(false)
            .setEnd(false).setCount(1);

        id.test(        doc, 2, raw,  0);
        content.test(   doc, 1, "no", 0, 0);
        doc.assertIdLeaf( 0, 2, "no", 0, 0, 0);
        doc.assertKeyLeaf(2, 3, "-",  0, 1);

        doc.assertIds();
    }
}
