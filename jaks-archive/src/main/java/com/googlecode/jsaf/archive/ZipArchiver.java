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
import java.util.zip.Deflater;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import com.googlecode.jsaf.archive.resource.AbstractFolder;
import com.googlecode.jsaf.archive.resource.AbstractNode;
import com.googlecode.jsaf.archive.resource.AbstractResource;
import com.googlecode.jsaf.archive.resource.UnixModeGuesser;
import com.googlecode.jsaf.common.io.NonClosingOutputStream;
import com.googlecode.jsaf.common.io.StreamUtil;

public class ZipArchiver extends AbstractArchiver
{
	private final ZipOutputStream out;
	
	public ZipArchiver(final OutputStream out)
	{
		this(out, Deflater.BEST_SPEED, new UnixModeGuesser());
	}
	
	public ZipArchiver(final OutputStream out, final int compression)
	{
		this(out, compression, new UnixModeGuesser());
	}
	
	public ZipArchiver(final OutputStream out, final UnixModeGuesser guesser)
	{
		this(out,  Deflater.BEST_SPEED, guesser);
	}

	public ZipArchiver(final OutputStream out, final int compression, final UnixModeGuesser guesser)
	{
		super(guesser);
		this.out = new ZipOutputStream(out);
		this.out.setEncoding(StreamUtil.UTF8);
		this.out.setLevel(compression);
	}

	@Override
	public void add(AbstractNode node) throws Exception 
	{
		if(node instanceof AbstractFolder)
		{
			final ZipEntry entry = new ZipEntry(node.getPath() + "/");
			try
			{
				entry.setSize(0);
				entry.setTime(node.getLastModified());
				entry.setUnixMode(getUnixModeGuesser().guessUnixMode(node));
				out.putNextEntry(entry);
			}
			finally
			{
				out.closeEntry();
			}

		}
		else if(node instanceof AbstractResource)
		{
			final ZipEntry entry = new ZipEntry(node.getPath());
			try
			{
				entry.setSize(((AbstractResource)node).size());
				entry.setTime(node.getLastModified());
				entry.setUnixMode(getUnixModeGuesser().guessUnixMode(node));
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
