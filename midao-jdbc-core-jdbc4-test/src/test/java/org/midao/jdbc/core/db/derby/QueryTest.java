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

package org.midao.jdbc.core.db.derby;

import org.midao.jdbc.core.MidaoConfig;
import org.midao.jdbc.core.MidaoFactory;
import org.midao.jdbc.core.db.DBConstants;
import org.midao.jdbc.core.db.DBQuery;
import org.midao.jdbc.core.db.DBQueryQueryStructure;
import org.midao.jdbc.core.db.QueryStructure;
import org.midao.jdbc.core.handlers.type.UniversalTypeHandler;
import org.midao.jdbc.core.service.QueryRunnerService;
import org.midao.jdbc.core.statement.LazyStatementHandler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QueryTest extends BaseDerby {
	
	public void testOutputHandler() throws SQLException {
    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryOutputHandlerDS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBQuery.queryOutputHandlerDS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryOutputHandlerDS(structure, runner);
	}
	
	public void testOutputHandlerWParams() throws SQLException {
    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryOutputHandlerWParamsDS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBQuery.queryOutputHandlerWParamsDS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryOutputHandlerWParamsDS(structure, runner);
	}
	
	public void testInputHandler1() throws SQLException {
    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryInputHandler1DS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBQuery.queryInputHandler1DS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryInputHandler1DS(structure, runner);
	}
	
	public void testInputHandler2() throws SQLException {
    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryInputHandler2DS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBQuery.queryInputHandler2DS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryInputHandler2DS(structure, runner);
	}
	
	public void testInputHandler3() throws SQLException {
    	QueryRunnerService runner = null;
    	
    	Map<String, Object> values = new HashMap<String, Object>();
    	
    	final QueryStructure defaultStructure = DBQueryQueryStructure.queryInputHandler3DS(values);
    	
    	QueryStructure structure = new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				defaultStructure.execute(runner);
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				defaultStructure.drop(runner);
			}
    		
    	};
    	
    	runner = MidaoFactory.getQueryRunner(this.dataSource);
    	
    	DBQuery.queryInputHandler3DS(structure, runner);
        
    	runner = MidaoFactory.getQueryRunner(this.conn);
    	
    	DBQuery.queryInputHandler3DS(structure, runner);
	}

    public void testLazyOutputMapList() throws SQLException {

        QueryRunnerService runner = null;

        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBQueryQueryStructure.queryLazyOutputMapList(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
            }

        };

        runner = MidaoFactory.getQueryRunner(this.dataSource, UniversalTypeHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryLazyOutputMapList(structure, runner);

        runner = MidaoFactory.getQueryRunner(this.conn, UniversalTypeHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryLazyOutputMapList(structure, runner);
    }

    public void testLazyOutputHandler() throws SQLException {
        QueryRunnerService runner = null;

        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBQueryQueryStructure.queryLazyOutputHandler(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
            }

        };

        runner = MidaoFactory.getQueryRunner(this.dataSource, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryLazyOutputHandler(structure, runner);

        runner = MidaoFactory.getQueryRunner(this.conn, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryLazyOutputHandler(structure, runner);
    }

    public void testLazyScrollOutputHandler() throws SQLException {
        QueryRunnerService runner = null;

        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBQueryQueryStructure.queryLazyScrollOutputHandler(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
            }

        };

        runner = MidaoFactory.getQueryRunner(this.dataSource, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryLazyScrollOutputHandler(structure, runner);

        runner = MidaoFactory.getQueryRunner(this.conn, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryLazyScrollOutputHandler(structure, runner);
    }

    public void testLazyScrollOutputHandlerLimitCache() throws SQLException {

        // the goal of test is to test the case when cache is limited and ResultSet should be read more intensely
        int defaultMaxCacheSize = MidaoConfig.getDefaultLazyCacheMaxSize();

        MidaoConfig.setDefaultLazyCacheMaxSize(1);

        testLazyScrollOutputHandler();

        MidaoConfig.setDefaultLazyCacheMaxSize(defaultMaxCacheSize);
    }

    public void testMapLazyUpdateOutputHandler() throws SQLException {
        QueryRunnerService runner = null;

        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBQueryQueryStructure.queryLazyUpdateOutputHandler(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
                defaultStructure.create(runner);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
            }

        };

        runner = MidaoFactory.getQueryRunner(this.dataSource, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryMapLazyUpdateOutputHandler(structure, runner);

        runner = MidaoFactory.getQueryRunner(this.conn, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryMapLazyUpdateOutputHandler(structure, runner);
    }

    public void testBeanLazyUpdateOutputHandler() throws SQLException {
        QueryRunnerService runner = null;

        Map<String, Object> values = new HashMap<String, Object>();

        final QueryStructure defaultStructure = DBQueryQueryStructure.queryLazyUpdateOutputHandler(values);

        QueryStructure structure = new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.CREATE_STUDENT_TABLE_DERBY);
                defaultStructure.create(runner);
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                defaultStructure.execute(runner);
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                defaultStructure.drop(runner);
            }

        };

        runner = MidaoFactory.getQueryRunner(this.dataSource, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryBeanLazyUpdateOutputHandler(structure, runner);

        runner = MidaoFactory.getQueryRunner(this.conn, null, LazyStatementHandler.class);

        runner.setTransactionManualMode(true);

        DBQuery.queryBeanLazyUpdateOutputHandler(structure, runner);
    }
}
