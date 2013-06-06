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

package org.midao.jdbc.core.handlers.input.named;

import junit.framework.Assert;
import org.junit.Test;
import org.midao.jdbc.core.MidaoConfig;
import org.midao.jdbc.core.handlers.input.named.AbstractNamedInputHandler;
import org.midao.jdbc.core.handlers.model.QueryParameters;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class AbstractNamedInputHandlerTest {
    @Test
    public void testUpdateMap() throws Exception {
        Map<String, Object> washMap = new HashMap<String, Object>();
        washMap.put("name", "Wash");
        washMap.put("skill", "Pilot");
        washMap.put("number", 7);

        Map<String, Object> jayneMap = new HashMap<String, Object>();
        jayneMap.put("name", "Jayne");
        jayneMap.put("skill", "Hired gun");
        jayneMap.put("number", 5);

        Map<String, Object> jayneMapClone = new TestNamedInputHandler<Character>().updateMap(washMap, jayneMap);

        Assert.assertEquals("Jayne", jayneMapClone.get("name"));
        Assert.assertEquals("Hired gun", jayneMapClone.get("skill"));
        Assert.assertEquals(5, ((Integer) jayneMapClone.get("number")).intValue());
    }

    @Test
    public void testUpdateBean() throws Exception {
        Character mal = new Character();
        mal.setName("Mal");
        mal.setSkill("Sergeant/Smuggler");
        mal.setNumber(57);

        Map<String, Object> zoeMap = new HashMap<String, Object>();
        zoeMap.put("name", "Zoe");
        zoeMap.put("skill", "Corporal/Warrior woman");
        zoeMap.put("number", 57);

        Character zoe = new TestNamedInputHandler<Character>().updateBean(mal, zoeMap);

        Assert.assertEquals("Zoe", zoe.getName());
        Assert.assertEquals("Corporal/Warrior woman", zoe.getSkill());
        Assert.assertEquals(57, zoe.getNumber().intValue());
    }

    public class TestNamedInputHandler<T> extends AbstractNamedInputHandler<T> {

        protected TestNamedInputHandler() {
            super(MidaoConfig.getDefaultQueryInputProcessor());
        }

        @Override
        public <T1 extends Object> T1 updateInput(QueryParameters updatedInput) {
            return null;
        }

        @Override
        public String getEncodedQueryString() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public QueryParameters getQueryParameters() {
            return null;
        }

        @Override
        protected Map<String, Object> updateMap(Map<String, Object> target, Map<String, Object> source) {
            return super.updateMap(target, source);
        }

        @Override
        protected T updateBean(T object, Map<String, Object> source) {
            return super.updateBean(object, source);
        }
    }

    public static class Character {
        String name;
        String skill;
        Integer number;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSkill() {
            return skill;
        }

        public void setSkill(String skill) {
            this.skill = skill;
        }

        public Integer getNumber() {
            return number;
        }

        public void setNumber(Integer number) {
            this.number = number;
        }
    }
}
