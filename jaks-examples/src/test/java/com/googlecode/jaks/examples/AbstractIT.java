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

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.junit.Assert;

public abstract class AbstractIT extends Assert
{
	protected File getBuildDirectory()
	{
		return new File(System.getProperty("project.build.directory"));
	}
	
	protected File getCommand(final String command) throws IOException
	{
		return new File(getBuildDirectory(), "installation/bin/" + command + (SystemUtils.IS_OS_WINDOWS?".bat":"")).getCanonicalFile();
	}
}
