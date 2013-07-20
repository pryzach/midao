package org.midao.jdbc.core.handlers.model;

import java.sql.SQLException;

/**
 * {@link QueryParametersLazyList} Iterator.
 */
public interface LazyCacheIterator<T> {

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyScrollOutputHandler#hasPrev()}
     */
    public boolean hasPrev();

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyScrollOutputHandler#getPrev()}
     */
    public T getPrev();

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler#getCurrent()}
     */
    public T getCurrent();

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler#hasNext()}
     */
    public boolean hasNext();

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyOutputHandler#getNext()}
     */
    public T getNext();

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyScrollOutputHandler#moveTo(int)}
     */
    public boolean moveTo(int row);

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyScrollOutputHandler#moveRelative(int)}
     */
    public boolean moveRelative(int rows);

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyScrollOutputHandler#position()}
     */
    public int position();

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyUpdateOutputHandler#updateRow(Object)}
     */
    public void updateRow(T row) throws SQLException;

    /**
     * @see {@link org.midao.jdbc.core.handlers.output.lazy.LazyUpdateOutputHandler#insertRow(Object)}
     */
    public void insertRow(T row) throws SQLException;

    /**
     * Returns source {@link QueryParametersLazyList}
     *
     * @return source {@link QueryParametersLazyList}
     */
    public QueryParametersLazyList getSource();
}
