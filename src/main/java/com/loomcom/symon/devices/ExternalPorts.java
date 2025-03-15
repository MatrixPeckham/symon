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

/**
 * Very basic implementation of a MOS 6522 VIA.
 * <p>
 * TODO: Implement timers as threads.
 */
public class ExternalPorts extends Pia {

    public static final int VIA_SIZE = 16;

    enum Register {
        P0, P1, P2, P3, P4, P5, P6, P7,
        P8, P9, PA, PB, PC, PD, PE, PF

    }

    int[] registers = new int[VIA_SIZE];

    public ExternalPorts(int address) throws MemoryRangeException {
        super(address, address + VIA_SIZE - 1, "External Ports");
        for (int i = 0; i < VIA_SIZE; i++) {
            registers[i] = 0;
        }
    }

    @Override
    public void write(int address, int data) throws MemoryAccessException {
        if (address >= registers.length) {
            throw new MemoryAccessException("Unknown register: " + address);
        }
        registers[address] = data;

    }

    @Override
    public int read(int address, boolean cpuAccess) throws MemoryAccessException {

        if (address >= registers.length) {
            throw new MemoryAccessException("Unknown register: " + address);
        }

        return registers[address];
    }

    public int read(int address) {
        try {
            return read(address, false);
        } catch (MemoryAccessException e) {
            return 0;
        }
    }

}
