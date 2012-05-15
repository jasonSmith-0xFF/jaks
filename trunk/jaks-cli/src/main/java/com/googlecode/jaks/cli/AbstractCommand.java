package com.googlecode.jaks.cli;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

import joptsimple.OptionParser;

@JaksCommand
public abstract class AbstractCommand 
{
	@JaksOption(name = {"e", "verbose-errors"},description="Errors show full stack trace.")
	public boolean verboseErrors = false;
	
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
	
	public void startCommand(final String... args)
	{
		try
		{
			final OptionProcessor proc = new OptionProcessor();
			if(args.length > 0 && Arrays.asList("-h", "--help").contains(args[0]))
			{
				printHelp(proc);
			}
			else
			{
				proc.process(this, args);
				execute();
			}
		}
		catch(final Exception e)
		{
			if(verboseErrors)
			{
				e.printStackTrace(System.err);
			}
			else
			{
				Throwable t = e;
				while(t != null)
				{
					if(!(t instanceof InvocationTargetException))
					{
						System.err.println(t.toString());
					}
					t = t.getCause();
				}
			}
			Runtime.getRuntime().exit(1);
		}
		catch(final Error e)
		{
			e.printStackTrace(System.err);
			Runtime.getRuntime().exit(2);
		}
	}
	
	protected void printHelp(final OptionProcessor proc) throws IOException
	{
		final OptionParser parser = proc.initializeOptionParser(this);
		System.out.println(getLaunchScript().getName().split("\\.")[0] + " [options]");
		System.out.println();
		parser.printHelpOn(System.out);
	}
	
	protected File getLaunchScript()
	{
		return new File(System.getProperty("jaks.launch.script"));
	}
}
