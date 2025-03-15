/*
 * Copyright (c) 2016 Seth J. Morabito <web@loomcom.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.loomcom.symon.devices;

import com.loomcom.symon.exceptions.MemoryAccessException;
import com.loomcom.symon.exceptions.MemoryRangeException;
import java.io.File;
import java.io.IOException;

public class MemoryBank extends Device {

    private Memory[] bank;

    int count;

    public MemoryBank(int startAddress, int endAddress, int count) throws
            MemoryRangeException {
        this(startAddress, endAddress, count, Memory.makeRAM(startAddress,
                endAddress));
    }

    public MemoryBank(int startAddress, int endAddress, int count,
            Memory initial)
            throws MemoryRangeException {
        super(startAddress, endAddress, "Memory Bank");
        this.count = count;
        bank = new Memory[count];
        bank[0] = initial;
    }

    @Override
    public int read(int address, boolean cpuAccess) throws MemoryAccessException {
        return read(address, 0, cpuAccess);
    }

    @Override
    public void write(int address, int data) throws MemoryAccessException {
        write(address, data, 0);
    }

    public static MemoryBank makeBank(int startAddress, int endAddress,
            int count, File f)
            throws MemoryRangeException, IOException {
        Memory memory = Memory.makeROM(startAddress, endAddress, f);
        return new MemoryBank(startAddress, endAddress, count, memory);
    }

    public static MemoryBank makeBank(int startAddress, int endAddress,
            int count)
            throws MemoryRangeException {
        return new MemoryBank(startAddress, endAddress, count);
    }

    public void write(int address, int data, int page) throws
            MemoryAccessException {
        if (bank[page] == null) {
            try {
                bank[page] = Memory.makeRAM(startAddress(), endAddress());
            } catch (MemoryRangeException ex) {
                throw new MemoryAccessException("Bank failed to initialze page");
            }
        }
        bank[page].write(address, data);
    }

    public int read(int address, int page, boolean cpuAccess) throws
            MemoryAccessException {
        if (bank[page] == null) {
            try {
                bank[page] = Memory.makeRAM(startAddress(), endAddress());
            } catch (MemoryRangeException ex) {
                throw new MemoryAccessException("Bank failed to initialze page");
            }
        }
        return bank[page].read(address, cpuAccess);
    }

    public void setPage(int page, Memory mem) {
        bank[page] = mem;
    }

    public Memory getPage(int page) {
        return bank[page];
    }

    public String toString() {
        return "Memory: " + getMemoryRange().toString();
    }

}
