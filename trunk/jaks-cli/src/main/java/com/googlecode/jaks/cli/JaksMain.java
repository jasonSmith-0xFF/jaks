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
package com.googlecode.jaks.cli;

/**
 * The standard entry-point for Jaks scripts.
 * @author Jason Smith
 */
public class JaksMain 
{
	/**
	 * Launch a Jaks {@link AbstractCommand}.
	 * @param args The arguments from the command-line.
	 * @throws Exception See {@link Exception}.
	 */
	public static void main(String... args) throws Exception
	{
		final Class<?> commandClass = Class.forName(System.getProperty("jaks.command.class"), true, JaksMain.class.getClassLoader());
		final AbstractCommand command = (AbstractCommand)commandClass.newInstance();
		
		final OptionProcessor proc = new OptionProcessor();
		proc.process(command, args);
		
		command.execute();
	}
}
