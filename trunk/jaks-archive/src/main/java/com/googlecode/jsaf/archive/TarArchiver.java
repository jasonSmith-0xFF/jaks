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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;

import com.googlecode.jsaf.archive.resource.AbstractFolder;
import com.googlecode.jsaf.archive.resource.AbstractNode;
import com.googlecode.jsaf.archive.resource.AbstractResource;
import com.googlecode.jsaf.archive.resource.UnixModeGuesser;
import com.googlecode.jsaf.common.io.NonClosingOutputStream;
import com.googlecode.jsaf.common.io.StreamUtil;

public class TarArchiver extends AbstractArchiver
{
	private final TarOutputStream out;
	
	
	public TarArchiver(final OutputStream out)
	{
		this(out, new UnixModeGuesser());
	}
	
	public TarArchiver(final OutputStream out, final UnixModeGuesser guesser)
	{
		super(guesser);
		
		this.out = new TarOutputStream(out);
		this.out.setLongFileMode(TarOutputStream.LONGFILE_GNU);
	}
	
	@Override
	public void add(AbstractNode node) throws Exception
	{
		if(node instanceof AbstractFolder)
		{
			final TarEntry entry = new TarEntry(node.getPath() + "/");
			try
			{
				entry.setSize(0);
				entry.setModTime(node.getLastModified());
				entry.setMode(getUnixModeGuesser().guessUnixMode(node));
				out.putNextEntry(entry);
			}
			finally
			{
				out.closeEntry();
			}

		}
		else if(node instanceof AbstractResource)
		{
			final TarEntry entry = new TarEntry(node.getPath());
			try
			{
				entry.setSize(((AbstractResource)node).size());
				entry.setModTime(node.getLastModified());
				entry.setMode(getUnixModeGuesser().guessUnixMode(node));
				out.putNextEntry(entry);
				StreamUtil.transfer(((AbstractResource)node).getInputStream(), new NonClosingOutputStream(out));
			}
			finally
			{
				out.closeEntry();
			}
		}
		else
		{
			throw new IllegalArgumentException("I don't know how to add a " + node + ".");
		}
	}
	
	@Override
	public void close() throws IOException 
	{
		out.close();
	}
}
