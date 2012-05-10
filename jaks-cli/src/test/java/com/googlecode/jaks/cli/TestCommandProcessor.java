/**
 * Copyright (C) 2012 by Jason Smith
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.jaks.cli;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import com.googlecode.jaks.cli.JSAFOption;
import com.googlecode.jaks.cli.OptionProcessor;

public class TestCommandProcessor extends Assert
{
	protected final OptionProcessor proc = new OptionProcessor();

	public static class ExampleCommand1 
	{
		@JSAFOption(name={"t","testbool"}, description="Test boolean.")
		public boolean testBool;
	}
	
	@Test
	public void testBoolean() throws Exception
	{
		assertTrue("Didn't detect --testbool, although it's there.", 
				proc.process(new ExampleCommand1(), "--testbool").testBool);
		assertFalse("Detected --testbool when not specified.", 
				proc.process(new ExampleCommand1()).testBool);
	}  
	
	public static class ExampleCommand2 
	{
		@JSAFOption(name={"s","teststr"}, description="Test string.")
		public String testString;
	}
	
	@Test
	public void testString() throws Exception
	{
		assertEquals("Did not return expected value.",
				"Hello, world.",
				proc.process(new ExampleCommand2(), "-s", "Hello, world.").testString);
	}
	
	public static class ExampleCommand3 
	{
		@JSAFOption(name={"s","teststr"}, description="Test string.", separator=',')
		public String[] testString;
	}
	
	@Test public void testStringArray() throws Exception
	{
		assertArrayEquals("", 
				new String[] {"a", "b", "c"}, 
				proc.process(new ExampleCommand3(), "-s", "a,b", "-s", "c").testString);
	}
	
	public static class ExampleCommand4 
	{
		@JSAFOption(name={"s","teststr"}, description="Test string.", separator=',')
		public Set<String> testString;
	}

	@Test public void testSetOfString() throws Exception
	{
		assertTrue("Didn't get back the expected results.", 
				CollectionUtils.isEqualCollection(
						Arrays.asList("a", "b", "c"), 
						proc.process(new ExampleCommand4(), "-s", "a,b", "-s", "c").testString));
	}

	public static class ExampleCommand5 
	{
		@JSAFOption(name={"i","testint"}, description="Test int.", separator=',')
		public LinkedList<Integer> testInt;
	}

	@Test public void testLinkedListOfInt() throws Exception
	{
		assertTrue("Didn't get back the expected results.", 
				CollectionUtils.isEqualCollection(
						Arrays.asList(1, 2, 3), 
						proc.process(new ExampleCommand5(), "-i", "1,2", "-i", "3").testInt));
	}
	
	public static class ExampleCommand6 
	{
		@JSAFOption(name={"i","testint"}, description="Test int.", separator=',')
		public List<Integer> testInt;
	}
	
	@Test public void testListOfInt() throws Exception
	{
		assertTrue("Didn't get back the expected results.", 
				CollectionUtils.isEqualCollection(
						Arrays.asList(1, 2, 3), 
						proc.process(new ExampleCommand5(), "-i", "1,2", "-i", "3").testInt));
	}

}
