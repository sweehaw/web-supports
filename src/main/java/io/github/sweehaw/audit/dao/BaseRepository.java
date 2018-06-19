package io.github.sweehaw.audit.dao;

/**
 * @author sweehaw
 */
public interface BaseRepository<T> {

    /**
     * BaseRepository create and update
     *
     * @param entity entity object
     * @param <S>    entity class
     * @return entity object
     */
    <S extends T> S save(S entity);

    /**
     * BaseRepository delete
     *
     * @param entity entity object
     */
    void delete(T entity);

    /**
     * BaseRepository access log
     * @param object object
     */
    void accessLog(Object object);
}
