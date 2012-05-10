package com.googlecode.jsaf.cli;

import java.util.Locale;

@JSAFCommand
public abstract class AbstractCommand 
{
	/**
	 * Gets the name of this command. The default implementation uses the class name, all lower case,
	 * with '_' converted to '-'. If the command is an inner class, the '$' in the name are converted to 
	 * '.'. This is used by Maven during the generation of scripts.
	 * @return The name of this command.
	 */
	public String getCommandName()
	{
		final String[] pathElts = getClass().getName().split("\\.");
		final String simpleName = pathElts[pathElts.length-1];
		return simpleName.toLowerCase(Locale.getDefault()).replace('$', '.').replace('_', '-');
	}
	
	public abstract void execute() throws Exception;
}
