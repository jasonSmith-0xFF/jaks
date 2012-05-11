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
package com.googlecode.jaks.examples;

import org.junit.Test;

import com.googlecode.jaks.system.Subprocess;
import com.googlecode.jaks.system.SubprocessException;

/**
 * Demonstrates <tt>sayhello</tt>.
 * @author Jason Smith
 */
public class ITSayHello extends AbstractIT
{
	/**
	 * Test the happy path, verifying that if I define a specific <tt>--who</tt>
	 * parameter, it shows up in the output.
	 * <pre>$ sayhello --who World</pre>
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testHappyPathWithSpecificWho() throws SubprocessException, Exception
	{
		assertEquals("Process returned unexpected stdout.", "Hello, World.\n", 
				new Subprocess(
						getCommand("sayhello").getPath(),
						"--who", "World").call((String)null).replace("\r\n", "\n"));
	}
}
