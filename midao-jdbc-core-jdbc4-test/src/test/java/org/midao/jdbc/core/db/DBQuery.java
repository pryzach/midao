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

import org.midao.jdbc.core.handlers.output.BeanListLazyOutputHandler;
import org.midao.jdbc.core.handlers.output.MapListLazyOutputHandler;
import org.midao.jdbc.core.service.QueryRunnerService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DBQuery extends BaseDB {
	public static void queryOutputHandlerWParamsDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryOutputHandlerDS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryInputHandler1DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryInputHandler2DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}
	
	public static void queryInputHandler3DS(QueryStructure structure, QueryRunnerService runner) throws SQLException {
		try {
			structure.create(runner);
		
			structure.execute(runner);
		
			Map<String, Object> result = (Map<String, Object>) structure.values.get("resultMap");
			assertEquals("Not me", result.get(result.keySet().toArray()[0]).toString());
		} finally {
			structure.drop(runner);
		}
	}

    public static void queryLazyOutputMapList(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        try {
            structure.create(runner);

            structure.execute(runner);

            List<Map<String, Object>> result = (List<Map<String, Object>>) structure.values.get("resultMapList");
            assertEquals("Not me", result.get(0).get("name").toString());

            for (int i = 0; i < 20; i++) {
                assertEquals(Integer.toString(i + 1), result.get(i).get("id").toString());
            }
        } finally {
            structure.drop(runner);
        }
    }

    public static void queryLazyOutputHandler(QueryStructure structure, QueryRunnerService runner) throws SQLException {
        try {
            structure.create(runner);

            structure.execute(runner);

            MapListLazyOutputHandler resultMap = (MapListLazyOutputHandler) structure.values.get("lazyMapList");
            assertEquals("Not me", resultMap.getNext().get("name").toString());

            int i = 1;

            while (resultMap.hasNext() == true) {
                assertEquals(Integer.toString(i + 1), resultMap.getNext().get("id").toString());

                i++;
            }

            BeanListLazyOutputHandler<Student> resultBean = (BeanListLazyOutputHandler<Student>) structure.values.get("lazyBeanList");
            assertEquals("Not me", resultBean.getNext().getName());

            i = 1;

            while (resultBean.hasNext() == true) {
                assertEquals(Integer.toString(i + 1), resultBean.getNext().getId().toString());

                i++;
            }

            resultBean.close();
            resultMap.close();
        } finally {
            structure.drop(runner);
        }
    }
}
