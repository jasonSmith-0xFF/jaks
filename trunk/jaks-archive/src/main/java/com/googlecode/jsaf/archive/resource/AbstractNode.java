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

import org.apache.commons.lang.StringUtils;

/**
 * A pathed archive entry; either a generic resource or generic folder.
 * @author Jason Smith
 */
public abstract class AbstractNode  implements Comparable<AbstractNode>
{
	private final String path;
	
	private final long lastModified;
	
	public AbstractNode(final String path, final long lastModified)
	{
		if(StringUtils.isEmpty(path))
		{
			throw new IllegalArgumentException("Path can't be empty.");
		}
		
		if(path.startsWith("/"))
		{
			throw new IllegalArgumentException("Path can't start with '/': " + path);
		}
		
		if(path.endsWith("/"))
		{
			throw new IllegalArgumentException("Path can't end with '/': " + path);
		}
		
		if(path.contains("//"))
		{
			throw new IllegalArgumentException("Path can't contain '//': " + path);
		}
		
		this.path = path;
		this.lastModified = lastModified;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getName()
	{
		String[] pathElts = getPath().split("/");
		return pathElts[pathElts.length-1];
	}
	
	public long getLastModified()
	{
		return lastModified;
	}
	
	@Override
	public boolean equals(final Object obj) 
	{
		return obj instanceof AbstractResource && getPath().equals(((AbstractResource)obj).getPath());
	}

	@Override
	public int hashCode() 
	{
		return getPath().hashCode();
	}

	@Override
	public String toString() 
	{
		return super.toString() + ":" + getPath();
	}

	@Override
	public int compareTo(final AbstractNode that) 
	{
		return this.getPath().compareTo(that.getPath());
	}
}
