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

import com.googlecode.jaks.cli.AbstractCommand;
import com.googlecode.jaks.cli.JaksNonOptionArguments;

/**
 * Takes a single {@link int} as a non-option argument and returns it
 * via stdout.
 * @author Jason Smith
 */
public class TypedSingleNonOptionPrimitiveArgument extends AbstractCommand
{
	@JaksNonOptionArguments
	public int value = 256;
	
	@Override
	public void execute() throws Exception 
	{
		System.out.print(value);
	}
}
