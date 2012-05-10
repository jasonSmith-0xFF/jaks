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
package com.googlecode.jaks.archive;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

import com.googlecode.jaks.archive.resource.AbstractNode;
import com.googlecode.jaks.archive.resource.UnixModeGuesser;

public abstract class AbstractArchiver implements Closeable
{
	private final UnixModeGuesser guesser;

	public AbstractArchiver(final UnixModeGuesser guesser)
	{
		this.guesser = guesser;
	}
	
	public UnixModeGuesser getUnixModeGuesser()
	{
		return guesser;
	}
	
	public void addAll(Collection<AbstractNode> nodes) throws Exception
	{
		for(AbstractNode node : nodes)
		{
			add(node);
		}
	}
	
	public abstract void add(AbstractNode node) throws Exception;
	
	@Override
	public abstract void close() throws IOException;
}
