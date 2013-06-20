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
import org.midao.jdbc.core.handlers.HandlersConstants;
import org.midao.jdbc.core.handlers.type.TypeHandler;
import org.midao.jdbc.core.handlers.utils.MappingUtils;
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
 * It is recommended to use {@link #iterator()} to iterate through this list in {@link Type#READ_ONLY_FORWARD} mode (default mode).
 * Also please be aware that first element it returns is not statement params, but actually first value from ResultSet.
 * </p>
 *
 * By default {@link #get(int)} returns null if no value was found by that index. In future exception might be thrown instead.
 *
 * <p><i>This is reference implementation and is under active development</i></p>
 */
public class QueryParametersLazyList implements List<QueryParameters> {
    private static final String ERROR_NOT_ALLOWED = "This function is not allowed to be executed in lazy cache implementation.";
    private int maxCacheSize;
    private final TypeHandler typeHandler;

    private Map<Integer, QueryParameters> cacheMap;
    private Type type;

    private List<ResultSet> closedResultSet;
    private Statement stmt;
    private ResultSet currentResultSet;
    private int currentIndex;

    /**
     * Allowed types of Lazy query output list implementation
     */
    public enum Type {READ_ONLY_FORWARD};

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

        cacheMap = new TreeMap<Integer, QueryParameters>();
        cacheMap.put(0, statementParams);

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
                    cacheMap.put(i + 1, converted.get(i));
                }

                MidaoUtils.closeQuietly(rs);
            }
        }

        this.currentResultSet = stmt.getResultSet();
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
        this.maxCacheSize = maxCacheSize;
    }

    /**
     * Checks if cache list is empty
     *
     * @return true - if cache list is empty
     */
    public boolean isEmpty() {
        boolean result = cacheMap.isEmpty();

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
        return cacheMap.size();
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
        return cacheMap.values().toArray();
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
        return cacheMap.values().toArray(a);
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
        return new ArrayList(cacheMap.values()).subList(fromIndex, toIndex);
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
        if (currentResultSet == null && index > 0 && valueCached(index) == false) {
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
                            params = convertResultSetNextLine(currentResultSet);

                            if (params == null) {
                                closeResultSet(currentResultSet);
                                currentResultSet = getNextResultSet();
                            } else {
                                params = typeHandler.processOutput(stmt, params);
                            }
                        } while (params == null && currentResultSet != null);

                        if (params != null) {
                            updateCache(i + 1, params);
                            currentIndex++;
                        } else {
                            if (currentResultSet == null) {
                                break;
                            }
                        }
                    }
                } else {
                    throw new MidaoRuntimeException("Not supported yet");
                }
            } else {
                params = cacheMap.get(index);
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
        while (currentResultSet != null) {
            try {
                closeResultSet(currentResultSet);

                currentResultSet = getNextResultSet();
            } catch (SQLException ex) {
                // considering that all ResultSets are closed
                currentResultSet = null;
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
            private int currentIndex = -1;

            public boolean hasNext() {
                boolean result = false;

                QueryParameters params = source.get(currentIndex + 1);

                if (params != null) {
                    result = true;
                }

                return result;
            }

            public QueryParameters next() {
                QueryParameters result = source.get(currentIndex + 1);

                if (result != null) {
                    currentIndex++;
                }

                return result;
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

        if (valueCached(index) == true) {
            params = cacheMap.get(index);
            updateCache(index, element);
        } else {
            throw new MidaoRuntimeException(ERROR_NOT_ALLOWED + ". Only cached(read) values can be replaced");
        }

        return params;
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
     * Returns next (not closed) result set. If there is no more result sets - null is returned.
     * {@link Statement} is used to fetch next {@link ResultSet}
     *
     * @return next result set, null otherwise
     * @throws SQLException
     */
    private ResultSet getNextResultSet() throws SQLException {
        ResultSet result = null;

        while (stmt.getMoreResults() == true) {
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
     * After cache update - cache trim is performed {@link #trimCache()}
     *
     * @param index index at which the specified element is to be inserted
     * @param params element to be inserted
     */
    private void updateCache(int index, QueryParameters params) {
        this.cacheMap.put(index, params);
        trimCache();
    }

    /**
     * Checks if cache size exceeds maximum allowed value. Value is read from {@link #maxCacheSize}
     * If {@link #maxCacheSize} is less than zero - cache trim is not performed (everything would be cached).
     */
    private void trimCache() {
        if (maxCacheSize > 0 && this.cacheMap.size() - 1 > maxCacheSize) {
            Integer[] keyArray = this.cacheMap.keySet().toArray(new Integer[0]);

            for (int i = 1; i < keyArray.length && this.cacheMap.size() - 1 > maxCacheSize; i++) {
                this.cacheMap.remove(keyArray[i]);
            }
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

        if (cacheMap.containsKey(index) == true) {
            result = true;
        }

        return result;
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
        String columnName = null;

        if (rs.next() == true) {
            result = new QueryParameters();

            ResultSetMetaData rsmd = rs.getMetaData();
            int cols = rsmd.getColumnCount();

            for (int i = 1; i <= cols; i++) {

                columnName = rsmd.getColumnLabel(i);
                if (null == columnName || 0 == columnName.length()) {
                    columnName = rsmd.getColumnName(i);
                }

                result.set(columnName, rs.getObject(i));
                result.updatePosition(columnName, i - 1);
            }
        }

        return result;
    }
}
