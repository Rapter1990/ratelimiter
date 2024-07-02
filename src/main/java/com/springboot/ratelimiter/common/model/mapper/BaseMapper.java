package com.springboot.ratelimiter.common.model.mapper;

import java.util.Collection;
import java.util.List;

/**
 * Interface for mapping between source and target types.
 *
 * @param <S> the source type
 * @param <T> the target type
 */
public interface BaseMapper<S, T> {

    /**
     * Maps a single source object to a target object.
     *
     * @param source the source object
     * @return the target object
     */
    T map(S source);

    /**
     * Maps a collection of source objects to a list of target objects.
     *
     * @param sources the collection of source objects
     * @return the list of target objects
     */
    List<T> map(Collection<S> sources);

}
