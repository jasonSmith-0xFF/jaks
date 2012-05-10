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
package com.googlecode.jsaf.system;

import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestSubprocess extends Assert
{
	@Test
	public void testLs() throws Exception
	{
		if(SystemUtils.IS_OS_WINDOWS)
		{
			new Subprocess("dir").call();
		}
		else
		{
			new Subprocess("ls", "-al").call();
		}
	}
}
