package com.creativeartie.jwriter.lang;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

import static com.creativeartie.jwriter.lang.DocumentAssert.*;

public abstract class SpanBranchAssert<T extends SpanBranchAssert>{
    private Optional<CatalogueIdentity> expectId;
    private CatalogueStatus expectStatus;
    private DetailStyle[] expectStyles;
    private boolean isCatalogued;
    private Class<T> cast;


    public SpanBranchAssert(Class<T> clazz){
        cast = clazz;
        expectStatus = CatalogueStatus.NO_ID;
        isCatalogued = false;
        expectId = Optional.empty();
        expectStyles = new DetailStyle[0];
    }

    public T cast(){
        return cast.cast(this);
    }

    public T setCatalogued(CatalogueStatus status){
        return setCatalogued(status, null);
    }

    public T setCatalogued(CatalogueStatus status, IDBuilder builder){
        expectId = Optional.ofNullable(builder).map(found -> found.build());
        expectStatus = status;
        isCatalogued = true;
        return cast();
    }

    public void setCatalogued(){
        isCatalogued = true;
    }

    public boolean isCatalogued(){
        return isCatalogued;
    }

    public CatalogueStatus getCatalogueStatus(){
        return expectStatus;
    }

    public void setStyles(DetailStyle ... styles){
        expectStyles = styles;
    }

    public void addStyles(DetailStyle ... styles){
        DetailStyle[] tmp = new DetailStyle[expectStyles.length + styles.length];
        System.arraycopy(expectStyles, 0, tmp, 0, expectStyles.length);
        System.arraycopy(styles, 0, tmp, expectStyles.length, styles.length);
        expectStyles = tmp;
    }

    public void setup(){}

    public SpanBranch test(DocumentAssert doc, int size, String rawText,
        int ... idx)
    {
        setup();
        SpanBranch span = doc.assertChild(size, rawText, idx);
        assertArrayEquals(getError("styles", span), expectStyles, span
            .getBranchStyles().toArray());

        assertEquals(getError("status", span), expectStatus, span.getIdStatus());
        if (isCatalogued){
            assertTrue("Not implmeneted Catalogued: " + span,
                span instanceof Catalogued);

            Optional<CatalogueIdentity> test = ((Catalogued) span)
                .getSpanIdentity();
            if (expectId.isPresent()){
                CatalogueIdentity expected = expectId.get();

                assertTrue("Id should be found", test.isPresent());
                assertEquals(getError("id", span), expected, test.get());
            } else {
                assertFalse("ID should be empty", test.isPresent());
            }
        }
        test(span);
        return span;
    }

    protected static <T> T assertClass(SpanBranch span, Class<T> clazz){
        assertEquals(getError("class", span), clazz, span.getClass());
        return clazz.cast(span);
    }

    protected <T> void assertSpan(String field, SpanBranch span,
        Optional<T> base, Optional<T> expect)
    {
        assertSpan(field, span, base.orElse(null), expect);
    }

    protected <T> void assertSpan(String field, SpanBranch span, T test,
        Optional<T> expect)
    {
        if (expect.isPresent()){
            assertNotNull(getError(field, span), test);
            assertSame(getError(field, span), test, expect.get());
        } else {
            assertNull(getError(field, span), test);
        }
    }

    public static String getError(String name, Object test){
        return "Wrong " + name + " for " + test.toString();
    }

    protected abstract void test(SpanBranch branch);
}