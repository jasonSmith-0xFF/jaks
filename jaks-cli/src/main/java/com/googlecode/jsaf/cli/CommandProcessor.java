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
package com.googlecode.jsaf.cli;

import java.util.Arrays;

import joptsimple.OptionParser;

public class CommandProcessor 
{
	public static void Main(String... args)
	{
		OptionParser parser = new OptionParser();
		parser.acceptsAll(Arrays.asList("a", "all"), "Description for all.");
		parser.acceptsAll(Arrays.asList("b", "bah"), "Description for bah.").withRequiredArg().ofType(Integer.class);
	}
}
