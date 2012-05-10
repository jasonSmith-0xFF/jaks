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
 * Thread-worker that pumps data from an input stream to an output
 * stream; expected to be interrupted when the process ends.
 * @author Jason Smith
 */
class StdInFacilitator extends AbstractFacilitator
{
    /**
     * Constructor. 
     * @param baseThreadName The base name of the thread.
     * @param in Source input stream.
     * @param out Destination output stream; the "stdin" of the process.
     */
    public StdInFacilitator(String baseThreadName, InputStream in, OutputStream out)
    {
        super(baseThreadName, in, out);
    }
    
    /*
     * @see java.lang.Thread#run()
     */
    public void run()
    {
        try
        {
            byte[] buffer = new byte[4096];
        
            while(true)
            {
                if(Thread.currentThread().isInterrupted())
                    return;
                
                while(in.available() > 0)
                {
                    int length = in.read(buffer, 0, Math.min(in.available(), buffer.length));
                    if(length == -1)
                        return;
                    else
                        out.write(buffer, 0, length);
                }
                Thread.sleep(10);
            }
        }
        catch(InterruptedException e)
        {
            return;
        }
        catch(Throwable t)
        {
            error = t;
        }
    }
}
