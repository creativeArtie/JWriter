package com.creativeartie.writerstudio.export;

import java.io.*; // IOException
import java.util.*; // ArrayList, LinkedList, Optional

import com.creativeartie.writerstudio.file.*; // ManuscriptFile
import com.creativeartie.writerstudio.lang.markup.*; // (many)
import com.creativeartie.writerstudio.export.value.*; // LineAlginment, PageAignment, Utitlies

import static com.creativeartie.writerstudio.main.Checker.*;

/** A {@link SectionContent} for main contents.
 */
public class SectionContentMain extends SectionContent<LinedSpan> {
    private boolean paraFirst;
    private LinkedList<Integer> listNumbering;

    /** Only construcutor.
     *
     * @param parent
     *      input parent data
     * @throws IOException
     *      exceptions thrown from uses of other classes
     */
    public SectionContentMain(WritingExporter parent) throws IOException{
        super(parent);
        paraFirst = true;
        listNumbering = new LinkedList<>();
    }

    @Override
    protected MatterArea parseHeader(ManuscriptFile data) throws IOException{
        checkNotNull(data, "data");
        MatterArea header = new MatterArea(getPage(), PageAlignment.TOP);
        header.addAll(DivisionTextFormatted.newPrintLines(this,
            data.getMetaData().getPrint(TextDataType.Area.MAIN_HEADER)
        ));
        return header;
    }

    @Override
    protected DivisionText parseSpan(LinedSpan span) throws IOException{
        checkNotNull(span, "span");
        boolean clear = true;
        DivisionText line = null;
        switch(span.getLinedType()){
        case BREAK:
            addBreak();
            return null;
        case BULLET:
            line = parseBullet((LinedSpanLevelList) span);
            break;
        case NUMBERED:
            line = parseNumber((LinedSpanLevelList) span);
            clear = false;
            break;
        case HEADING:
            line = parse((LinedSpanLevelSection) span);
            break;
        case PARAGRAPH:
            line = parse((LinedSpanParagraph) span);
            break;
        case QUOTE:
            line = parse((LinedSpanQuote) span);
        }
        /// reset list numbering as needed
        if (line != null && clear){
            listNumbering.clear();
        }
        return line;
    }

    /** Adds a break line
     *
     * @return answer
     * @throws IOException
     *      content fail to render
     * @see #parseSpan(LinedSpan)
     */
    private void addBreak() throws IOException{
        addLines(getOutputData().getMetaData().getPrint(TextDataType.Area.
            MAIN_BREAK));
        paraFirst = true;
    }

    /** Parse a bullet list line
     *
     * @param span
     *      the span to parse
     * @return answer
     * @throws IOException
     *      content fail to render
     * @see #parseSpan(LinedSpan)
     */
    private DivisionText parseBullet(LinedSpanLevelList span)
            throws IOException{
        assert span != null: "Null span";
        return parse(span, "•");
    }

    /** Parse a numbered list line
     *
     * @param span
     *      the span to parse
     * @return answer
     * @throws IOException
     *      content fail to render
     * @see #parseSpan(LinedSpan)
     */
    private DivisionText parseNumber(LinedSpanLevelList span)
            throws IOException{
        assert span != null: "Null span";

        int level = span.getLevel();
        /// line is in the parent list
        while (listNumbering.size() > level){
            listNumbering.pop();
        }
        /// line is in a sub list
        while (listNumbering.size() < level){
            listNumbering.push(1);
        }

        /// get the number out, parse it & push it back in
        int count = listNumbering.pop();
        DivisionText ans = parse(span, count + ".");
        listNumbering.push(count + (ans == null? 0: 1));
        return ans;
    }

