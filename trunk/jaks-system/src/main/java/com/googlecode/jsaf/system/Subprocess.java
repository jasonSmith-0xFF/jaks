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
package com.googlecode.jsaf.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.googlecode.jsaf.common.io.MultiOutputStream;
import com.googlecode.jsaf.common.io.SquashedOutputStream;
import com.googlecode.jsaf.common.io.StreamUtil;

/**
 * {@link Subprocess} lets you launch an external process, optionally passing stdin data to 
 * the process, and optionally receiving stdout data from the process. A number of different
 * versions of {@link #call()} are available, letting you deal with the data directly in whatever
 * form is most convenient for you ({@link String}, {@link byte}, or streaming). It's 
 * like {@link java.lang.Process}, only a lot easier.
 * @author Jason Smith
 */
public class Subprocess extends ArrayList<String>
{
	private static final long serialVersionUID = 2782934671390653885L;
	
	private final File workingFolder;
	
	private final Map<String,String> deltaEnv;
	
	/**
	 * Default constructor.
	 * @throws IOException See {@link IOException}.
	 */
	public Subprocess() throws IOException 
	{
		this(new File("."), new ArrayList<String>());
	}

	public Subprocess(final Collection<? extends String> elements) throws IOException 
	{
		this(new File("."), null, elements);
	}
	
	public Subprocess(final String... elements) throws IOException
	{
		this(Arrays.asList(elements));
	}
	
	public Subprocess(final File workingFolder) throws IOException 
	{
		this(workingFolder, new ArrayList<String>());
	}

	public Subprocess(final File workingFolder, final Collection<? extends String> elements) throws IOException 
	{
		this(workingFolder, null, elements);
	}
	
	public Subprocess(final File workingFolder, final String... elements) throws IOException 
	{
		this(workingFolder, null, Arrays.asList(elements));
	}
	
	public Subprocess(final File workingFolder, final Map<String,String> deltaEnv) throws IOException 
	{
		super();
		if(!workingFolder.isDirectory())
		{
			throw new FileNotFoundException(workingFolder.getPath());
		}
		this.workingFolder = workingFolder.getCanonicalFile();
		this.deltaEnv = deltaEnv==null?new HashMap<String,String>():deltaEnv;
	}

	public Subprocess(final File workingFolder, final Map<String,String> deltaEnv, final Collection<? extends String> elements) throws IOException 
	{
		super(elements);
		if(!workingFolder.isDirectory())
		{
			throw new FileNotFoundException(workingFolder.getPath());
		}
		this.workingFolder = workingFolder.getCanonicalFile();
		this.deltaEnv = deltaEnv==null?new HashMap<String,String>():deltaEnv;
	}
	
	public Subprocess(final File workingFolder, final Map<String,String> deltaEnv, final String... elements) throws IOException 
	{
		this(workingFolder, deltaEnv, Arrays.asList(elements));
	}
	
	/**
	 * Call a process and return stdout as a string.
	 * @param encoding String encoding. {@code null} to use system default encoding.
	 * @return Stdout as a string.
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	public String call(final String encoding) throws SubprocessException, Exception
	{
		return new String(call(), encoding==null?Charset.defaultCharset().name():encoding);
	}
	
	/**
	 * Call a process with stdin as a string and returning stdout as a string.
	 * @param stdin String data passed to the process as stdin.
	 * @param encoding String encoding. {@code null} to use system default encoding.
	 * @return Stdout as a string.
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	public String call(final String stdin, final String encoding) throws SubprocessException, Exception
	{
		return new String(call(stdin.getBytes()), encoding==null?Charset.defaultCharset().name():encoding);
	}
	
	/**
	 * Call a process with stdin as a byte-array and return stdout as a string.
	 * @param stdin Byte data passed to the process as stdin.
	 * @param encoding String encoding. {@code null} to use system default encoding.
	 * @return Stdout as a string.
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	public String call(final byte[] stdin, final String encoding) throws SubprocessException, Exception
	{
		return new String(call(stdin), encoding==null?Charset.defaultCharset().name():encoding);
	}
	
	/**
	 * Call a process and return stdout as a byte-array.
	 * @return Stdout as a byte-array.
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	public byte[] call() throws SubprocessException, Exception
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		call(null, out, new SquashedOutputStream());
		return out.toByteArray();
	}
	
	/**
	 * Call a process with stdin as a byte-array and return stdout as a byte-array.
	 * @param stdin Byte data passed to the process as stdin.
	 * @return Stdout as a byte-array.
	 * @throws SubprocessException See {@link SubprocessException}.
	 * @throws Exception See {@link Exception}.
	 */
	public byte[] call(final byte[] stdin) throws SubprocessException, Exception
	{
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(final ByteArrayInputStream in = new ByteArrayInputStream(stdin))
		{
			call(in, out, new SquashedOutputStream());
		}
		return out.toByteArray();
	}
	
	public void call(final OutputStream stdout) throws SubprocessException, Exception
	{
		call(null, stdout, new SquashedOutputStream());
	}
	
	public void call(final InputStream stdin, final OutputStream stdout) throws SubprocessException, Exception
	{
		call(stdin, stdout, new SquashedOutputStream());
	}
	
	public void call(final OutputStream stdout, final OutputStream stderr) throws SubprocessException, Exception
	{
		call(null, stdout, stderr);
	}
	
