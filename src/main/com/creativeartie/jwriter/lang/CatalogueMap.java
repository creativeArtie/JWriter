package com.creativeartie.jwriter.lang;

import java.util.*;
import com.google.common.collect.*;
import static com.creativeartie.jwriter.main.Checker.*;

/**
 * A map of {@link CatalogueData} that is serachable by
 * {@link CatalogueIdentity}.
 */
public final class CatalogueMap extends ForwardingSortedMap<CatalogueIdentity,
        CatalogueData>{
    private final TreeMap<CatalogueIdentity, CatalogueData> idMap;

    /** {@linkplain CatalogueMap}'s constructor.*/
    CatalogueMap(){
        idMap = new TreeMap<>();
    }

    void addId(CatalogueIdentity id, SpanBranch span){
        checkNotNull(id, "id");
        checkNotNull(span, "span");

        CatalogueData data = idMap.get(id);
        if (data == null){
            data = new CatalogueData(this, id);
            idMap.put(id, data);
        }
        data.addId(span);
    }

    void addRef(CatalogueIdentity ref, SpanBranch span){
        checkNotNull(ref, "ref");
        checkNotNull(span, "span");

        CatalogueData data = idMap.get(ref);
        if (data == null){
            data = new CatalogueData(this, ref);
            idMap.put(ref, data);
        }
        data.addRef(span);
    }

    @Override
    public SortedMap<CatalogueIdentity, CatalogueData> delegate(){
        return ImmutableSortedMap.copyOf(idMap);
    }

    public TreeSet<SpanBranch> getIds(String base){
        TreeSet<SpanBranch> spans = new TreeSet<>(Comparator.comparing(
            span -> Optional.of(span)
                .filter(found -> found instanceof Catalogued)
                .flatMap(found -> ((Catalogued)found).getSpanIdentity())
                .orElseThrow(() -> new RuntimeException(
                    "Span is not of type Catalogued: " + span))
        ));
        for (Entry<CatalogueIdentity, CatalogueData> entry: idMap.entrySet()){
            if (entry.getKey().getBase().equals(base)){
                for (SpanBranch span: entry.getValue().getIds()){
                    spans.add(span);
                }
            }
        }
        return spans;
    }
}
