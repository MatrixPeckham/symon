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

public class MemoryWindow extends Device {

    private static final int BANK_COUNT = 256;

    private int port;

    MemoryBank bank;

    private ExternalPorts controller;

    public MemoryWindow(int startAddress, int endAddress, ExternalPorts ports,
            int port) throws MemoryRangeException {
        this(startAddress, endAddress, ports, port, MemoryBank.makeBank(
                startAddress,
                endAddress, BANK_COUNT));
    }

    public MemoryWindow(int startAddress, int endAddress, ExternalPorts ports,
            int port, MemoryBank initial)
            throws MemoryRangeException {
        super(startAddress, endAddress, "memory window");
        this.port = port;
        this.controller = ports;
        bank = initial;
    }

    @Override
    public void write(int address, int data) throws MemoryAccessException {
        bank.write(address, data, controller.read(port));
    }

    @Override
    public int read(int address, boolean cpuAccess) throws MemoryAccessException {
        return bank.read(address, controller.read(port), cpuAccess);
    }

    @Override
    public String toString() {
        return "Memory Bank: " + getMemoryRange().toString();
    }

}
