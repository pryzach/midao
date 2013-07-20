/*
 * Copyright 2013 Zakhar Prykhoda
 *
 *    midao.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.midao.jdbc.core.handlers.model;

import org.midao.jdbc.core.MidaoConfig;
import org.midao.jdbc.core.exception.MidaoRuntimeException;
import org.midao.jdbc.core.exception.MidaoSQLException;
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
import org.midao.jdbc.core.utils.AssertUtils;
import org.midao.jdbc.core.utils.MidaoUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Lazy query output list implementation. Is used to handle ResultSets returned from Query Execution.
 * Currently amount of cache is unlimited. In order to make it more memory friendly - please specify it via Constructor.
 *
 * <p>
 * It is recommended to use {@link #getLazyCacheIterator()} to iterate through this list in every mode.
 * </p>
 *
 * By default {@link #get(int)} returns null if no value was found by that index. In future exception might be thrown instead.
 *
 * <p><i>This is reference implementation and is under active development</i></p>
 */
public class QueryParametersLazyList implements List<QueryParameters> {
    private static final String ERROR_NOT_ALLOWED = "This function is not allowed to be executed in lazy cache implementation.";
    private int maxCacheSize;

    /** {@link TypeHandler} which would be used to process returned values */
    private final TypeHandler typeHandler;

    /** Instructs to use relative {@link ResultSet#relative(int)} positioning during scrolling */
    private boolean useRelativePositioning = true;

    /** Unlimited cache for generated values: header, generated values */
    private Map<Integer, QueryParameters> generatedCacheMap;
    /** Capped {@link #maxCacheSize} cache for values read from ResultSet */
    private Map<Integer, QueryParameters> resultSetCacheMap;

    /** Lazy Cache type. {@link Type} */
    private Type type;

    /** List of closed ResultSets */
    private List<ResultSet> closedResultSet;
    private Statement stmt;

    /** Current ResultSet */
    private ResultSet currentResultSet;

    /** Current Lazy Cache index. Please be aware that {@link LazyCacheIterator} have it's own index */
    private int currentIndex;

    /**
     * Allowed types of Lazy query output list implementation
     */
    public enum Type {READ_ONLY_FORWARD, UPDATE_FORWARD, READ_ONLY_SCROLL, UPDATE_SCROLL};

