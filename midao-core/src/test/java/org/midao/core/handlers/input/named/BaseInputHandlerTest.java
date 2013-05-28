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

package org.midao.core.handlers.input.named;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BaseInputHandlerTest extends TestCase {
	protected String encodedSingleParameterQuery = "SELECT ID FROM CATS WHERE AGE=:cat.age AND NAME = :cat.name";
	protected String encodedMultipleParameterQuery = "SELECT * FROM cats FULL JOIN dogs ON cats.owner=dogs.owner WHERE (cats.AGE=:cat.age AND cats.NAME = :cat.name) or (dogs.weight = :dog.weight AND dogs.breed = :dog.breed AND dogs.age = :dog.age)";
	protected String encodedShortParameterQuery = "SELECT ID FROM CATS WHERE AGE=:age AND NAME = :name";
	
	protected String decodedSingleParameterQuery = "SELECT ID FROM CATS WHERE AGE=? AND NAME = ?";
	protected String decodedMultipleParameterQuery = "SELECT * FROM cats FULL JOIN dogs ON cats.owner=dogs.owner WHERE (cats.AGE=? AND cats.NAME = ?) or (dogs.weight = ? AND dogs.breed = ? AND dogs.age = ?)";
	protected String decodedShortParameterQuery = "SELECT ID FROM CATS WHERE AGE=? AND NAME = ?";
	
	protected String simpleQuery = "SELECT * FROM DUAL";
	
	protected Cat cat = new Cat();
	protected Dog dog = new Dog();
	
	protected Map<String, Object> catMap = new HashMap<String, Object>() {
		{put("age", cat.getAge());put("name", cat.getName());}
	};
	
	protected Map<String, Object> dogMap = new HashMap<String, Object>() {
		{put("weight", dog.getWeight());put("breed", dog.getBreed());put("age", dog.getAge());}
	};
	
	protected Object[] singleParameterQueryParameters = Arrays.asList(cat.getAge(), cat.getName()).toArray();
	protected Object[] multipleParameterQueryParameters = Arrays.asList(cat.getAge(), cat.getName(), dog.getWeight(), dog.getBreed(), dog.getAge()).toArray();
	
	protected boolean contains(Object[] array, Object value) {
		return Arrays.asList(array).contains(value);
	}
	
	protected class Cat extends Pet {
		private int age = 5;
		private String name = "whiskers";
		
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	protected class Dog extends Pet {
		private int weight = 17;
		private String breed = "Blue Lacy";
		private int age = 3;
		
		public int getWeight() {
			return weight;
		}
		public void setWeight(int weight) {
			this.weight = weight;
		}
		public String getBreed() {
			return breed;
		}
		public void setBreed(String breed) {
			this.breed = breed;
		}
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
	}
	
	protected class Pet {
		
	}
}
