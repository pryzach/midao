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

package org.midao.jdbc.core.handlers.utils;

import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Collection of utilities used during Stored Procedure/Function call
 */
public class CallableUtils {
    private static final String REGEX_IS_FUNCTION = "[{].*?[?].*?[=].*?(call)";
    private static final String REGEX_PROCEDURE_FULL_NAME = "(?<=call)[ :=?]*?[a-zA-Z0-9\\-_\\.]+?[ ]*?(?=[(].*?[)])";
    private static final String ERROR_FULL_PROCEDURE_NAME_NOT_FOUND = "Error! Error in SQL String. Couldn't determine full procedure name from: %s";
    private static final String ERROR_SHORT_PROCEDURE_NAME_NOT_FOUND = "Error! Error in full name. Couldn't determine short procedure name from: %s";

    /**
     * Checks if SQL String represents function call
     *
     * @param decodedSql SQL String which would be checked
     * @return true/false
     */
    public static boolean isFunctionCall(String decodedSql) {
        Pattern regexPattern = null;
        Matcher regexMatcher = null;

        regexPattern = Pattern.compile(REGEX_IS_FUNCTION, Pattern.CASE_INSENSITIVE);
        regexMatcher = regexPattern.matcher(decodedSql);


        return regexMatcher.find();
    }

    /**
     * Returns short function name. Example:
     * schema.package.name - "name" would be returned
     *
     * @param decodedSql SQL String which would be processed
     * @return procedure name
     */
    public static String getStoredProcedureShortNameFromSql(String decodedSql) {
        String spName = null;

        Pattern regexPattern = null;
        Matcher regexMatcher = null;

        String procedureFullName = getStoredProcedureFullName(decodedSql);

        String[] procedurePath = procedureFullName.split("[.]");

        if (procedurePath.length > 0) {
            spName = procedurePath[procedurePath.length - 1];
        } else {
            throw new IllegalArgumentException(String.format(ERROR_SHORT_PROCEDURE_NAME_NOT_FOUND, procedureFullName));
        }

        return spName;
    }

    /**
     * Returns full function name. Example:
     * call schema.package.name - "schema.package.name" would be returned
     *
     * @param decodedSql SQL String which would be processed
     * @return full procedure name
     */
    public static String getStoredProcedureFullName(String decodedSql) {
        String spName = null;

        Pattern regexPattern = null;
        Matcher regexMatcher = null;

        regexPattern = Pattern.compile(REGEX_PROCEDURE_FULL_NAME, Pattern.CASE_INSENSITIVE);
        regexMatcher = regexPattern.matcher(decodedSql);

        if (regexMatcher.find() == true) {
            spName = regexMatcher.group();

            spName = spName.trim();
        } else {
            throw new IllegalArgumentException(String.format(ERROR_FULL_PROCEDURE_NAME_NOT_FOUND, decodedSql));
        }

        return spName;
    }

    /**
     * Clones @original and updates it's direction - taken from @source.
     *
     * @param original QueryParameters which would be updated
     * @param source   QueryParameters directions of which would be read
     * @return updated clone on @original with updated directions
     */
    public static QueryParameters updateDirections(QueryParameters original, QueryParameters source) {
        QueryParameters updatedParams = new QueryParameters(original);
        Integer position = null;
        String originalKey = null;

        if (source != null) {
            for (String sourceKey : source.keySet()) {
                position = source.getFirstPosition(sourceKey);

                if (position != null) {
                    originalKey = original.getNameByPosition(position);
                    if (updatedParams.containsKey(originalKey) == true) {
                        updatedParams.updateDirection(originalKey, source.getDirection(sourceKey));
                    }
                }
            }
        }

        return updatedParams;
    }

    /**
     * Clones @original and updates it's types - taken from @source.
     *
     * @param original QueryParameters which would be updated
     * @param source   QueryParameters types of which would be read
     * @return updated clone on @original with updated types
     */
    public static QueryParameters updateTypes(QueryParameters original, QueryParameters source) {
        QueryParameters updatedParams = new QueryParameters(original);
        Integer position = null;
        String originalKey = null;

        if (source != null) {
            for (String sourceKey : source.keySet()) {
                position = source.getFirstPosition(sourceKey);

                if (position != null) {
                    originalKey = original.getNameByPosition(position);
                    if (updatedParams.containsKey(originalKey) == true) {
                        updatedParams.updateType(originalKey, source.getType(sourceKey));
                    }
                }
            }
        }

        return updatedParams;
    }

    /**
     * Same as @updateDirections but updates based not on position but on key
     *
     * @param original QueryParameters which would be updated
     * @param source   QueryParameters directions of which would be read
     * @return updated clone on @original with updated directions
     */
    public static QueryParameters updateDirectionsByName(QueryParameters original, QueryParameters source) {
        QueryParameters updatedParams = new QueryParameters(original);

        if (source != null) {
            for (String sourceKey : source.keySet()) {
                if (updatedParams.containsKey(sourceKey) == true) {
                    updatedParams.updateDirection(sourceKey, source.getDirection(sourceKey));
                }
            }
        }

        return updatedParams;
    }

    /**
     * Same as @updateTypes but updates based not on position but on key
     *
     * @param original QueryParameters which would be updated
     * @param source   QueryParameters types of which would be read
     * @return updated clone on @original with updated types
     */
    public static QueryParameters updateTypesByName(QueryParameters original, QueryParameters source) {
        QueryParameters updatedParams = new QueryParameters(original);

        if (source != null) {
            for (String sourceKey : source.keySet()) {
                if (updatedParams.containsKey(sourceKey) == true) {
                    updatedParams.updateType(sourceKey, source.getType(sourceKey));
                }
            }
        }

        return updatedParams;
    }

}
