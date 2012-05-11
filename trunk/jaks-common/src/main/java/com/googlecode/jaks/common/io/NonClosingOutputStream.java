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

/**
 * Aggregates an {@link OutputStream}, overriding the {@link #close()} method to prevent
 * the consumer from explicitly closing the wrapped stream (it calls {@link #flush()} instead).
 * @author Jason Smith
 */
public class NonClosingOutputStream extends OutputStream
{
	private final OutputStream out;
	
	/**
	 * Constructor.
	 * @param out The {@link OutputStream} to wrap.
	 */
	public NonClosingOutputStream(final OutputStream out)
	{
		this.out = out;
	}
	
	@Override
	public void write(final int b) throws IOException 
	{
		out.write(b);
	}

	@Override
	public void close() throws IOException 
	{
		out.flush();
	}

	@Override
	public void flush() throws IOException 
	{
		out.flush();
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException 
	{
		out.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException 
	{
		out.write(b);
	}
}
