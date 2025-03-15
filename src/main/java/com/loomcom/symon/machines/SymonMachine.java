/*
 * Copyright (c) 2016 Seth J. Morabito <web@loomcom.com>
 *                    Maik Merten <maikmerten@googlemail.com>
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
package com.loomcom.symon.machines;

import com.loomcom.symon.Bus;
import com.loomcom.symon.Cpu;
import com.loomcom.symon.devices.*;
import com.loomcom.symon.exceptions.MemoryRangeException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymonMachine implements Machine {

    private final static Logger logger = LoggerFactory.getLogger(
            SymonMachine.class.getName());

    // Constants used by the simulated system. These define the memory map.
    private static final int BUS_BOTTOM = 0x0000;

    private static final int BUS_TOP = 0xffff;

    //Zero Page, banked on external port 0 @ 0x0200
    private static final int ZP_BASE = 0x0000;

    private static final int ZP_SIZE = 0x0100;

    //Stack Page, banked on external port 1 @ 0x0201
    private static final int STACK_BASE = 0x0100;

    private static final int STACK_SIZE = 0x0100;

    //External Ports 0x0200-0x020F ZP page, Stack Page, RAM Page, RAM/ROM Page low 0x8000-0xBFFF, RAM/ROM Page high 0xC000=0xFFFF
    private static final int EXTERNAL_BASE = 0x0200;

    private static final int ZP_PORT = 0;

    private static final int STACK_PORT = 1;

    private static final int RAM_PORT_1 = 2;

    private static final int RAM_ROM_LOW = 3;

    private static final int RAM_ROM_HI = 4;

    private static final int ZP2_PORT = 5;

    private static final int STACK2_PORT = 6;

    // PIA at $0300-$030F
    private static final int PIA_BASE = 0x0300;

    //"SD" controller at $0400 - $0408
    private static final int SD_BASE = 0x0400;

    // ACIA at $0500-$0503
    private static final int ACIA_BASE = 0x0500;

    // CRTC at $0600-$0601
    private static final int CRTC_BASE = 0x0600;

    //Mirror Zero Page, banked on external port 0 @ 0x0205
    private static final int ZP2_BASE = 0x0800;

    private static final int ZP2_SIZE = 0x0100;

    //Stack Page, banked on external port 1 @ 0x0206
    private static final int STACK2_BASE = 0x0900;

    private static final int STACK2_SIZE = 0x0100;

    // 12K of RAM from $1000 - $7FFF
    private static final int MEMORY_BASE = 0x1000;

    private static final int MEMORY_SIZE = 0x3000;

    // 16K banked ram at 0x4000-0x7FFF paged on external port 2 @ 0x0202
    private static final int RAM_BASE = 0x4000;

    private static final int RAM_SIZE = 0x4000;

    // 16KB Banked RAM/ROM at 0x8000-0xBFFF paged on external port 3 @ 0x0203
    //Banks Shared with High 16k of memory space
    private static final int RAM_ROM_BASE = 0x8000;

    private static final int RAM_ROM_SIZE = 0x4000;

    // 16KB banked RAM/ROM at 0xC000-0xFFFF paged on external port 4 @ 0x0204
    //Banks Shared with previous 16k of memory space
    private static final int ROM_BASE = 0xC000;

    private static final int ROM_SIZE = 0x4000;

    // The simulated peripherals
    private final MemoryBank zpBank;

    private final MemoryBank stackBank;

    private final MemoryWindow zpWindow;

    private final MemoryWindow stackWindow;

    private final MemoryWindow zpWindow2;

    private final MemoryWindow stackWindow2;

    private final MemoryWindow ram2;

    private final MemoryWindow ramRomLow;

    private final MemoryWindow ramRomHi;

    private final ExternalPorts externalPorts;

    private final Bus bus;

    private final Cpu cpu;

    private final Acia acia;

    private final Pia pia;

    private final Crtc crtc;

    private final SdController sdCon;

    private final Memory ram;

    private final MemoryBank ramrom;

    public SymonMachine(String romFile) throws Exception {
        this.bus = new Bus(BUS_BOTTOM, BUS_TOP);
        zpBank = MemoryBank.makeBank(ZP_BASE, ZP_BASE + ZP_SIZE - 1, 256);
        stackBank = MemoryBank.makeBank(STACK_BASE, STACK_BASE + STACK_SIZE - 1,
                256);
        this.externalPorts = new ExternalPorts(EXTERNAL_BASE);
        this.zpWindow = new MemoryWindow(ZP_BASE, ZP_BASE + ZP_SIZE - 1,
                externalPorts,
                ZP_PORT, zpBank);
        this.stackWindow = new MemoryWindow(STACK_BASE, STACK_BASE + STACK_SIZE
                - 1,
                externalPorts,
                STACK_PORT, stackBank);
        this.zpWindow2 = new MemoryWindow(ZP2_BASE, ZP2_BASE + ZP2_SIZE - 1,
                externalPorts,
                ZP2_PORT, zpBank);
        this.stackWindow2 = new MemoryWindow(STACK2_BASE, STACK2_BASE
                + STACK2_SIZE
                - 1,
                externalPorts,
                STACK2_PORT, stackBank);
        this.ram2 = new MemoryWindow(RAM_BASE, RAM_BASE + RAM_SIZE
                - 1,
                externalPorts,
                RAM_PORT_1);
        this.cpu = new Cpu();
        this.ram = new Memory(MEMORY_BASE, MEMORY_BASE + MEMORY_SIZE - 1, false);
        this.pia = new Via6522(PIA_BASE);
        this.acia = new Acia6551(ACIA_BASE);
        this.sdCon = new SdController(SD_BASE);
        this.crtc = new Crtc(CRTC_BASE, ram);

        bus.addCpu(cpu);
        bus.addDevice(externalPorts);
        bus.addDevice(zpWindow);
        bus.addDevice(stackWindow);
        bus.addDevice(zpWindow2);
        bus.addDevice(stackWindow2);
        bus.addDevice(ram);
        bus.addDevice(ram2);
        bus.addDevice(pia);
        bus.addDevice(acia);
        bus.addDevice(sdCon);
        bus.addDevice(crtc);

        if (romFile != null) {
            File romImage = new File(romFile);
            if (romImage.canRead()) {
                logger.info("Loading ROM image from file {}", romImage);
                this.ramrom = MemoryBank.makeBank(ROM_BASE, ROM_BASE + ROM_SIZE
                        - 1, 256,
                        romImage);
            } else {
                logger.info(
                        "Default ROM file {} not found, loading empty R/W memory image.",
                        romImage);
                this.ramrom = MemoryBank.makeBank(ROM_BASE, ROM_BASE + ROM_SIZE
                        - 1, 256);
            }
        } else {
            logger.
                    info("No ROM file specified, loading empty R/W memory image.");
            this.ramrom = MemoryBank.makeBank(ROM_BASE, ROM_BASE + ROM_SIZE - 1,
                    256);
        }
        this.ramRomLow = new MemoryWindow(RAM_ROM_BASE, RAM_ROM_BASE
                + RAM_ROM_SIZE - 1, externalPorts,
                RAM_ROM_LOW, this.ramrom);
        this.ramRomHi = new MemoryWindow(ROM_BASE, ROM_BASE + ROM_SIZE - 1,
                externalPorts,
                RAM_ROM_HI, this.ramrom);
        bus.addDevice(ramRomLow);
        bus.addDevice(ramRomHi);
    }

    @Override
    public Bus getBus() {
        return bus;
    }

    @Override
    public Cpu getCpu() {
        return cpu;
    }

    @Override
    public Memory getRam() {
        return ram;
    }

    @Override
    public Acia getAcia() {
        return acia;
    }

    @Override
    public Pia getPia() {
        return pia;
    }

    @Override
    public Crtc getCrtc() {
        return crtc;
    }

    @Override
    public Memory getRom() {
        return ramrom.getPage(0);
    }

    public void setRom(Memory rom) throws MemoryRangeException {
        ramrom.setPage(0, rom);
    }

    @Override
    public int getRomBase() {
        return ROM_BASE;
    }

    @Override
    public int getRomSize() {
        return ROM_SIZE;
    }

    @Override
    public int getMemorySize() {
        return MEMORY_SIZE;
    }

    @Override
    public String getName() {
        return "Symon";
    }

}
