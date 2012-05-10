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
package com.googlecode.jsaf.archive.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileResource extends AbstractResource
{
	private final File file;
	
	public FileResource(final String path, final File file) throws FileNotFoundException
	{
		super(path, 0L);
		if(!file.isFile())
		{
			throw new FileNotFoundException(file.getPath());
		}
		this.file = file;
	}

	@Override
	public long getLastModified() 
	{
		return file.lastModified();
	}

	@Override
	public InputStream getInputStream() throws IOException 
	{
		return new FileInputStream(file);
	}

	@Override
	public long size() 
	{
		return file.length();
	}
}
