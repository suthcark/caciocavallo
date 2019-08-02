/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package net.java.openjdk.cacio.directfb;

import java.awt.GraphicsDevice;

import sun.awt.SunToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;

public class DirectFBGraphicsEnvironment extends SunGraphicsEnvironment {

    static {
        try {
            Class.forName("sun.awt.X11FontManager");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        NarSystem.loadLibrary();
        // System.loadLibrary("awt_xawt");
        // System.loadLibrary("awt");
        SurfaceManagerFactory.setInstance(new DirectFBSurfaceManagerFactory());
    }

    private long nativePtr;

    private DirectFBGraphicsDevice defaultDevice;

    long getDirectFB() {
        return nativePtr;
    }

    private native long createDirectFB();

    public DirectFBGraphicsEnvironment() {
        try {
            SunToolkit.awtLock();
            nativePtr = createDirectFB();
        } finally {
            SunToolkit.awtUnlock();
        }
        defaultDevice = new DirectFBGraphicsDevice(this);
    }

    @Override
    protected int getNumScreens() {
        return 1;
    }

    @Override
    public boolean isDisplayLocal() {
        return true;
    }

    @Override
    protected GraphicsDevice makeScreenDevice(int num) {
        return defaultDevice;
    }

}
