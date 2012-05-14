/*
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
package com.googlecode.jaks.examples.test;

import org.junit.Test;

import com.googlecode.jaks.common.io.StreamUtil;
import com.googlecode.jaks.examples.AbstractIT;
import com.googlecode.jaks.system.Subprocess;
import com.googlecode.jaks.system.SubprocessException;

/**
 * Tests for {@link TypedSingleNonOptionArgumentNoDefault}.
 * @author Jason Smith
 */
public class TypedSingleNonOptionArgumentNoDefaultIT extends AbstractIT
{
	/**
	 * Verify that when I pass in a single non-option argument, it is passed back correctly.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testPassOneValue() throws Exception
	{
		assertEquals("Returned the wrong value.", 
				"42", 
				new Subprocess(getCommand("typedsinglenonoptionargumentnodefault").getPath(), "42").call(StreamUtil.DEFAULT_CHARSET));
	}
	
	/**
	 * The command specifies a single value, not a collection or array; verify that when I pass more than one
	 * value, the command throws an exception.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=SubprocessException.class)
	public void testPassTwoValuesNotAllowed() throws Exception
	{
		new Subprocess(getCommand("typedsinglenonoptionargumentnodefault").getPath(), "42", "43").call(StreamUtil.DEFAULT_CHARSET);
	}
	
	/**
	 * Defined single non-option argument with no default defaults to {@code null}.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testPassZeroValues() throws Exception
	{
		assertEquals("No default and value not defined; expected 'null.'", 
				"null", 
				new Subprocess(getCommand("typedsinglenonoptionargumentnodefault").getPath()).call(StreamUtil.DEFAULT_CHARSET));
	}
	
	/**
	 * Verify that command throws exception if there is a conversion error.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=SubprocessException.class) 
	public void testBadConversion() throws Exception
	{
		new Subprocess(getCommand("typedsinglenonoptionargumentnodefault").getPath(), "wont-convert").call(StreamUtil.DEFAULT_CHARSET);
	}
}
