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

package org.midao.jdbc.core.db;

import org.midao.jdbc.core.handlers.input.named.MapInputHandler;
import org.midao.jdbc.core.handlers.input.named.MapListInputHandler;
import org.midao.jdbc.core.handlers.output.*;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DBQueryQueryStructure {

	public static QueryStructure queryOutputHandlerDS(Map<String, Object> values) {
    	return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.INSERT_STUDENT_TABLE, new RowCountOutputHandler<Integer>(), new Object[0]);
				
				this.values.put("resultMap", runner.query(DBConstants.SELECT_STUDENT_TABLE_W_PARAMS, new MapOutputHandler(), 1));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
	}
	
	public static QueryStructure queryOutputHandlerWParamsDS(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.INSERT_STUDENT_TABLE, new RowCountOutputHandler<Integer>(), new Object[0]);
				
				this.values.put("resultMap", runner.query(DBConstants.SELECT_STUDENT_TABLE, new MapOutputHandler()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
	}
	
	public static QueryStructure queryInputHandler1DS(Map<String, Object> values) throws SQLException {
    		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.INSERT_STUDENT_TABLE, new RowCountOutputHandler<Integer>(), new Object[0]);
				
				MapInputHandler input = new MapInputHandler(DBConstants.SELECT_STUDENT_TABLE, null);
				
				this.values.put("resultMap", runner.query(input, new MapOutputHandler()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
	}
	
	public static QueryStructure queryInputHandler2DS(Map<String, Object> values) throws SQLException {
    		return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.INSERT_STUDENT_TABLE, new RowCountOutputHandler<Integer>(), new Object[0]);
				
				MapInputHandler input = new MapInputHandler(DBConstants.SELECT_NAMED_STUDENT_TABLE, new HashMap<String, Object>() {{put("id", 1);}});
				
				this.values.put("resultMap", runner.query(input, new MapOutputHandler()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
	}
	
	public static QueryStructure queryInputHandler3DS(Map<String, Object> values) throws SQLException {
    	return new QueryStructure(values) {

			@Override
			public void create(QueryRunnerService runner) throws SQLException {
			}

			@Override
			public void execute(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.INSERT_STUDENT_TABLE, new RowCountOutputHandler<Integer>(), new Object[0]);
				
				final HashMap<String, Object> tableParams = new HashMap<String, Object>() {{put("id", 1);}};
				final HashMap<String, Object> studentParams = new HashMap<String, Object>() {{put("address", "unknown");}};
				HashMap<String, Map<String, Object>> paramsList = new HashMap<String, Map<String, Object>>() {{put("table", tableParams);put("student", studentParams);}};
				
				MapListInputHandler input = new MapListInputHandler(DBConstants.SELECT_NAMED2_STUDENT_TABLE, paramsList);
				
				this.values.put("resultMap", runner.query(input, new MapOutputHandler()));
			}

			@Override
			public void drop(QueryRunnerService runner) throws SQLException {
				runner.update(DBConstants.DROP_STUDENT_TABLE);
			}
    		
    	};
	}

    public static QueryStructure queryLazyOutputMapList(Map<String, Object> values) throws SQLException {
        return new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                Map<String, Object> insertValues = null;

                for (int i = 0; i < 20; i++) {
                    insertValues = new HashMap<String, Object>();
                    insertValues.put("studentName", "Not me");

                    MapInputHandler input = new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, insertValues);

                    runner.update(input, new RowCountOutputHandler<Integer>());
                    runner.commit();
                }

                MapInputHandler input = new MapInputHandler(DBConstants.SELECT_STUDENT_TABLE_ALL, null);

                this.values.put("resultMapList", runner.query(input, new MapListOutputHandler()));
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.DROP_STUDENT_TABLE);
                runner.commit();
            }

        };
    }

    public static QueryStructure queryLazyOutputHandler(Map<String, Object> values) throws SQLException {
        return new QueryStructure(values) {

            @Override
            public void create(QueryRunnerService runner) throws SQLException {
            }

            @Override
            public void execute(QueryRunnerService runner) throws SQLException {
                Map<String, Object> insertValues = null;

                for (int i = 0; i < 20; i++) {
                    insertValues = new HashMap<String, Object>();
                    insertValues.put("studentName", "Not me");

                    MapInputHandler input = new MapInputHandler(DBConstants.INSERT_NAMED_STUDENT_TABLE, insertValues);

                    runner.update(input, new RowCountOutputHandler<Integer>());
                    runner.commit();
                }

                MapInputHandler input = new MapInputHandler(DBConstants.SELECT_STUDENT_TABLE_ALL, null);

                this.values.put("lazyMapList", runner.query(input, new MapListLazyOutputHandler()));

                this.values.put("lazyBeanList", runner.query(input, new BeanListLazyOutputHandler<Student>(Student.class)));
            }

            @Override
            public void drop(QueryRunnerService runner) throws SQLException {
                runner.update(DBConstants.DROP_STUDENT_TABLE);
                runner.commit();
            }

        };
    }
}
