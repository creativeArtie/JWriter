package com.creativeartie.writerstudio.lang;

import java.util.*; // Optional;

import static com.creativeartie.writerstudio.main.ParameterChecker.*;

/** A {@link CacheKey} for {@linkplain Optional} value.
 *
 * @param T
 *      list value type
 */
public final class CacheKeyOptional<T> extends CacheKey<Optional<T>> {

    private Class<T> valueCaster;

    /** Creates a {@linkplain CacheKey}.
     *
     * @param caster
     *      value caster
     */
    public CacheKeyOptional(Class<T> caster){
        valueCaster = argumentNotNull(caster, "caster");
    }

    @Override
    public Optional<T> cast(Optional<?> value){
        return value.map(v -> valueCaster.cast(v));
    }
}