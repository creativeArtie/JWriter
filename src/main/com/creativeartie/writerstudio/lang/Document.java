package com.creativeartie.writerstudio.lang;

import java.util.*; // (many)
import java.util.concurrent.*; // Callable, ExecutionException
import java.util.function.*; // Function

import com.google.common.cache.*; // Cache, CacheBuilder, LoadingCache,
import com.google.common.collect.*; // ImmmuableList, Range

import static com.creativeartie.writerstudio.main.Checker.*;

/**
 * Representation of a text file that is parsed by {@link SetupParser}.
 */
public abstract class Document extends SpanNode<SpanBranch>{

    /// Caches to reduce the need to recalculate data for each span.
    private final Cache<Span, Range<Integer>> spanRanges;
    private final LoadingCache<Integer, Integer[]> spanLocation;
    private final Cache<Span, List<SpanLeaf>> spanLeaves;
    private final Cache<Span, String> spanTexts;

    private ArrayList<SpanBranch> documentChildren;
    private final CatalogueMap catalogueMap;
    private SetupParser[] documentParsers;

    private ArrayList<SpanNode<?>> removeSpan;

    protected Document(String raw, SetupParser ... parsers){
        checkNotNull(raw, "raw");
        checkNotEmpty(parsers, "parser");
        removeSpan = new ArrayList<>();

        spanRanges = CacheBuilder.newBuilder().weakKeys().build();
        spanLeaves = CacheBuilder.newBuilder().weakKeys().build();
        spanTexts = CacheBuilder.newBuilder().weakKeys().build();
        documentParsers = parsers;
        spanLocation = CacheBuilder.newBuilder().maximumSize(1000)
            .build(CacheLoader.from(pos ->{
                int column = 0;
                int line = 1;
                String input = getRaw();
                for (int i = 0; i < pos; i++){
                    if (input.charAt(i) == '\n'){
                        column = 0;
                        line++;
                    } else {
                        column++;
                    }
                }
                return new Integer[]{column, line};
            }));

        catalogueMap = new CatalogueMap();
        /// Setup for building the doc and a pointer to use
        parseDocument(raw);

        /// Finalize the parse loop
        catalogueMap.clear();
        loadMap(documentChildren);
    }

    public void addReferences(Document ... docs){
        for (Document doc: docs){
            CatalogueMap map = doc.catalogueMap;
            // TODO work on span branches (no more editing will be doen to them)
            // TODO remove id references (not applicable)
            catalogueMap.add(map);
        }
    }

    /**
     * Parses the document. Helper method of
     * {@link #Document(String, SetupParsers...)}, {@link #insert(int, String},
     * and {@link #edit(Function, int}.
     */
    private final void parseDocument(String raw){
        assert raw != null: "Null raw.";

        documentChildren = new ArrayList<>();
        SetupPointer ptr = SetupPointer.newPointer(raw, this);
        /// Setup for runtime exceptions
        int counter = 0;

        /// Parse loop
        while (ptr.hasNext()){
            /// CatalogueStatus checking what happen if SetupPointer fails or
            /// SetupParser[] misses the texts
            if(counter > raw.length()){
                throw new RuntimeException("Loop too much: " + ptr);
            }
            counter++;

            /// Finding the correct SetupParser to build span from
            for(SetupParser s: documentParsers){
                Optional<?> span = s.parse(ptr);

                /// Span has been created
                if (span.isPresent()){
                    SpanBranch found = (SpanBranch)span.get();
                    found.setParent(this);
                    documentChildren.add(found);
                    break;
                }
            }
        }
    }

    final void updateDoc(){
        /// clear caches and data
        spanRanges.invalidateAll();
        spanLeaves.invalidateAll();
        spanTexts.invalidateAll();
        clearCache();
        for (SpanBranch span: this){
            span.clearCache();
        }

        catalogueMap.clear();
        loadMap(this);

        removeSpan.forEach(s -> s.fireRemoveListeners());
        removeSpan.clear();
        fireListeners();
    }

    final void removeSpan(SpanNode<?> span){
        removeSpan.add(span);
    }

    /**
     * Recursively update all child {@link Span spans}. Helper method of
     * {@link #parseDocument(String)} and {@link #updateEdit()}.
     */
    private final void loadMap(List<? extends Span> children){
        assert children != null: "Null children";
        for (Span child: children){
            /// Fill or refill {@link #catalogueMap}
            if (child instanceof SpanBranch){
                SpanBranch branch = (SpanBranch) child;
                catalogueMap.add(branch);
                loadMap(branch);
            }
        }
    }

    public final CatalogueMap getCatalogue(){
        return catalogueMap;
    }

    @Override
    public final Document getDocument(){
        return this;
    }

    @Override
    public final SpanNode<?> getParent(){
        throw new UnsupportedOperationException("No parents");
    }

    @Override
    protected final void setChildren(List<SpanBranch> children){
        children.forEach(s -> s.setParent(this));
        documentChildren = new ArrayList<>(children);
    }

    @Override
    public final List<SpanBranch> delegate(){
        return ImmutableList.copyOf(documentChildren);
    }

    @Override
    public final Range<Integer> getRange(){
        return getRangeCache(this, () -> Range.closedOpen(0, getLocalEnd()));
    }

