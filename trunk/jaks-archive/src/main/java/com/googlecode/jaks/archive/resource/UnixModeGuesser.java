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
package com.googlecode.jaks.archive.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import org.apache.tools.zip.UnixStat;

import com.googlecode.jsaf.common.io.StreamUtil;

/**
 * "Guess" an appropriate Unix mode (permissions) for a resource or folder.
 * 
 * <p>For archiving, we can't depend on the <em>actual</em> permissions set on a file since
 * we may be building on a non-Unix OS. This class makes a best guess at the permissions
 * that should be on a folder or resource, which is usually good enough for an archive.</p>
 * 
 * <p>The base implementation uses {@link UnixStat#DEFAULT_DIR_PERM} for folders and 
 * {@link UnixStat#DEFAULT_FILE_PERM} for files. If the file looks like a shell script,
 * it is marked as executable. You can override the default implementation to handle
 * more complex scenarios.</p>
 * 
 * @author Jason Smith
 */
public class UnixModeGuesser 
{
	/** Owner-read (0400). */
	public static final int OWNER_READ = 0400;
	/** Owner-write (0200). */
	public static final int OWNER_WRITE = 0200;
	/** Owner-execute (0100). */
	public static final int OWNER_EXECUTE = 0100;

	/** Group-read (0400). */
	public static final int GROUP_READ = 040;
	/** Group-write (0200). */
	public static final int GROUP_WRITE = 020;
	/** Group-execute (0100). */
	public static final int GROUP_EXECUTE = 010;
	
	/** Other-read (0400). */
	public static final int OTHER_READ = 04;
	/** Other-write (0200). */
	public static final int OTHER_WRITE = 02;
	/** Other-execute (0100). */
	public static final int OTHER_EXECUTE = 01;
	
	/** Default folder permissions. */
	public static final int DEFAULT_DIR_PERM = UnixStat.DEFAULT_DIR_PERM;
	/** Default file permissions. */
	public static final int DEFAULT_FILE_PERM = UnixStat.DEFAULT_FILE_PERM;
	/** Default permissions for an executable file. */
	public static final int EXECUTABLE_FILE_PERM = DEFAULT_FILE_PERM | OWNER_EXECUTE | GROUP_EXECUTE | OTHER_EXECUTE;


	/**
	 * Guess the Unix mode.
	 * @param node The node.
	 * @return The Unix mode (permissions) to set in the archive.
	 * @throws Exception See {@link Exception}.
	 */
	public int guessUnixMode(final AbstractNode node) throws Exception
	{
		if(node instanceof AbstractFolder)
		{
			return guessUnixModeForFolder((AbstractFolder)node);
		}
		else
		{
			return guessUnixModeForResource((AbstractResource)node);
		}
	}
	
	/**
	 * Unix mode for a folder. The default implementation hard-codes this to {@link #DEFAULT_DIR_PERM}.
	 * @param folder The folder.
	 * @return The Unix mode for the folder.
	 * @throws Exception See {@link Exception}.
	 */
	protected int guessUnixModeForFolder(final AbstractFolder folder) throws Exception
	{
		return DEFAULT_DIR_PERM;
	}
	
	/**
	 * Unix mode for a resource. 
	 * @param resource The resource.
	 * @return The Unix mode for the resource.
	 * @throws Exception See {@link Exception}.
	 */
	protected int guessUnixModeForResource(final AbstractResource resource) throws Exception
	{
		if(resource.getPath().endsWith(".sh") || !resource.getName().contains("."))
		{
			final String line = getFirstLine(resource);
			if(line.startsWith("#!/bin/") || line.startsWith("#!/usr/"))
			{
				return EXECUTABLE_FILE_PERM;
			}
		}
		return DEFAULT_FILE_PERM;
	}
	
	/**
	 * Get the first line from the resource, treating it as US-ASCII text (so no encoding errors, ever). 
	 * This is intended to be used to snoop for shell scripts.
	 * @param resource The resource.
	 * @return The first line of the resource.
	 */
	protected String getFirstLine(final AbstractResource resource) throws IOException
	{
		try	(final LineNumberReader reader = new LineNumberReader(new InputStreamReader(resource.getInputStream(), StreamUtil.US_ASCII)))
		{
			return reader.readLine();
		}
	}
}
