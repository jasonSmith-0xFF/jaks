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
package com.googlecode.jsaf.system;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base thread worker.
 * @author Jason Smith
 */
class AbstractFacilitator extends Thread
{
    /** Number used to identify the worker thread. */
    private static long workerCount = 0;

    /** The input stream. */
    protected final InputStream in;
    
    /** The output stream. */
    protected final OutputStream out;
    
    /** Placeholder for exceptions that occur during processing. */
    public Throwable error = null;
    
    /**
     * Constructor.
     * @param baseThreadName The base name of the thread.
     * @param in Data source.
     * @param out Data target.
     */
    protected AbstractFacilitator(String baseThreadName, InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = out;
        
        setName(baseThreadName + "-" + getNextCount());
    }
    
    /**
     * Returns any exception that occurred, or {@code null}.
     * @return Any exception that occurred.
     */
    public Throwable getError()
    {
        return error;
    }
    
    /**
     * Gets the next number.
     * @return The next number.
     */
    private long getNextCount()
    {
        synchronized(AbstractFacilitator.class)
        {
            return workerCount++;
        }
    }
}

