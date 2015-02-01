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

package org.midao.jdbc.core.db.oracle;

import org.midao.jdbc.core.MjdbcConstants;
import org.midao.jdbc.core.MjdbcFactory;
import org.midao.jdbc.core.db.DBConstants;
import org.midao.jdbc.core.db.DBUpdate;
import org.midao.jdbc.core.db.DBUpdateQueryStructure;
import org.midao.jdbc.core.db.QueryStructure;
import org.midao.jdbc.core.handlers.xml.XmlInputOutputHandler;
import org.midao.jdbc.core.handlers.xml.XmlRepositoryFactory;
import org.midao.jdbc.core.handlers.output.MapOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateTest extends BaseOracle {

    public void testGeneratedKeys() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBUpdateQueryStructure.updateGeneratedKeysDS(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG));
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                MapOutputHandler handler = new MapOutputHandler();

                this.values.put("generatedKeys", runner.overrideOnce(MjdbcConstants.OVERRIDE_GENERATED_COLUMN_NAMES, new String[]{"ID"}).update(DBConstants.INSERT_STUDENT_TABLE, handler, new Object[0]));
                this.values.put("generatedKeys", runner.overrideOnce(MjdbcConstants.OVERRIDE_GENERATED_COLUMN_NAMES, new String[]{"ID"}).update(DBConstants.INSERT_STUDENT_TABLE, handler, new Object[0]));
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
                this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ));
            }

        };

        runner = MjdbcFactory.getQueryRunner(this.dataSource);

        DBUpdate.updateGeneratedKeysDS(structure, runner);

        runner = MjdbcFactory.getQueryRunner(this.conn);

        DBUpdate.updateGeneratedKeysDS(structure, runner);
    }

    public void testXmlGeneratedKeys() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBUpdateQueryStructure.updateXmlGeneratedKeysDS(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG));
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                XmlRepositoryFactory.addAll(XmlRepositoryFactory.getDocument(new ByteArrayInputStream(
                        ("<?xml version=\"1.0\"?><root><update id='insertStudent' generateKeys='true' generateKeysColumns='ID' outputHandler='MapOutputHandler'>" +
                                "INSERT INTO students (name, address) VALUES ('Not me', 'unknown')" +
                                "</update></root>").getBytes()
                )));

                XmlInputOutputHandler handler1 = new XmlInputOutputHandler("insertStudent");

                this.values.put("generatedKeys", runner.execute(handler1));
                this.values.put("generatedKeys", runner.execute(handler1));
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
                this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ));
            }

        };

        runner = MjdbcFactory.getQueryRunner(this.dataSource);

        DBUpdate.updateGeneratedKeysDS(structure, runner);

        runner = MjdbcFactory.getQueryRunner(this.conn);

        DBUpdate.updateGeneratedKeysDS(structure, runner);
    }

    public void testRowCountHandler() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBUpdateQueryStructure.updateRowCountHandlerDS(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG));
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
                this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ));
            }

        };

        runner = MjdbcFactory.getQueryRunner(this.dataSource);

        DBUpdate.updateRowCountHandlerDS(structure, runner);

        runner = MjdbcFactory.getQueryRunner(this.conn);

        DBUpdate.updateRowCountHandlerDS(structure, runner);
    }

    public void testUpdateWithParams() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBUpdateQueryStructure.updateWParamsDS(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG));
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
                this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ));
            }

        };

        runner = MjdbcFactory.getQueryRunner(this.dataSource);

        DBUpdate.updateWParamsDS(structure, runner);

        runner = MjdbcFactory.getQueryRunner(this.conn);

        DBUpdate.updateWParamsDS(structure, runner);
    }

    public void testInputHandler1DS() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBUpdateQueryStructure.updateInputHandler1DS(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG));
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
                this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ));
            }

        };

        runner = MjdbcFactory.getQueryRunner(this.dataSource);

        DBUpdate.updateInputHandler1DS(structure, runner);

        runner = MjdbcFactory.getQueryRunner(this.conn);

        DBUpdate.updateInputHandler1DS(structure, runner);
    }

    public void testInputHandler2DS() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBUpdateQueryStructure.updateInputHandler2DS(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG));
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
                this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ));
            }

        };

        runner = MjdbcFactory.getQueryRunner(this.dataSource);

        DBUpdate.updateInputHandler2DS(structure, runner);

        runner = MjdbcFactory.getQueryRunner(this.conn);

        DBUpdate.updateInputHandler2DS(structure, runner);
    }

    public void testInputHandler3DS() throws SQLException {

        if (this.checkConnected(dbName) == false) {
            return;
        }

        QueryRunnerService runner = null;
        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBUpdateQueryStructure.updateInputHandler3DS(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_SEQ));
                this.values.put("createUpdatedCount", (Integer) runner.update(DBConstants.CREATE_STUDENT_TABLE_ORACLE_TRG));
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
                this.values.put("dropUpdatedCount", (Integer) runner.update(DBConstants.DROP_STUDENT_TABLE_ORACLE_SEQ));
            }

        };

        runner = MjdbcFactory.getQueryRunner(this.dataSource);

        DBUpdate.updateInputHandler3DS(structure, runner);

        runner = MjdbcFactory.getQueryRunner(this.conn);

        DBUpdate.updateInputHandler3DS(structure, runner);
    }
}
