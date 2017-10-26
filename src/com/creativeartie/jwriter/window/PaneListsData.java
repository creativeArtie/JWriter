package com.creativeartie.jwriter.window;

import java.util.*;
import javafx.collections.*;
import javafx.beans.property.*;

import com.creativeartie.jwriter.lang.*;
import com.creativeartie.jwriter.lang.markup.*;
import static com.creativeartie.jwriter.lang.markup.AuxiliaryData.*;

import com.google.common.collect.*;

public class PaneListsData{
    public static final int NO_ID  = -2;
    public static final int SINGLE = -1;

    public static ObservableList<PaneListsData> extractData(
        Collection<CatalogueData> data, DirectoryType type)
    {
        ArrayList<PaneListsData> out = new ArrayList<>();
        for (CatalogueData load: data){
            int size = load.getIds().size();
            if (size == 0){
                out.add(new PaneListsData(load, NO_ID, type));
            } else if (size == 1){
                out.add(new PaneListsData(load, SINGLE, type));
            } else {
                for(int i = 0; i < size; i++){
                    out.add(new PaneListsData(load, i, type));
                }
            }
        }
        return FXCollections.observableArrayList(out);
    }

    public class IdentityData{
        private final String uniqueName;
        private final String uniqueTarget;
        private IdentityData(String id, String target){
            uniqueName = id;
            uniqueTarget = target;
        }

        public String getName() {
            return uniqueName;
        }

        public String getTarget() {
            return uniqueTarget;
        }
    }

    private final ReadOnlyStringWrapper catalogueCategory;
    private final ReadOnlyObjectWrapper<IdentityData> catalogueIdentity;
    private final ReadOnlyStringWrapper refText;
    private final ReadOnlyObjectWrapper<Optional<Range<Integer>>> spanLocation;
    private final ReadOnlyObjectWrapper<Optional<SpanBranch>> targetSpan;
    private final CatalogueIdentity targetId;
    private final int targetNum;

    private PaneListsData(CatalogueData data, int target, DirectoryType type){
        CatalogueIdentity id = data.getKey();
        targetId = id;
        targetNum = target;
        String categories = getCategories(id.getCategories());
        String name = id.getName();
        String pointer = target >= 0? "(" + target + ")" : "";
        String refs = buildRefText(type, categories, name);
        Optional<Range<Integer>> location = Optional.empty();
        Optional<SpanBranch> span = Optional.empty();
        switch(target){
            case NO_ID:
                break;
            case SINGLE:
                span = Optional.of(data.getTarget());
                location = Optional.of(span.get().getRange());
                break;
            default:
                span = Optional.of(data.getIds().get(target));
                location = Optional.of(span.get().getRange());
        }
        catalogueCategory = new ReadOnlyStringWrapper(categories);
        catalogueIdentity = new ReadOnlyObjectWrapper<>(new IdentityData(name,
            pointer));
        refText = new ReadOnlyStringWrapper(refs);
        spanLocation = new ReadOnlyObjectWrapper<>(location);
        targetSpan = new ReadOnlyObjectWrapper<>(span);
    }

    public ReadOnlyStringProperty categoryProperty(){
        return catalogueCategory.getReadOnlyProperty();
    }

    public String getCatalogueCategory(){
        return catalogueCategory.getValue();
    }

    public ReadOnlyObjectProperty<IdentityData> catalogueIdentityProperty(){
        return catalogueIdentity.getReadOnlyProperty();
    }

    public IdentityData getCatalogueIdentity(){
        return catalogueIdentity.getValue();
    }

    public ReadOnlyStringProperty refTextProperty(){
        return refText.getReadOnlyProperty();
    }

    public String getRefText(){
        return refText.getValue();
    }

    public ReadOnlyObjectProperty<Optional<Range<Integer>>> spanLocationProperty(){
        return spanLocation.getReadOnlyProperty();
    }

    public Optional<Range<Integer>> spanLocationCount(){
        return spanLocation.getValue();
    }

    public ReadOnlyObjectProperty<Optional<SpanBranch>> targetSpanProperty(){
        return targetSpan.getReadOnlyProperty();
    }

    public Optional<SpanBranch> getTargetSpan(){
        return targetSpan.getValue();
    }

    public CatalogueIdentity getIdentity(){
        return targetId;
    }

    public int getTarget(){
        return targetNum;
    }

    @SuppressWarnings("fallthrough")
    private static String getCategories(List<String> categories){
        int status = 0;
        String ans = "";
        for(String category: categories){
            switch (status){
            default:
                ans += DIRECTORY_CATEGORY;
                /** FALL THROUGH **/
            case 1:
                ans += category;
                /** FALL THROUGH **/
            case 0:
                status++;
            }
        }
        return ans;
    }

    private static String buildRefText(DirectoryType type, String category,
        String name)
    {
        if (type == DirectoryType.COMMENT){
            return "";
        }
        if (! category.isEmpty()){
            category += DIRECTORY_CATEGORY;
        }
        return type.getStart() + category + name + type.getEnd();
    }
}