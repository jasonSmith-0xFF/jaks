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

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.googlecode.jaks.common.io.StreamUtil;
import com.googlecode.jaks.examples.AbstractIT;
import com.googlecode.jaks.examples.test.TypedNonOptionArguments;
import com.googlecode.jsaf.system.Subprocess;

/**
 * Tests for {@link TypedNonOptionArguments}.
 * @author Jason Smith
 */
public class TypedNonOptionArgumentsIT extends AbstractIT
{
	/**
	 * Verify that calling the subprocess with a list of files returns the expected results.
	 * I'm passing all the files in the scripts folder, and expecting the stdout to be 
	 * a listing of them, one on each line returned.
	 * @throws Exception See {@link Exception}.
	 */
	@Test
	public void testInterpretAsFile() throws Exception
	{
		final Subprocess sp = new Subprocess(getCommand("typednonoptionarguments").getPath());
		for(final File file : getCommand("typednonoptionarguments").getParentFile().listFiles())
		{
			sp.add(file.getPath());
		}
		
		final String results = sp.call(StreamUtil.DEFAULT_CHARSET);
		assertFalse("Subprocess returned empty results.", StringUtils.isEmpty(results));
		final String[] lines = results.split("\\r?\\n");
		assertTrue("Not enough results returned.", lines.length > 5);
		
		for(final String result : results.split("\\r?\\n"))
		{
			if(!StringUtils.isEmpty(result))
			{
				assertTrue("I expected the file to exist, but didn't find it.", new File(result).isFile());
			}
		}
	}
}
