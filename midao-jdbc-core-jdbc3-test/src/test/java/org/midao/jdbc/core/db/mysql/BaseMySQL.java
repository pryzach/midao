/*
 * Copyright 2013 Zakhar Prykhoda
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

package org.midao.jdbc.core.db.mysql;

import org.midao.jdbc.core.db.BaseDB;
import org.midao.jdbc.core.db.DBConstants;

public class BaseMySQL extends BaseDB {

    protected final String dbName = DBConstants.mysql;

    @Override
    protected void setUp() throws Exception {
        establishConnection(dbName);
    }

    @Override
    protected void tearDown() throws Exception {
        closeConnection();
    }

}
