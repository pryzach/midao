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

package org.midao.jdbc.core.handlers.output;

import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
public class BaseOutputHandlerTest {
    protected List<QueryParameters> paramsList;
    protected List<QueryParameters> emptyList = new ArrayList<QueryParameters>();
    protected QueryParameters params;
    protected Character zoe;

    protected void init() {
        QueryParameters param1 = new QueryParameters().set("name", "jack").set("occupation", "sheriff").set("age", 36);
        QueryParameters param2 = new QueryParameters().set("name", "henry").set("occupation", "mechanic").set("age", 36);
        QueryParameters param3 = new QueryParameters().set("name", "alison").set("occupation", "agent").set("age", 30);

        params = new QueryParameters();
        params.set("name", "zoe");
        params.set("occupation", "child");
        params.set("age", 15);

        paramsList = Arrays.asList(new QueryParameters(), param1, param2, param3);

        zoe = new Character();
        zoe.setName("zoe");
        zoe.setAge(15);
        zoe.setOccupation("child");
    }

    public static class Character {
        private String name;
        private String occupation;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }
}
