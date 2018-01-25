package com.creativeartie.jwriter.lang;

import java.util.*;
import java.util.function.*;
import com.google.common.collect.*;

import static com.creativeartie.jwriter.main.Checker.*;

/**
 * A {@link Span} storing {@link SpanLeaf} and {@link SpanBranch}.
 *
 * This implements some abstract methods left over from {@link SpanNode}, and
 * does the span editing.
 */
public abstract class SpanBranch extends SpanNode<Span> {

    private ArrayList<Span> spanChildren;

    private SpanNode<?> spanParent;

    private Optional<Document> spanDoc;

    private Optional<CatalogueStatus> spanStatus;

    public SpanBranch(List<Span> spans){
        spanChildren = setParents(spans);
        spanStatus = Optional.empty();
        clearLocalCache();
        clearDocCache();
    }

    /**
     * Set the children's parent to this. Helper method of
     * {@link #SpanBranch(List)} and {@link #editRaw(String)}.
     */
    private final ArrayList<Span> setParents(List<Span> spans){
        checkNotEmpty(spans, "spans");
        ArrayList<Span> ans = new ArrayList<>(spans);
        ans.forEach((span) -> {
            if (span instanceof SpanBranch){
                ((SpanBranch)span).setParent(this);
            } else {
                ((SpanLeaf)span).setParent(this);
            }
        });
        return ans;
    }

    @Override
    public final List<Span> delegate(){
        return ImmutableList.copyOf(spanChildren);
    }

    @Override
    public final Document getDocument(){
        return get(0).getDocument(); /// will eventually get to a SpanLeaf
    }

    @Override
    public final SpanNode<?> getParent(){
        return spanParent;
    }

    final void setParent(SpanNode<?> parent){
        spanParent = parent;
    }

    final void addCatalgoue(){
        if (this instanceof Catalogued){
            getDocument().getCatalogue().add((Catalogued)this);
        }
    }

    /** Get style information about this {@linkplain SpanBranch}.*/
    public abstract List<StyleInfo> getBranchStyles();

    @Override
    public final List<SpanLeaf> getLeaves(){
        return getDocument().getLeavesCache(this, () -> {
            /// Create the builder
            ImmutableList.Builder<SpanLeaf> builder = ImmutableList.builder();
            for(Span span: this){
                if (span instanceof SpanLeaf){
                    builder.add((SpanLeaf)span);
                } else if (span instanceof SpanBranch){
                    builder.addAll(((SpanBranch)span).getLeaves());
                }
            }
            return builder.build();
        });
    }

    /** Edit the children if this span can hold the entire text. */
    final boolean editRaw(String text){
        checkNotEmpty(text, "text");
        SetupParser parser = getParser(text);
        if (parser != null){
            /// It can be fully parsed.

            /// Removes the children
            removeChildren();

            /// Reparse text
            SetupPointer pointer = SetupPointer.updatePointer(text,
                getDocument());
            Optional<SpanBranch> found = parser.parse(pointer);

            assert ! pointer.hasNext(): "Has left over characters: " + pointer;
            assert found.isPresent(): "No children found.";

            found.ifPresent(span -> spanChildren = setParents(span));

            spanStatus = Optional.empty();
            return true;
       }
       return false;
    }

    void removeChildren(){
        if (this instanceof Catalogued){
            getDocument().getCatalogue().remove((Catalogued)this);
        }
        setRemove();
        for (Span child: this){
            child.setRemove();
            if (child instanceof SpanBranch){
                ((SpanBranch)child).removeChildren();
            }
        }
    }

    protected void idChanged(){
        if (this instanceof Catalogued){
            getDocument().getCatalogue().remove((Catalogued)this);
        } else if (getParent() instanceof SpanBranch){
            ((SpanBranch)getParent()).idChanged();
        }
    }

    /** Gets the parser only if it can reparsed the whole text. */
    protected abstract SetupParser getParser(String text);

    public final CatalogueStatus getIdStatus(){
        spanStatus = getCache(spanStatus, () -> {
            if (this instanceof Catalogued){
                Catalogued catalogued = (Catalogued) this;
                Optional<CatalogueIdentity> id = catalogued.getSpanIdentity();
                if (id.isPresent()){
                    return id.get().getStatus(getDocument().getCatalogue());
                }
            }
            return CatalogueStatus.NO_ID;
        });
        return spanStatus.get();
    }
}
