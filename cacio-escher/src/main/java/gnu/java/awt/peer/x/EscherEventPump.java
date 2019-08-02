/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package gnu.java.awt.peer.x;

import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import gnu.x11.Display;
import gnu.x11.event.ButtonPress;
import gnu.x11.event.ButtonRelease;
import gnu.x11.event.Event;
import gnu.x11.event.Expose;
import gnu.x11.event.MotionNotify;
import sun.awt.SunToolkit;
import sun.awt.peer.cacio.CacioComponent;
import sun.awt.peer.cacio.CacioEventPump;

class EscherEventPump extends CacioEventPump<Event> {

    private HashMap<Long, EscherPlatformWindow> windowMap;

    EscherEventPump() {
        windowMap = new HashMap<>();
    }

    @Override
    protected Event fetchNativeEvent() {
        SunToolkit.awtLock();
        Event eventData = null;
        try {
            eventData = nativeFetchEvent(XGraphicsEnvironment.class.cast(GraphicsEnvironment.getLocalGraphicsEnvironment()).getDisplay());
        } finally {
            SunToolkit.awtUnlock();
        }
        if (eventData == null) {
            try { Thread.sleep(10); } catch (InterruptedException ex) { }
        }
        return eventData;
    }

    @Override
    protected void dispatchNativeEvent(Event nativeEvent) {
        if (nativeEvent == null) {
            return;
        }
        switch (nativeEvent.code()) {
            case MAP_NOTIFY:
                break;
            case EXPOSE: {
                Expose event = Expose.class.cast(nativeEvent);
                EscherPlatformWindow w = windowMap.get(Long.valueOf(event.getWindowID()));
                CacioComponent source = w.getCacioComponent();
                Component c = source.getAWTComponent();
System.err.println("EscherEventPump::dispatchNativeEvent: exposing: " + c);
                postPaintEvent(source, 0, 0, c.getWidth(), c.getHeight());
                break;
            }
            case MOTION_NOTIFY: {
                MotionNotify event = MotionNotify.class.cast(nativeEvent);
                EscherPlatformWindow w = windowMap.get(Long.valueOf(event.getEventWindowID()));
                CacioComponent source = w.getCacioComponent();
                postMouseEvent(source, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, event.getEventX(), event.getEventY(), 0, false);
                break;
            }
            case BUTTON_PRESS: {
                ButtonPress event = ButtonPress.class.cast(nativeEvent);
                EscherPlatformWindow w = windowMap.get(Long.valueOf(event.getEventWindowID()));
                CacioComponent source = w.getCacioComponent();
                /* TODO: Fix mods. */
System.err.println("EscherEventPump::dispatchNativeEvent: BUTTON_PRESS");
                postMouseEvent(source, MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), MouseEvent.BUTTON1_DOWN_MASK, event.getEventX(), event.getEventY(), 0, false);
                break;
            }
            case BUTTON_RELEASE: {
                ButtonRelease event = ButtonRelease.class.cast(nativeEvent);
                EscherPlatformWindow w = windowMap.get(Long.valueOf(event.getEventWindowID()));
                CacioComponent source = w.getCacioComponent();
                /* TODO: Fix mods. */
System.err.println("EscherEventPump::dispatchNativeEvent: BUTTON_RELEASE");
                postMouseEvent(source, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, event.getEventX(), event.getEventY(), 0, false);
                break;
            }
            default:
System.err.println("EscherEventPump::dispatchNativeEvent: unhandled event type: " + nativeEvent.code());
        }
    }

    void registerWindow(long windowId, EscherPlatformWindow w) {
        windowMap.put(Long.valueOf(windowId), w);
    }

    void deregisterWindow(long windowId) {
        windowMap.remove(Long.valueOf(windowId));
    }

    private Event nativeFetchEvent(Display display) {
        Event event;

        if (!display.eventAvailable()) {
            return null;
        }

        event = display.nextEvent();
        switch (event.code()) {
            case MAP_NOTIFY:
            case EXPOSE:
            case MOTION_NOTIFY:
            case BUTTON_PRESS:
            case BUTTON_RELEASE:
                return event;
            default:
System.err.printf("Unhandled X event type: %s\n", event.code());
                return null;
        }
    }
}
