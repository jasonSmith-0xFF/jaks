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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.googlecode.jaks.common.SquashedException;

public class Sha1OutputStream extends OutputStream
{
	private final MessageDigest digest;

	private byte[] sha1Hash = null;
	
	public Sha1OutputStream() throws NoSuchAlgorithmException
	{
		digest = MessageDigest.getInstance("SHA-1");
	}
	
	public byte[] getSha1Hash()
	{
		try
		{
			close();
		}
		catch(final IOException e)
		{
			return SquashedException.raise(e);
		}
		return sha1Hash;
	}
	
	@Override
	public void write(int b) throws IOException 
	{
		if(sha1Hash != null)
		{
			throw new IllegalStateException("Stream closed.");
		}
		digest.update((byte)(b & 0xFF));
	}

	@Override
	public void close() throws IOException 
	{
		if(sha1Hash == null)
		{
			super.close();
			sha1Hash = digest.digest();
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException 
	{
		if(sha1Hash != null)
		{
			throw new IllegalStateException("Stream closed.");
		}
		digest.update(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException 
	{
		if(sha1Hash != null)
		{
			throw new IllegalStateException("Stream closed.");
		}
		digest.update(b);
	}
}