    /**
     * Get a range in cache and wraps {@linkplain ExecutionException} with
     * {@link RuntimeException}.
     */
    final Range<Integer> getRangeCache(Span child,
            Callable<Range<Integer>> caller) {
        checkNotNull(child, "child");
        checkNotNull(caller, "caller function (caller)");

        try {
            return spanRanges.get(child, caller);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }
    /**
     * Get a text in cache and wraps {@linkplain ExecutionException} with
     * {@link RuntimeException}.
     */
    final String getTextCache(Span child, Callable<String> caller) {
        checkNotNull(child, "child");
        checkNotNull(caller, "caller");

        try {
            return spanTexts.get(child, caller);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    /**
     * Get a leave list in cache and wraps {@linkplain ExecutionException} with
     * {@link RuntimeException}.
     */
    final List<SpanLeaf> getLeavesCache(Span child,
            Callable<List<SpanLeaf>> caller){
        checkNotNull(child, "child");
        checkNotNull(caller, "caller");

        try {
            return spanLeaves.get(child, caller);
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    @Override
    public final List<SpanLeaf> getLeaves(){
        return getLeavesCache(this, () ->{
            ImmutableList.Builder<SpanLeaf> builder = ImmutableList.builder();
            for(SpanBranch span: this){
                /// span.getLeaves() might be cached, reduces the need to search
                builder.addAll(span.getLeaves());
            }
            return builder.build();
        });
    }

    /** Find column index. */
    final int getColumn(int index){
        try {
            return spanLocation.get(index)[0];
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /** Find line index. */
    final int getLine(int index){
        try {
            return spanLocation.get(index)[1];
        } catch (ExecutionException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    /** Locate a {@link Span} that is a instance of a certain class  */
    public final <T> Optional<T> locateSpan(int index, Class<T> clazz){
        checkRange(index, "index", 0, true, getEnd(), true);
        checkNotNull(clazz, "requested class (clazz).");

        /// Empty document
        if(getLocalEnd() == 0){
            return Optional.empty();
        }

        SpanNode<?> pointer = this;
        do {
            Span found = locateSpan(index, pointer);
            if (found instanceof SpanLeaf){
                /// Nothing found:
                return Optional.empty();
            } else if (clazz.isInstance(found)){
                /// Found and matched reqrested class
                return Optional.of(clazz.cast(found));
            }
            assert found instanceof SpanNode: "Wrong class: " + found.getClass();
            pointer = (SpanNode<?>) found;
        } while(true);

    }

    /** Locate a {@link SpanLeaf} */
     public final Optional<SpanLeaf> locateLeaf(int index){
        checkRange(index, "index", 0, true, getEnd(), true);
        if(getLocalEnd() == 0){
            return Optional.empty();
        }
        Span pointer = this;
        while (pointer instanceof SpanNode<?>){
            pointer = locateSpan(index, (SpanNode<?>) pointer);
        }
        return Optional.of((SpanLeaf)pointer);
    }

    /**
     * Located the span in a {@link SpanNode}. Helper method of
     * {@link #locateSapn(int, Class)}, and {@link #getLeaf(int)}.
     */
    private final Span locateSpan(int index, SpanNode<?> parent){
        assert parent != null: "Null parent";
        for (Span span: parent){
            if (span.getRange().contains(index)){
                return span;
            }
        }
        return parent.get(parent.size() - 1);
    }

    /** Insert a {@linkplain String} at a location.*/
    public synchronized final void insert(int location, String input){
        checkRange(location, "location", 0, true, getEnd(), true);
        checkNotNull(input, "input");
        if (isEmpty()){
            reparseDocument(input);
            return;
        }

        if (location == getLocalEnd()){
            /// Insert at the end
            Span span = get(size() - 1);
            while (span instanceof SpanBranch){
                SpanBranch child = (SpanBranch) span;
                span = child.get(child.size() - 1);
            }
            assert span instanceof SpanLeaf: "Wrong class.";
            span = span.getParent();
            while (span instanceof SpanBranch){
                if (((SpanBranch)span).editRaw(span.getRaw() + input)){
                    return;
                }
                span = span.getParent();
            }
            /// Reparse the whole document
            assert span instanceof Document: "Wrong class.";
            reparseDocument(getRaw() + input);
            return;
        }
        edit(span -> {
            StringBuilder text = new StringBuilder(span.getRaw());
            text.insert(location - span.getStart(), input);
            return text.toString();
        }, location);
    }

    /** Insert a {@linkplain String} at a location.*/
    public synchronized final void delete(int start, int end){
        checkRange(end, "end", 0, true, getEnd(), true);
        checkRange(start, "start", 0, true, end, true);

        edit(span -> {
            if (span.getEnd() >= end){
                String text = span.getRaw();
                text = text.substring(0,start - span.getStart()) +
                    text.substring(end - span.getStart(), text.length());
                return text.toString();
            }
            return "";
        }, start);
    }

    /**
     * Edit the document, excluding adding to the end of it. Helper function for
     * {@link #insert(int, String)}, and {@link #delete(int, int)}.
     */
    private void edit(Function<Span, String> editedText,
            int location){
        assert editedText != null: "Null editText";
        assert location >= 0 && location <= getEnd(): "Wrong location";
        Optional<SpanLeaf> found = getLeaf(location);

        if (! found.isPresent()){
            reparseDocument(editedText.apply(this));
            return;
        }

        SpanNode<?> span = found.get().getParent();

        /// Attempt to parse at a SpanBranch level
        while (span instanceof SpanBranch){
            String raw = editedText.apply(span);
            if (! raw.isEmpty()){
                /// edit is within the local text
                if (((SpanBranch)span).editRaw(raw)){
                    /// edit is completed
                    // ((SpanBranch)span).updateSpan();
                    return;
                }
            }
            span = span.getParent();
        }

        /// Must be parse at Document level
        assert span instanceof Document: "Wrong class:" + span.getClass();
        reparseDocument(editedText.apply(this));
        return;
    }

    private void reparseDocument(String text){
        forEach(s -> s.setRemove());
        parseDocument(text);
        updateSpan();
        updateDoc();
    }

    protected final void runCommand(Command command){
        parseDocument(command.getResult());
    }
}