    /** Parse a list line.
     *
     * @param span
     *      the span to parse
     * @param prefix
     *      the prefix to add
     * @throws IOException
     *      content fail to render
     * @return answer
     * @see #parseBullet(LinedSpanLevelList)
     * @see #parseNumber(LinedSpanLevelList)
     */
    private DivisionTextFormatted parse(LinedSpanLevelList span, String prefix)
            throws IOException{
        assert span != null: "Null span";

        Optional<FormattedSpan> format = span.getFormattedSpan();
        if (format.isPresent() && ! format.get().isEmpty()){
            DivisionTextFormatted line = newFormatDivision();
            /// calculate the indent of prefix & nested list
            float indent = Utilities.cmToPoint(.5f) +
                (Utilities.cmToPoint(.5f) * span.getLevel());

            /// set indent
            line.setFirstIndent(indent);
            line.setIndent(indent);

            /// set nested list
            line.setPrefix(ContentText.createWords(prefix, newFont()),
                indent - Utilities.cmToPoint(.75f));

            /// next paragraph will be indented
            paraFirst = false;

            return line.addContent(format.get());
        }
        return null;
    }

    /** Parse a section heading.
     *
     * @param span
     *      the span to parse
     * @throws IOException
     *      content fail to render
     * @return answer
     * @see #parseSpan(LinedSpan)
     */
    @SuppressWarnings("fallthrough")
    private DivisionText parse(LinedSpanLevelSection span) throws IOException{
        assert span != null: "Null span";

        Optional<FormattedSpan> format = span.getFormattedSpan();
        if (format.isPresent() && ! format.get().isEmpty()){
            /// no indent on the next paragraph
            paraFirst = true;
            DivisionTextFormatted ans = newFormatDivision();

            /// set different format for each type
            switch (span.getLevel()){
            case 1:
                nextPage(PageAlignment.THIRD);
                /// fall through
            case 2:
                ans.setLineAlignment(LineAlignment.CENTER);
                ans.setBottomSpacing(Utilities.cmToPoint(1f));
                break;
            case 3:
                ans.setBottomSpacing(Utilities.cmToPoint(.8f));
                ans.setLineAlignment(LineAlignment.CENTER);
                break;
            case 4:
                ans.setBottomSpacing(Utilities.cmToPoint(.6f));
                ans.setLineAlignment(LineAlignment.CENTER);
                break;
            case 5:
                ans.setBottomSpacing(Utilities.cmToPoint(.4f));
                /// fall through
            default:
                assert span.getLevel() == 6;
                ans.setLineAlignment(LineAlignment.CENTER);

            }
            return ans.addContent(format.get());
        }
        return null;
    }

    /** Parse a normal paragraph.
     *
     * @param span
     *      the span to parse
     * @throws IOException
     *      content fail to render
     * @return answer
     * @see #parseSpan(LinedSpan)
     */
    private DivisionText parse(LinedSpanParagraph span) throws IOException{
        assert span != null: "Null span";

        Optional<FormattedSpan> format = span.getFormattedSpan();

        if (format.isPresent() && ! format.get().isEmpty()){
            DivisionTextFormatted ans = newFormatDivision();
            /// add first indent as neccesary
            if (! paraFirst){
                ans.setFirstIndent(Utilities.cmToPoint(1.25f));
            } else {
                paraFirst = false;
            }

            return ans.addContent(format.get());
        }
        return null;
    }

    /** Parse a quote block.
     *
     * @param span
     *      the span to parse
     * @throws IOException
     *      content fail to render
     * @return answer
     * @see #parseSpan(LinedSpan)
     */
    private DivisionTextFormatted parse(LinedSpanQuote span)
            throws IOException{
        assert span != null: "Null span";

        Optional<FormattedSpan> format = span.getFormattedSpan();
        if (format.isPresent() && ! format.get().isEmpty()){
            /// create indents for the quote
            DivisionTextFormatted ans = newFormatDivision();
            ans.setWidth(ans.getWidth() - Utilities.cmToPoint(2f));
            ans.setFirstIndent(Utilities.cmToPoint(2f));
            ans.setIndent(Utilities.cmToPoint(2f));
            paraFirst = false;
            return ans.addContent(format.get());
        }
        return null;
    }

}