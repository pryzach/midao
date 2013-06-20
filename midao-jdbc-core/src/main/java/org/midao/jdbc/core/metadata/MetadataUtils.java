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

package org.midao.jdbc.core.metadata;

/**
 * Utilities useful while working with Database metadata
 */
public class MetadataUtils {
    /**
     * Reads value returned from getDatabaseProductName (DatabaseMetadata class) and converts it into
     * "short" name
     *
     * @param databaseProductName {@link java.sql.DatabaseMetaData#getDatabaseProductName()}
     * @return short database name
     */
    public static String processDatabaseProductName(String databaseProductName) {
        String result = databaseProductName;

        if (databaseProductName != null && databaseProductName.startsWith("DB2") == true) {
            result = "DB2";
        } else if ("Sybase SQL Server".equals(databaseProductName) == true
                || "Adaptive Server Enterprise".equals(databaseProductName) == true || "ASE".equals(databaseProductName) == true
                || "sql server".equals(databaseProductName) == true) {
            result = "Sybase";
        }

        return result;
    }
}