    /**
     * Creates new QueryParametersLazyList instance
     *
     * @param stmt SQL Statement
     * @param typeHandler Type handler which would be used to process rows after read
     * @param readGeneratedKeys specifies if generated keys should be cached right away
     * @param type lazy list type
     * @throws SQLException
     */
    public QueryParametersLazyList(Statement stmt, TypeHandler typeHandler, boolean readGeneratedKeys, Type type, int maxCacheSize) throws SQLException {
        this.stmt = stmt;
        this.typeHandler = typeHandler;
        this.maxCacheSize = maxCacheSize;
        this.type = type;
        this.currentIndex = 0;

        // generating QueryParameters class with values from Statement
        QueryParameters statementParams = new QueryParameters();
        statementParams.set(HandlersConstants.STMT_UPDATE_COUNT, stmt.getUpdateCount());

        final QueryParametersLazyList thisInstance = this;
        resultSetCacheMap = new LinkedHashMap<Integer, QueryParameters>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, QueryParameters> eldest) {
                return thisInstance.getMaxCacheSize() > 0 && size() > thisInstance.getMaxCacheSize();
            }
        };

        generatedCacheMap = new TreeMap<Integer, QueryParameters>();
        generatedCacheMap.put(0, statementParams);

        // reading generated keys
        closedResultSet = new ArrayList<ResultSet>();
        ResultSet rs = null;
        List<QueryParameters> converted = null;

        if ( (Integer) statementParams.getValue(HandlersConstants.STMT_UPDATE_COUNT) > 0 && readGeneratedKeys == true) {

            rs = stmt.getGeneratedKeys();

            // it is possible that generated values would exceed max cache size. planned to be improved with next release
            if (rs != null) {
                converted = MappingUtils.convertResultSet(rs);

                for (int i = 0; i < converted.size(); i++) {
                    generatedCacheMap.put(i + 1, converted.get(i));
                }

                MidaoUtils.closeQuietly(rs);
            }
        }

        setCurrentResultSet(stmt.getResultSet());
    }

    /**
     * Creates new QueryParametersLazyList instance
     *
     * @param stmt SQL Statement
     * @param typeHandler Type handler which would be used to process rows after read
     * @param readGeneratedKeys specifies if generated keys should be cached right away
     * @param maxCacheSize maximum cache size
     * @throws SQLException
     */
    public QueryParametersLazyList(Statement stmt, TypeHandler typeHandler, boolean readGeneratedKeys, int maxCacheSize) throws SQLException {
        this(stmt, typeHandler, readGeneratedKeys, Type.READ_ONLY_FORWARD, maxCacheSize);
    }

    /**
     * Creates new QueryParametersLazyList instance
     *
     * @param stmt SQL Statement
     * @param typeHandler Type handler which would be used to process rows after read
     * @param readGeneratedKeys specifies if generated keys should be cached right away
     * @throws SQLException
     */
    public QueryParametersLazyList(Statement stmt, TypeHandler typeHandler, boolean readGeneratedKeys) throws SQLException {
        this(stmt, typeHandler, readGeneratedKeys, Type.READ_ONLY_FORWARD, MidaoConfig.getDefaultLazyCacheMaxSize());
    }

    /**
     * Sets maximum cache size for this instance.
     * If new max size limit is already exceeded - trim would be performed only during next cache update.
     *
     * @param maxCacheSize new max cache size
     */
    public void setMaxCacheSize(int maxCacheSize) {

        if (maxCacheSize == 0) {
            this.maxCacheSize = 1;
        } else if (maxCacheSize < 0) {
            this.maxCacheSize = -1;
        } else {
            this.maxCacheSize = maxCacheSize;
        }

    }

    /**
     * Returns current max cache size
     *
     * @return current max cache size
     */
    public int getMaxCacheSize() {
        return this.maxCacheSize;
    }

    /**
     * Changes type of this Lazy Cache.
     * This function is intended for internal use by {@link org.midao.jdbc.core.AbstractQueryRunner}.
     *
     * @param type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Checks if cache list is empty
     *
     * @return true - if cache list is empty
     */
    public boolean isEmpty() {
        boolean result = generatedCacheMap.isEmpty();

        return result;
    }

    /**
     * Function is not allowed to be executed as Lazy query output doesn't know the size of output.
     *
     * @return throws Exception
     */
    public int size() {
        throw new MidaoRuntimeException("Size is unknown in lazy cache implementation. Please use sizeCached()");
    }

    /**
     * Returns amount of elements cached (inc. header as first element).
     *
     * @return size of cached elements
     */
    public int sizeCached() {
        return generatedCacheMap.size() + resultSetCacheMap.size();
    }

    /**
     * Function is not allowed to be executed, as it might result in caching whole query output which would lead to
     * huge memory usage and/or crash.
     *
     * In future it might be changed and this functionality might be allowed.
     *
     * @return Exception
     */
    public Object[] toArray() {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + " Please use toArrayCached instead.");
    }

    /**
     * Returns array of cached elements (inc. statement as first element).
     *
     * @return arrya of cached elements
     */
    public Object[] toArrayCached() {
        Object[] readCacheArray = generatedCacheMap.values().toArray();
        Object[] writeCacheArray = resultSetCacheMap.values().toArray();

        Object[] combinedCacheArray = new Object[readCacheArray.length + writeCacheArray.length];

        System.arraycopy(readCacheArray, 0, combinedCacheArray, 0, readCacheArray.length);
        System.arraycopy(writeCacheArray, 0, combinedCacheArray, readCacheArray.length, writeCacheArray.length);

        return combinedCacheArray;
    }

    /**
     * The same as {@link #toArray()}. Currently execution is not allowed. Might be changed in future
     *
     * @param a the array into which the elements of this list are to be stored, if it is big enough;
     *          otherwise, a new array of the same runtime type is allocated for this purpose.
     * @return Exception
     */
    public <T> T[] toArray(T[] a) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + " Please use toArrayCached instead.");
    }

    /**
     * The same as {@link #toArrayCached()}. Returns array of cached elements(inc. header as first element)
     * as array of Type T.
     *
     * @param a the array into which the elements of this list are to be stored, if it is big enough;
     *          otherwise, a new array of the same runtime type is allocated for this purpose.
     * @return array of cached elements as array of Type T.
     */
    public <T> T[] toArrayCached(T[] a) {
        T[] readCacheArray = generatedCacheMap.values().toArray(a);
        T[] writeCacheArray = resultSetCacheMap.values().toArray(a);

        T[] combinedCacheArray = (T[]) java.lang.reflect.Array.
                newInstance(a.getClass().getComponentType(), readCacheArray.length + writeCacheArray.length);

        System.arraycopy(readCacheArray, 0, combinedCacheArray, 0, readCacheArray.length);
        System.arraycopy(writeCacheArray, 0, combinedCacheArray, readCacheArray.length, writeCacheArray.length);

        return combinedCacheArray;
    }

    /**
     * Execution is currently not allowed, as it might result in caching whole query output which would lead to
     * huge memory usage and/or crash.
     *
     * @see {@link List#subList(int, int)}
     */
    public List<QueryParameters> subList(int fromIndex, int toIndex) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + " Please use subListCached instead.");
    }

    /**
     * Returns sublist of cached elements.
     *
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex high endpoint (exclusive) of the subList
     * @return a view of the specified range within this list
     */
    public List<QueryParameters> subListCached(int fromIndex, int toIndex) {
        ArrayList<QueryParameters> result = new ArrayList<QueryParameters>();

        result.addAll(generatedCacheMap.values());
        result.addAll(resultSetCacheMap.values());

        return result.subList(fromIndex, toIndex);
    }

    /**
     * Returns element at specified position. If no element was found - null is returned (in future exception
     * might be thrown instead).
     *
     * @see {@link List#get(int)}
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list (null if nothing was found).
     */
    public QueryParameters get(int index) {
        if (getCurrentResultSet() == null && index > 0 && valueCached(index) == false) {
            // don't have anymore values in cache
            return null;
        }

        QueryParameters params = null;

        try {
            if (valueCached(index) == false) {

                if (this.type == Type.READ_ONLY_FORWARD && currentIndex >= index) {
                    throw new MidaoRuntimeException("Attempt to read current/previous value failed because it is not present in cache. " +
                            "Please increase maximum cache size via overrider or MidaoConfig.");
                }

                if (this.type == Type.READ_ONLY_FORWARD) {
                    for (int i = currentIndex; i < index; i++) {
                        params = null;

                        do {
                            params = convertResultSetNextLine(getCurrentResultSet());

                            if (params == null) {
                                closeResultSet(getCurrentResultSet());
                                setCurrentResultSet(getNextResultSet());
                            }
                        } while (params == null && getCurrentResultSet() != null);

                        if (params != null) {
                            updateCache(i + 1, params);
                            currentIndex++;
                        } else {
                            if (getCurrentResultSet() == null) {
                                break;
                            }
                        }
                    }
                } else if (this.type == Type.READ_ONLY_SCROLL || this.type == Type.UPDATE_SCROLL) {

                    params = readResultSetRow((index + 1) - generatedCacheMap.size());

                    if (params != null) {
                        updateCache(index, params);
                    }

                }
            } else {
                params = readCachedValue(index);
            }
        } catch (SQLException ex) {
            throw new MidaoRuntimeException("Failed to read ResultSet", ex);
        }

        if (params == null) {
            // It seems appropriate to return null rather then exception.
            // This might be changed in the future
        }

        return params;
    }

    /**
     * Performs cleanup of all resources used by this lazy query output list implementation: {@link Statement} and {@link ResultSet}
     */
    public void close() {
        while (getCurrentResultSet() != null) {
            try {
                closeResultSet(getCurrentResultSet());

                setCurrentResultSet(getNextResultSet());
            } catch (SQLException ex) {
                // considering that all ResultSets are closed
                setCurrentResultSet(null);
            }
        }

        MidaoUtils.closeQuietly(stmt);
    }

    /**
     * Returns iterator of this lazy query output list implementation.
     * First element returned - is header. Actual values are returned only starting from second element.
     *
     * @return new {@link Iterator} instance
     */
    public Iterator<QueryParameters> iterator() {
        final QueryParametersLazyList source = this;

        return new Iterator<QueryParameters>() {
            private LazyCacheIterator<QueryParameters> lazyCacheIterator = source.getLazyCacheIterator(-1);

            public boolean hasNext() {
                return lazyCacheIterator.hasNext();
            }

            public QueryParameters next() {
                return lazyCacheIterator.getNext();
            }

            public void remove() {
                source.remove(currentIndex);
            }
        };
    }

    /**
     * Sets value in cache. Please be aware that currently cache only is updated.
     * No changes to Database are made
     *
     * @param index element number to replace
     * @param element new element value
     * @return previous element at that position
     * @throws MidaoRuntimeException if value is not in cache
     */
    public QueryParameters set(int index, QueryParameters element) {
        QueryParameters params = null;

        if (getMaxCacheSize() != -1) {
            throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + ". Cache update is allowed when Cache is not limited (used by Cached output handlers)");
        }

        if (valueCached(index) == true) {
            params = readCachedValue(index);
            updateCache(index, element);
        } else {
            throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + ". Only cached(read) values can be replaced");
        }

        return params;
    }

    /**
     * Updates specified row in ResultSet
     *
     * @param index index of row which would be updated
     * @param params source of values with which row would be updated
     * @return value before update
     * @throws SQLException
     */
    public QueryParameters update(int index, QueryParameters params) throws SQLException {
        AssertUtils.assertNotNull(params, "Element cannot be null, but values inside of it - can");

        if (this.type == Type.READ_ONLY_FORWARD || this.type == Type.READ_ONLY_SCROLL) {
            throw new MidaoSQLException("This Lazy query output cache is initialized as read-only - therefore cannot be updated.");
        }

        QueryParameters result = get(index);

        if (this.type == Type.UPDATE_FORWARD) {
            if (this.currentIndex == index) {
                updateResultSetCurrentLine(getCurrentResultSet(), params);
            } else if (this.currentIndex + 1 == index) {
                updateResultSetRow((index + 1) - generatedCacheMap.size(), params);
            } else {
                throw new MidaoRuntimeException("Only current/next element can be updated!");
            }

            getCurrentResultSet().updateRow();

            this.currentIndex = index;
        } else if (this.type == Type.UPDATE_SCROLL) {

            updateResultSetRow((index+1) - generatedCacheMap.size(), params);
            getCurrentResultSet().updateRow();
        } else {
            throw new MidaoSQLException("This Lazy query output cache was initialized with unknown type");
        }

        return result;
    }

    /**
     * Inserts new row into returned ResultSet
     *
     * @param params values which would be used to fill newly inserted row
     * @throws SQLException
     */
    public void insert(QueryParameters params) throws SQLException {
        getCurrentResultSet().moveToInsertRow();

        updateResultSetCurrentLine(getCurrentResultSet(), params);
        getCurrentResultSet().insertRow();

        getCurrentResultSet().moveToCurrentRow();
    }

    /* List of functions which would be implemented to allow SCROLL and UPDATEABLE */

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public ListIterator<QueryParameters> listIterator() {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + " Please use iterator() instead.");
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public ListIterator<QueryParameters> listIterator(int index) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + " Please use iterator() instead.");
    }

    /* List of functions which are not allowed to be executed due to nature of this class */

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public boolean add(QueryParameters queryParameters) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public boolean addAll(Collection<? extends QueryParameters> c) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public boolean addAll(int index, Collection<? extends QueryParameters> c) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public void add(int index, QueryParameters element) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public boolean remove(Object o) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public QueryParameters remove(int index) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public boolean removeAll(Collection<?> c) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public boolean retainAll(Collection<?> c) {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public void clear() {
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    public boolean contains(Object o) {
        // it is inefficient to check contains on lazy cache implementation
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public boolean containsAll(Collection<?> c) {
        // it is inefficient to check contains on lazy cache implementation
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public int indexOf(Object o) {
        // it is inefficient to check indexOf on lazy cache implementation
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Currently usage is not allowed.
     * Will be implemented along with Updateable/scrollable functionality
     *
     * @throws MidaoRuntimeException
     */
    public int lastIndexOf(Object o) {
        // it is inefficient to check indexOf on lazy cache implementation
        throw new MidaoRuntimeException(ERROR_NOT_ALLOWED);
    }

    /**
     * Return newly created {@link LazyCacheIterator} with initial position equal 0 (which means that first element
     * returned would be not header but actual value from JDBC Driver)
     *
     * @see {@link #getLazyCacheIterator(int)}
     */
    public LazyCacheIterator<QueryParameters> getLazyCacheIterator() {
        return getLazyCacheIterator(0);
    }

    /**
     * Returns {@link LazyCacheIterator}.
     * Can be used to iterate/scroll/update over {@link QueryParametersLazyList}.
     *
     * <p>
     * It is generally not advised to use multiple {@link LazyCacheIterator} for once instance of {@link QueryParametersLazyList},
     * due to possible performance issues or possible JDBC Driver issues.
     * </p>
     *
     * @param initialIndex initial position of newly create {@link LazyCacheIterator}
     * @return new instance of {@link LazyCacheIterator}
     */
    private LazyCacheIterator<QueryParameters> getLazyCacheIterator(final int initialIndex) {
        return new LazyCacheIterator<QueryParameters>() {
            private int currentIndex = initialIndex;

            private boolean beginReached = true;
            private boolean endReached = false;

            /**
             * {@inheritDoc}
             */
            public boolean hasPrev() {
                boolean result = false;

                // doesn't allow to treat header as element from Database
                if (currentIndex > 1) {
                    QueryParameters params = get(currentIndex - 1);

                    if (params != null) {
                        result = true;
                        beginReached = false;
                    } else {
                        checkBegin();
                    }
                }

                return result;
            }

            /**
             * {@inheritDoc}
             */
            public QueryParameters getPrev() {
                QueryParameters result = null;

                // doesn't allow to treat header as element from Database
                if (currentIndex > 1) {
                    result = get(currentIndex - 1);
                }

                if (result != null) {
                    currentIndex--;
                } else {
                    checkBegin();
                }

                return result;
            }

            /**
             * {@inheritDoc}
             */
            public QueryParameters getCurrent() {
                return get(currentIndex);
            }

            /**
             * {@inheritDoc}
             */
            public boolean hasNext() {
                boolean result = false;
                QueryParameters params = get(currentIndex + 1);

                if (params != null) {
                    result = true;
                    endReached = false;
                } else {
                    checkEnd();
                }

                return result;
            }

            /**
             * {@inheritDoc}
             */
            public QueryParameters getNext() {
                QueryParameters result = null;

                result = get(currentIndex + 1);

                if (result != null) {
                    currentIndex++;
                } else {
                    checkEnd();
                }

                return result;
            }

            /**
             * {@inheritDoc}
             */
            public boolean moveTo(int row) {
                this.currentIndex = row;

                return true;
            }

            /**
             * {@inheritDoc}
             */
            public boolean moveRelative(int rows) {
                this.currentIndex = this.currentIndex + rows;

                return true;
            }

            /**
             * {@inheritDoc}
             */
            public int position() {
                return this.currentIndex;
            }

            /**
             * {@inheritDoc}
             */
            public void updateRow(QueryParameters row) throws SQLException {
                update(this.currentIndex, row);
            }

            /**
             * {@inheritDoc}
             */
            public void insertRow(QueryParameters row) throws SQLException {
                insert(row);
            }

            /**
             * {@inheritDoc}
             */
            public QueryParametersLazyList getSource() {
                return getInstance();
            }

            /**
             * Checks if end if reached and updated corresponding variables accordingly
             */
            private void checkEnd() {
                if (endReached != true) {
                    endReached = true;
                    beginReached = false;
                    currentIndex++;
                }
            }

            /**
             * Checks if begin if reached and updated corresponding variables accordingly
             */
            private void checkBegin() {
                if (beginReached != true) {
                    beginReached = true;
                    endReached = false;
                    currentIndex--;
                }
            }

        };
    }

    /**
     * Returns next (not closed) result set. If there is no more result sets - null is returned.
     * {@link Statement} is used to fetch next {@link ResultSet}
     *
     * @return next result set, null otherwise
     * @throws SQLException
     */
    private ResultSet getNextResultSet() throws SQLException {
        ResultSet result = null;

        while (stmt.getMoreResults() == true) {

            // when gerMoreResults are returned - some drivers close previous ResultSets - therefore we cannot read them again.
            if (this.type == Type.UPDATE_SCROLL || this.type == Type.READ_ONLY_SCROLL) {
                throw new MidaoSQLException("Lazy cache scroll does not support Multiple ResultSets. Pleae use" +
                        "non-scrollable lazy handlers or cached output handlers.");
            }

            if (closedResultSet.contains(result) == false) {
                result = stmt.getResultSet();
                break;
            }
        }

        return result;
    }

    /**
     * Silently closes supplied result set
     *
     * @param rs result set which should be closed
     */
    private void closeResultSet(ResultSet rs) {
        if (closedResultSet.contains(rs) == false) {
            MidaoUtils.closeQuietly(rs);
            closedResultSet.add(rs);
        }
    }

    /**
     * Updates value in cache.
     * After cache update - cache trim is performed {@link LinkedHashMap#removeEldestEntry(java.util.Map.Entry)}
     *
     * @param index index at which the specified element is to be inserted
     * @param params element to be inserted
     */
    private void updateCache(int index, QueryParameters params) {
        if (generatedCacheMap.containsKey(index) == true) {
            this.generatedCacheMap.put(index, params);
        } else {
            this.resultSetCacheMap.put(index, params);
        }
    }

    /**
     * Checks if value(by index) is present in cache
     *
     * @param index index at which the specified element is to be inserted
     * @return true if value is present in cache, false otherwize
     */
    private boolean valueCached(int index) {
        boolean result = false;

        if (generatedCacheMap.containsKey(index) == true || resultSetCacheMap.containsKey(index) == true) {
            result = true;
        }

        return result;
    }

    /**
     * Reads cached values from {@link #generatedCacheMap} (first) and {@link #resultSetCacheMap} (second).
     *
     * @param index row index which should be read
     * @return value from cache, or null if it is not present in cache.
     */
    private QueryParameters readCachedValue(int index) {
        QueryParameters result = null;

        if (generatedCacheMap.containsKey(index) == true) {
            result = generatedCacheMap.get(index);
        } else if (resultSetCacheMap.containsKey(index) == true) {
            result = resultSetCacheMap.get(index);
        }

        return result;
    }

    /**
     * Returns this instance of {@link QueryParametersLazyList}. Is used by {@link #getLazyCacheIterator(int)}
     *
     * @return this instance of {@link QueryParametersLazyList}
     */
    private QueryParametersLazyList getInstance() {
        return this;
    }

    /**
     * Reads next result set line and converts it into {@link QueryParameters}.
     * If there is no more lines - null is returned.
     *
     * @param rs result set read
     * @return converted line, null otherwise
     * @throws SQLException
     */
    private QueryParameters convertResultSetNextLine(ResultSet rs) throws SQLException {
        QueryParameters result = null;

        if (rs.next() == true) {
            result = convertResultSetCurrentLine(rs);
        }

        return result;
    }

    /**
     * This is reference implementation of universal read function for scrollable ResultSets.
     *
     * Currently it is unoptimized, due to possible compatibility issues.
     *
     * @param row row which should be read
     * @return read value
     * @throws SQLException
     */
    private QueryParameters readResultSetRow(int row) throws SQLException {
        int currentRow = getCurrentResultSet().getRow();
        QueryParameters result = null;

        if (currentRow == 0) {
            // before first or last
            if (getCurrentResultSet().isAfterLast() == true) {
                // positioning on last
                getCurrentResultSet().last();
            } else if (getCurrentResultSet().isBeforeFirst() == true) {
                // position on first
                getCurrentResultSet().first();
            } else {
                throw new MidaoRuntimeException("ResultSet need to be repositioned to get row different than zero");
            }

            currentRow = getCurrentResultSet().getRow();
        }

        if (currentRow == row) {
            result = convertResultSetCurrentLine(getCurrentResultSet());
        } if (useRelativePositioning == true) {

            getCurrentResultSet().relative(row - currentRow);

            if (getCurrentResultSet().getRow() > 0) {
                result = convertResultSetCurrentLine(getCurrentResultSet());
            }
        } else if (currentRow < row) {
            while (getCurrentResultSet().next() == true) {
                if (getCurrentResultSet().getRow() == row) {
                    result = convertResultSetCurrentLine(getCurrentResultSet());
                }
            }
        } else {
            while (getCurrentResultSet().previous() == true) {
                if (getCurrentResultSet().getRow() == row) {
                    result = convertResultSetCurrentLine(getCurrentResultSet());
                }
            }
        }

        return result;
    }

    /**
     * This is reference implementation of universal update function for updateable ResultSets.
     *
     * Currently it is unoptimized, due to possible compatibility issues.
     *
     * @param row row which should be updated
     * @param params values which would be used to update current row
     * @throws SQLException
     */
    private void updateResultSetRow(int row, QueryParameters params) throws SQLException {
        int currentRow = getCurrentResultSet().getRow();

        if (currentRow == 0) {
            // before first or last
            if (getCurrentResultSet().isAfterLast() == true) {
                // positioning on last
                getCurrentResultSet().last();
            } else if (getCurrentResultSet().isBeforeFirst() == true) {
                // position on first
                getCurrentResultSet().first();
            } else {
                throw new MidaoRuntimeException("ResultSet need to be repositioned to get row different than zero");
            }

            currentRow = getCurrentResultSet().getRow();
        }

        if (currentRow == row) {
            updateResultSetCurrentLine(getCurrentResultSet(), params);
        } if (useRelativePositioning == true) {

            getCurrentResultSet().relative(row - currentRow);

            if (getCurrentResultSet().getRow() > 0) {
                updateResultSetCurrentLine(getCurrentResultSet(), params);
            }
        } else if (currentRow < row) {
            while (getCurrentResultSet().next() == true) {
                if (getCurrentResultSet().getRow() == row) {
                    updateResultSetCurrentLine(getCurrentResultSet(), params);
                }
            }
        } else {
            while (getCurrentResultSet().previous() == true) {
                if (getCurrentResultSet().getRow() == row) {
                    updateResultSetCurrentLine(getCurrentResultSet(), params);
                }
            }
        }
    }

    /**
     * Converts ResultSet current row into {@link QueryParameters}. Return value is handled by {@link TypeHandler}
     *
     * @param rs ResultSet which would be read
     * @return converted ResultSet current row
     * @throws SQLException
     */
    private QueryParameters convertResultSetCurrentLine(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();
        String columnName = null;
        QueryParameters result = new QueryParameters();

        for (int i = 1; i <= cols; i++) {

            columnName = rsmd.getColumnLabel(i);
            if (null == columnName || 0 == columnName.length()) {
                columnName = rsmd.getColumnName(i);
            }

            result.set(columnName, rs.getObject(i));
            result.updatePosition(columnName, i - 1);
        }

        if (result != null) {
            result = typeHandler.processOutput(stmt, result);
        }

        return result;
    }

    /**
     * Updates ResultSet current row. Input value is handled by {@link TypeHandler}
     *
     * @param rs ResultSet which would be updated
     * @param params values which would be used to update current row
     * @throws SQLException
     */
    private void updateResultSetCurrentLine(ResultSet rs, QueryParameters params) throws SQLException {
        Object value = null;
        Object prevValue = null;

        int columnIndex = -1;
        boolean readOnly = false;
        QueryParameters prevParams = get(this.currentIndex);
        ResultSetMetaData rsmd = rs.getMetaData();

        QueryParameters processedParams = typeHandler.processInput(stmt, params);

        for (String parameterName : processedParams.keySet()) {
            value = processedParams.getValue(parameterName);
            prevValue = prevParams.getValue(parameterName);

            // no need to update value if it is the same
            if (value == prevValue || (value != null && prevValue != null && value.equals(prevValue) == true)) {
                continue;
            }

            columnIndex = rs.findColumn(parameterName);
            readOnly = rsmd.isReadOnly(columnIndex) || rsmd.isAutoIncrement(columnIndex);

            if (readOnly == false) {
                rs.updateObject(parameterName, value);
            }
        }
    }

    /**
     * Returns current ResultSet
     *
     * @return current ResultSet
     */
    private ResultSet getCurrentResultSet() {
        return currentResultSet;
    }

    /**
     * Changes current ResultSet
     *
     * @param currentResultSet
     */
    public void setCurrentResultSet(ResultSet currentResultSet) {
        this.currentResultSet = currentResultSet;
    }
}
