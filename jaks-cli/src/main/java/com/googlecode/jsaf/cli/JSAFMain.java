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


public class JSAFMain 
{
	public static void main(String... args) throws Exception
	{
		final Class<?> commandClass = Class.forName(System.getProperty("jsaf.command.class"), true, JSAFMain.class.getClassLoader());
		AbstractCommand command = (AbstractCommand)commandClass.newInstance();
		
		final OptionProcessor proc = new OptionProcessor();
		proc.process(command, args);
		
		command.execute();
	}
}
