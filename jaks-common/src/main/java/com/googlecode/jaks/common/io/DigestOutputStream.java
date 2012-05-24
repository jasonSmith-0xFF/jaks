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

/**
 * An output stream whose result is the message digest of the data written to it.
 * @see MessageDigest
 * @see StreamUtil#transfer(java.io.InputStream, OutputStream)
 * @see MultiOutputStream
 * @author Jason Smith
 */
public class DigestOutputStream extends OutputStream
{
	private final MessageDigest digest;

	private byte[] result = null;
	
	/**
	 * Constructor.
	 * @param algorithm The algorithm, such as <tt>MD5</tt> (128 bits), <tt>SHA-1</tt> (160 bits), or <tt>SHA-256</tt> (256 bits).
	 *             Refer to the documentation for {@link MessageDigest} for more information.
	 * @throws NoSuchAlgorithmException See {@link NoSuchAlgorithmException}.
	 */
	public DigestOutputStream(final String algorithm) throws NoSuchAlgorithmException
	{
		digest = MessageDigest.getInstance(algorithm);
	}
	
	/**
	 * Return the digest, closing the stream if necessary.
	 * @return The digest.
	 */
	public byte[] getDigest()
	{
		try
		{
			close();
		}
		catch(final IOException e)
		{
			return SquashedException.raise(e);
		}
		return result;
	}
	
	/**
	 * Return the digest as a hexadecimal-encoded string, closing the stream if necessary.
	 * @return The digest as a hexadecimal-encoded string.
	 */
	public String getDigestString()
	{
		StringBuilder s = new StringBuilder();
		for(final byte b : getDigest())
		{
			int i = (int)b & 0xff;
			if(i <= 0xf)
			{
				s.append("0");
			}
			s.append(Integer.toHexString(i));
		}
		return s.toString();
	}
	
	@Override
	public void write(int b) throws IOException 
	{
		if(result != null)
		{
			throw new IllegalStateException("Stream closed.");
		}
		digest.update((byte)(b & 0xFF));
	}

	@Override
	public void close() throws IOException 
	{
		if(result == null)
		{
			super.close();
			result = digest.digest();
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException 
	{
		if(result != null)
		{
			throw new IllegalStateException("Stream closed.");
		}
		digest.update(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException 
	{
		if(result != null)
		{
			throw new IllegalStateException("Stream closed.");
		}
		digest.update(b);
	}
}
