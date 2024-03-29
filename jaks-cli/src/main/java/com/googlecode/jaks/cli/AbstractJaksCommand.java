package com.googlecode.jaks.cli;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

import joptsimple.OptionParser;

import com.googlecode.jaks.common.l10n.Strings;

/**
 * The abstract, base command class which concrete commands must extend.
 * @author Jason Smith
 */
@JaksCommand
public abstract class AbstractJaksCommand 
{
	private static AbstractJaksCommand primaryCommand = null;
	
	/**
	 * If set, errors will show the full stack trace and not just a summary.
	 */
	@JaksOption(name = {"e", "@{CMD_VERBOSE_ERRORS}"},description="@{DESC_VERBOSE_ERRORS}")
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
	
	/**
	 * Overridden in the command class to implement the logic of the command.
	 * @throws Exception The command is expected to be able to throw any type of {@link Exception}.
	 */
	public abstract void execute() throws Exception;
	
	/**
	 * Called from {@link JaksMain} to start the command.
	 * @param args The arguments passed from the command line.
	 */
	public void startCommand(final String... args)
	{
		if(primaryCommand == null)
		{
			primaryCommand = this;
		}
		try
		{
			if(args.length > 0 && Arrays.asList("-h", "--help").contains(args[0]))
			{
				printHelp();
			}
			else
			{
				new OptionProcessor().process(this, getLocale(), args);
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
	
	/**
	 * Return the locale to be used for internationalization. The default implementation
	 * returns {@link Locale#getDefault()}.
	 * @return The locale to be used for internationalization.
	 */
	public Locale getLocale()
	{
		return Locale.getDefault();
	}
	
	/**
	 * Print help for this command.
	 * @param proc The option-processor.
	 * @throws IOException See {@link IOException}.
	 */
	protected void printHelp() throws IOException
	{
		final OptionParser parser = new OptionProcessor().initializeOptionParser(this, getLocale());
		System.out.println(getCommandName() + " [options]");
		System.out.println();
		parser.printHelpOn(System.out);
	}
	
	/**
	 * Get the full path to the script file this command was launched from.
	 * @return The full path to the script file.
	 */
	public File getLaunchScript()
	{
		return new File(System.getProperty("jaks.launch.script"));
	}
	
	/**
	 * Short form of {@link String#format(Locale, String, Object...)} where the template
	 * is processed by the MVEL2 template engine. <tt>object</tt> is the currently running command instance.
	 * Localization strings are available by name. Ex., <tt>@{MSG_COMMAND_NAME}</tt> will find
	 * the property value "MSG_COMMAND_NAME" from the localization data.
	 * @param template MVEL2 template or literal string.
	 * @param values Values passed in to be formatted.
	 * @return The formatted text.
	 * @throws IOException See {@link IOException}.
	 */
	public String format(final String template, final Object... values) throws IOException
	{
		return Strings.format(this, getLocale(), template, values);
	}
	
	
	/**
	 * Keeps track of the first command executed (normally there is only ever one); can be used as a reference object.
	 * @return The first command executed.
	 */
	public static AbstractJaksCommand getPrimaryCommand()
	{
		return primaryCommand;
	}
}
