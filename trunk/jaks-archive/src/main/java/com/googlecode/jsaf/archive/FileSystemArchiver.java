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
package com.googlecode.jsaf.archive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.googlecode.jsaf.archive.resource.AbstractFolder;
import com.googlecode.jsaf.archive.resource.AbstractNode;
import com.googlecode.jsaf.archive.resource.AbstractResource;
import com.googlecode.jsaf.archive.resource.UnixModeGuesser;
import com.googlecode.jsaf.common.io.StreamUtil;

public class FileSystemArchiver extends AbstractArchiver
{
	private final File folder;
	
	public FileSystemArchiver(final File folder) throws FileNotFoundException
	{
		this(folder, new UnixModeGuesser());
	}
	
	public FileSystemArchiver(final File folder, UnixModeGuesser guesser) throws FileNotFoundException 
	{
		super(guesser);
		if(!folder.isDirectory())
		{
			throw new FileNotFoundException(folder.getPath());
		}
		this.folder = folder;
	}

	@Override
	public void add(final AbstractNode node) throws Exception 
	{
		if(node instanceof AbstractFolder)
		{
			final File childFolder = new File(folder, node.getPath()).getCanonicalFile();
			childFolder.mkdirs();
			childFolder.setLastModified(node.getLastModified());
		}
		else if(node instanceof AbstractResource)
		{
			final File file = new File(folder, node.getPath()).getCanonicalFile();
			file.getParentFile().mkdirs();
			StreamUtil.transfer(((AbstractResource)node).getInputStream(), new FileOutputStream(file));
			final int unixMode = getUnixModeGuesser().guessUnixMode(node);
			
			file.setReadable((unixMode & UnixModeGuesser.OTHER_READ) > 0);
			file.setWritable((unixMode & UnixModeGuesser.OWNER_WRITE) > 0);
			file.setExecutable((unixMode & UnixModeGuesser.OWNER_EXECUTE) > 0);
		}
		else
		{
			throw new IllegalArgumentException("I don't know how to add a " + node + ".");
		}
	}

	@Override
	public void close() throws IOException 
	{
	}
}
