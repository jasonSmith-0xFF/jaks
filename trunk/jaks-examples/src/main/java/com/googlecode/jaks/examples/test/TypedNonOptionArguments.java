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
import java.util.List;

import com.googlecode.jaks.cli.AbstractJaksCommand;
import com.googlecode.jaks.cli.JaksNonOption;

/**
 * This command demonstrates a common use of non-option arguments - a list of files.
 * @author Jason Smith
 */
public class TypedNonOptionArguments extends AbstractJaksCommand
{
	/**
	 * The non-option arguments interpreted as a list of files.
	 */
	@JaksNonOption
	public List<File> files;
	
	@Override
	public void execute() throws Exception 
	{
		for(final File file : files)
		{
			System.out.println(file.getCanonicalPath());
		}
	}
}
