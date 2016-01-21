/*
 * The MIT License
 *
 * Copyright 2015 Mark A. Heckler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.thehecklers.ws;

import org.thehecklers.utility.MissionControl;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.thehecklers.utility.MissionControl.logIt;

/**
 * WsControlClient connects with Cloud application service via WebSocket. It provides
 * the means by which to send status & receive commands from the Cloud interface(s).
 *
 * @author Mark Heckler - 2016
 */
@ClientEndpoint
public class WsControlClient implements Observer {
    private Session session = null;
    private WebSocketContainer container;
    private boolean isConnected = false;
    static private String uriWeb;
    static private MissionControl remote;

    public WsControlClient() {
        // No-parameter constructor for testing
        //logIt("In no parameter constructor of Control WS client.");
    }
    
    public WsControlClient(String uriWeb, MissionControl remote) {
        WsControlClient.uriWeb = uriWeb.endsWith("/") ? uriWeb + "control" : uriWeb + "/" + "control";
        WsControlClient.remote = remote;
        try {
            connectToWebSocketServer();
        } catch (Exception e) {
            logIt("Error connecting to Control WebSocket server: " + e.getLocalizedMessage());
        }
    }

    private void connectToWebSocketServer() throws Exception {
        try {
            container = ContainerProvider.getWebSocketContainer();

            logIt("Connecting to " + uriWeb);
            session = container.connectToServer(WsControlClient.class, URI.create(uriWeb));
        } catch (Exception e) {
            //(IOException | DeploymentException | IllegalStateException e) {
            logIt("Error connecting, " + uriWeb + ": " + e.getLocalizedMessage());
            isConnected = false;
            return;
        }
        
        isConnected = true;
        logIt("Control WebSocket connected: " + uriWeb);
    }

    public void disconnect() {
        if (session != null) {
            if (session.isOpen()) {
                try {
                    session.close();
                } catch (IOException ex) {
                    Logger.getLogger(WsControlClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                logIt("Disconnecting: Control WebSocket session now closed");
            } else {
                logIt("Disconnecting: Control WebSocket session was already closed");
            }
        }
    }
    
    @OnOpen
    public void onOpen(Session session) {
        logIt("Control WebSocket connected to endpoint: " + session.getBasicRemote());
    }
 
    /*
    Messages: (more may be pending)
        F: Forward
        L: turn Left
        R: turn Right
        B: Backward
    */
    @OnMessage
    public void onMessage(String message) {
        logIt("Control message received: '" + message + "'");
        
        if (message == null || message.length() != 2) {
            logIt("CONTROL FAIL: Message length ==" + message.length());
            return;
        }
        String order = message.substring(1,2);
        try {
            //cmdBuffer += order;
            // MAH  1/19/2016                           remote.addToCommand(order);
        } catch (Exception ex) {
            Logger.getLogger(WsControlClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    @OnError
    public void onError(Throwable t) {
        logIt("Error in Control WebSocket: " + t.getLocalizedMessage());
    }

    @Override
    public void update(Observable o, Object arg) {
        // Periodic check to verify the Control WebSocket is still connected; if not, reconnect

        if (isConnected) {
            try {
                if (session != null
                        && session.isOpen()
                        && session.getBasicRemote() != null) {
                    isConnected = true;
                } else {
                    if (session == null) {
                        logIt("Control WebSocket has session NULL");
                    } else {
                        if (!session.isOpen()) {
                            logIt("Control WebSocket has session CLOSED");
                        } else {
                            logIt("Control WebSocket has BasicRemote NULL");
                        }
                    }
                    // These cases all denote lack of/failed connection
                    isConnected = false;
                }
            } catch (Exception e) {
                logIt("Exception in Control WebSocket: " + e.getLocalizedMessage());
                isConnected = false;
            }
        } else {
            logIt("Trying to reconnect to Control WebSocket...");
            try {
                connectToWebSocketServer();
            } catch (Exception e) {
                logIt("Error connecting to Control WebSocket: " + e.getLocalizedMessage());
            }
        }
    }

//    @Override
//    public void onOpen(Session sn, EndpointConfig ec) {
//        logIt("Opening /control endpoint...");
//    }
}