	public void call(
    		final InputStream stdin, 
			final OutputStream stdout, 
    		final OutputStream stderr) throws SubprocessException, Exception
	{
		final ByteArrayOutputStream err = new ByteArrayOutputStream();
		OutputStream errs = new MultiOutputStream(stderr==null?System.err:stderr, err);
		final int result = execute(stdin, stdout, errs);
		if(result != 0)
		{
			String message = null;
			try
			{
				message = new String(err.toByteArray(), StreamUtil.UTF8);
			}
			catch(final Exception e)
			{
				message = new String(err.toByteArray(), StreamUtil.US_ASCII);
			}
			throw new SubprocessException(this, result, workingFolder.getAbsolutePath(), message);
		}
	}

    public int execute() throws Exception
	{
    	return execute(null, new SquashedOutputStream(), new SquashedOutputStream());
	}
	
    public int execute(final OutputStream stdout) throws Exception
	{
    	return execute(null, stdout, new SquashedOutputStream());
	}
	
    public int execute(
    		final InputStream stdin, 
    		final OutputStream stdout) throws Exception
	{
    	return execute(stdin, stdout, new SquashedOutputStream());
	}

	/**
     * Executes a command, waiting for it to finish.
     * 
     * <p>If stdout or stderr are not specified, they default to 
     * {@linkplain System#out} and {@linkplain System#err}, respectively.
     * If stdin is not defined, it defaults to {@code null}.</p>
     *
     * @param command The command to execute.
     * @param stdin stdin Available for the process to read.
     * @param stdout stdout From the process (defaults to {@linkplain System#out} if {@code null}).
     * @param stderr stderr From the process (defaults to {@linkplain System#err} if {@code null}).
     * @return The exit code from the process.
     * @throws IOException See {@link IOException}.
     */
    public int execute(
    		final InputStream stdin, 
    		final OutputStream stdout, 
    		final OutputStream stderr) throws Exception
    {
        final ProcessBuilder pb = new ProcessBuilder(this);
        pb.directory(workingFolder);
        
        /*
         * Look for an equivalent environment variable name, case insensitive.
         */
        for(final Map.Entry<String,String> dV : deltaEnv.entrySet())
        {
            String key = dV.getKey();
            
            for(final Map.Entry<String,String> v : pb.environment().entrySet())
            {
                if(v.getKey().equals(dV.getKey()))
                {
                    key = v.getKey();
                }
            }
            
            pb.environment().put(key, dV.getValue());
        }
        
        final Process process = pb.start();

        AbstractShutdownHandler hook = newShutdownHandler(process);
        try
        {
            Runtime.getRuntime().addShutdownHook(hook);
        }
        catch(final IllegalStateException e)
        {
            hook = null;
        }
        try
        {
            try
            {
                final OutputStream out = stdout==null?System.out:stdout;
                final AbstractFacilitator outWorker = new StdOutFacilitator("STDOUT-Worker", process.getInputStream(), out);
                outWorker.start();
                
                final OutputStream err = stderr==null?System.err:stderr;
                
                final AbstractFacilitator errWorker = new StdOutFacilitator("STDERR-Worker", process.getErrorStream(), err);
                errWorker.start();
                
                final AbstractFacilitator inWorker = stdin==null?null:new StdInFacilitator("STDIN-Worker", stdin, process.getOutputStream());
                if(inWorker != null)
                {
                    inWorker.start();
                }
                
                outWorker.join();
                
                if(errWorker != null)
                {
                    errWorker.join();
                }
                
                process.waitFor();
                
                if(inWorker != null) 
                {
                    inWorker.interrupt();
                    inWorker.join();
                }
                
                if(outWorker.error != null)
                {
                    throw new RuntimeException(outWorker.error);
                }
                
                if(errWorker != null && errWorker.error != null)
                {
                    throw new RuntimeException(errWorker.error);
                }
                
                if(inWorker != null && inWorker.error != null)
                {
                    throw new RuntimeException(inWorker.error);
                }
                
                return process.exitValue();
            }
            finally
            {
                process.destroy();
            }
        }
        finally
        {
            try
            {
                if(hook != null)
                {
                    Runtime.getRuntime().removeShutdownHook(hook);
                }
            }
            catch(IllegalStateException e)
            {
                //Ignore.
            }
        }
    }
    /**
     * Provides the shutdown handler. The default implementation is {@link Subprocess.DefaultShutdownHandlerImpl}.
     * Override this method to modify the shutdown.
     * @param process The associated process.
     * @return The shutdown handler.
     */
    protected AbstractShutdownHandler newShutdownHandler(final Process process)
    {
    	return new DefaultShutdownHandlerImpl(process);
    }
    /**
     * Abstract shutdown hook.
     * @author Jason Smith
     */
    protected static abstract class AbstractShutdownHandler extends Thread
    {
        /**
         * The process that is being shutdown.
         */
        protected final Process process;
        
        /**
         * Constructor.
         * @param process Process that is being shut down.
         */
        public AbstractShutdownHandler(final Process process)
        {
            this.process = process;
        }
        
        /**
         * execute method.
         */
        public abstract void execute();
    }
    
    /**
     * Default shutdown handler. This implementation simply calls {@link Process#destroy()}
     * on the process.
     */
    protected static final class DefaultShutdownHandlerImpl extends AbstractShutdownHandler
    {
        public DefaultShutdownHandlerImpl(final Process process)
        {
            super(process);
        }
        
        public void execute()
        {
            process.destroy();
        }
    }
}
