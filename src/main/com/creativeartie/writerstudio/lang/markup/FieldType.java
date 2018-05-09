package com.creativeartie.writerstudio.lang.markup;

/** Statstics Fields */
public enum FieldType {
    /** To show the current page number */
    PAGE_NUMBER("Stats.PageNumber"),
    /** To show the word count round to the significate digit*/
    WORD_COUNT("Stats.WordCountEst");

    private String fieldKey;

    /** Constructor with a key
     * @param key
     *      reference key name
     */
    private FieldType(String key){
        fieldKey = key;
    }

    /** Gets reference key name
     *
     * @return answer
     */
    public String getFieldKey(){
        return fieldKey;
    }

    /** Finds the field for a key
     *
     * @param key
     *      reference key name
     * @return answer or null
     */
    public static FieldType findField(String key){
        if (key == null || key.isEmpty()) return null;

        for (FieldType type: values()){
            if (type.fieldKey.equals(key)){
                return type;
            }
        }
        return null;
    }
}