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

import com.googlecode.jaks.cli.AbstractJaksCommand;
import com.googlecode.jaks.cli.JaksNonOption;

/**
 * This command demonstrates a single non-option argument with a defined default value.
 * @author Jason Smith
 */
public class TypedSingleNonOptionArgument extends AbstractJaksCommand
{
	/**
	 * Command expects a single non-option argument, an integer.
	 */
	@JaksNonOption
	public Integer value = 256;
	
	@Override
	public void execute() throws Exception 
	{
		System.out.print(value);
	}
}
