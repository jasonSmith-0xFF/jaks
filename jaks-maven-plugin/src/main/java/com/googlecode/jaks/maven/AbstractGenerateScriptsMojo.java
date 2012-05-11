package com.googlecode.jaks.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.StringUtils;

import com.googlecode.jaks.cli.AbstractCommand;
import com.googlecode.jaks.cli.JSAFMain;
import com.googlecode.jaks.common.io.StreamUtil;

import fmpp.util.FileUtil;

/**
 * Generate launch scripts.
 * @author Jason Smith
 */
public abstract class AbstractGenerateScriptsMojo extends AbstractJSAFMojo
{
	/**
	 * The command launcher.
	 * <br/><strong>Default</strong>: <tt>{@link JSAFMain}</tt>
	 * @parameter
	 */
	protected final Class<?> launcher = JSAFMain.class;
	
	/**
	 * Location of launch-scripts and any other executables.
	 * <br/><strong>Alias</strong>:<tt>bin-folder</tt>
	 * <br/><strong>Default</strong>: <tt>bin</tt>
	 * @parameter alias="bin-folder"
	 */
	protected final String binFolder = "bin";

	
	protected abstract String getClassPath() throws Exception;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException 
	{
		try
		{
			final Map<String,Class<?>> commands = getCommands();
			for(final String command : commands.keySet())
			{
				final Class<?> commandClass = commands.get(command);
				final File script = new File(new File(getGeneratedAppFolder(), binFolder), adjustScriptFileNameToOS(command));
				getLog().info(script.getPath());
				script.getParentFile().mkdirs();
				StreamUtil.transfer(
						new StringReader(getScript(command, commandClass)), 
						new OutputStreamWriter(new FileOutputStream(script), StreamUtil.UTF8));
			}
		}
		catch(final Exception e)
		{
			throw new MojoExecutionException("I've lost me mojo! " + e.toString(), e);
		}
	}
	
	protected abstract String adjustScriptFileNameToOS(final String command);
	
	protected abstract String getScript(final String command, final Class<?> commandClass) throws Exception;
	
	protected String getRelPathFromBinToLib() throws IOException
	{
		File lib = new File(getGeneratedAppFolder(), libFolder);
		File bin = new File(getGeneratedAppFolder(), binFolder);
		return FileUtil.getRelativePath(bin, lib);
	}
	
	protected Map<String,Class<?>> getCommands() throws Exception
	{
		final Map<String,List<Class<?>>> rawCommands = new HashMap<String,List<Class<?>>>();
		for(final Class<?> commandClass : getCommandClasses())
		{
			final String name = getCommandName(commandClass);
			if(!rawCommands.containsKey(name))
			{
				rawCommands.put(name, new ArrayList<Class<?>>());
			}
			rawCommands.get(name).add(commandClass);
		}
		
		for(final List<Class<?>> classes : rawCommands.values())
		{
			trimCommands(classes);
		}
		
		final Map<String,Class<?>> results = new TreeMap<String,Class<?>>();
		for(final String name : rawCommands.keySet())
		{
			for(final Class<?> commandClass : rawCommands.get(name))
			{
				String adjustedName = name;
				int count = 0;
				while(results.containsKey(adjustedName))
				{
					adjustedName = name + "-" + count;
					count++;
				}
				results.put(adjustedName, commandClass);
			}
		}
		
		return results;
	}

	private List<Class<?>> getCommandClasses() throws IOException, ClassNotFoundException
	{
		final List<URL> urls = new ArrayList<URL>();
		for(final Artifact artifact : getCompileArtifacts())
		{
			urls.add(artifact.getFile().toURI().toURL());
		}
		final URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
		
		final List<Class<?>> commandClasses = new ArrayList<Class<?>>();
		for(final URL commandClassDocument : Collections.list(cl.getResources("META-INF/jsaf/command-classes.txt")))
		{
			try(final LineNumberReader reader = new LineNumberReader(new InputStreamReader(commandClassDocument.openStream(), StreamUtil.UTF8)))
			{
				for(;;)
				{
					final String line = reader.readLine();
					if(line == null)
					{
						break;
					}
					if(!StringUtils.isEmpty(line))
					{
						commandClasses.add(Class.forName(line, true, cl));
					}
				}
			}
		}
		
		return commandClasses; 
	}
	
	private String getCommandName(final Class<?> commandClass) throws InstantiationException, IllegalAccessException
	{
		final AbstractCommand command = (AbstractCommand)commandClass.newInstance();
		return command.getCommandName();
	}
	
	
	private void trimCommands(final List<Class<?>> commandClasses)
	{
		final List<Class<?>> copy = new ArrayList<Class<?>>(commandClasses);
		for(final Class<?> a : copy)
		{
			for(final Class<?> b : copy)
			{
				if(!a.getCanonicalName().equals(b.getCanonicalName()) && a.isAssignableFrom(b))
				{
					commandClasses.remove(a);
				}
			}
		}
	}
}
