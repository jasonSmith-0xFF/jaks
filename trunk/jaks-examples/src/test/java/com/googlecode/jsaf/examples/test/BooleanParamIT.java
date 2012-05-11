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
package com.googlecode.jsaf.examples.test;

import org.junit.Test;

import com.googlecode.jaks.common.io.StreamUtil;
import com.googlecode.jaks.examples.AbstractIT;
import com.googlecode.jsaf.system.Subprocess;
import com.googlecode.jsaf.system.SubprocessException;

/**
 * Tests for {@link BooleanParam}.
 * TODO: Test for error if non-option parameters are present. Currently not working.
 * @author Jason Smith
 */
public class BooleanParamIT extends AbstractIT
{
	/**
	 * Setting the {@code --test} flag results in "true".
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSet() throws Exception
	{
		assertEquals("Process returned unexpected stdout.", 
				"true", 
				new Subprocess(getCommand("booleanparam").getPath(), "--test").call(StreamUtil.DEFAULT_CHARSET));
	}
	
	/**
	 * Not setting the {@code --test} flag results in "false".
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testClear() throws Exception
	{
		assertEquals("Process returned unexpected stdout.", 
				"false", 
				new Subprocess(getCommand("booleanparam").getPath()).call(StreamUtil.DEFAULT_CHARSET));
	}
	
	/**
	 * Setting the {@code --test} flag is okay, and still results in "true".
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testSet2() throws Exception
	{
		assertEquals("Process returned unexpected stdout.", 
				"true", 
				new Subprocess(getCommand("booleanparam").getPath(), "--test", "--test").call(StreamUtil.DEFAULT_CHARSET));
	}
	
	/**
	 * Setting the {@code --illegal-option} flag results in an error.
	 * @throws SubprocessException Expected result.
	 * @throws Exception See {@link Exception}.
	 */
	@Test(expected=SubprocessException.class)
	public void testIllegalOption() throws SubprocessException, Exception
	{
		new Subprocess(getCommand("booleanparam").getPath(), "--illegal-option").call(StreamUtil.DEFAULT_CHARSET);
	}
}
