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
package com.googlecode.jaks.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends the same data to multiple target output streams.
 * @author Jason Smith
 */
public class MultiOutputStream extends OutputStream
{
	private final List<OutputStream> outs = new ArrayList<OutputStream>();
	
	/**
	 * Constructor.
	 * @param outs Set of target output streams.
	 */
	public MultiOutputStream(final OutputStream... outs)
	{
		for(final OutputStream out : outs)
		{
			if(out != null)
			{
				this.outs.add(out);
			}
		}
	}
	
	@Override
	public void write(int b) throws IOException 
	{
		for(final OutputStream out : outs)
		{
			out.write(b);
		}
	}

	@Override
	public void close() throws IOException 
	{
		for(final OutputStream out : outs)
		{
			out.close();
		}

	}

	@Override
	public void flush() throws IOException 
	{
		for(final OutputStream out : outs)
		{
			out.flush();
		}

	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException 
	{
		for(final OutputStream out : outs)
		{
			out.write(b, off, len);
		}
	}

	@Override
	public void write(byte[] b) throws IOException 
	{
		for(final OutputStream out : outs)
		{
			out.write(b);
		}
	}
}
