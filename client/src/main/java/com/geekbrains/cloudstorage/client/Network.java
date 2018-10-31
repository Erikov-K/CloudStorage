package com.geekbrains.cloudstorage.client;

import com.geekbrains.cloudstorage.common.AbstractMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

/**
 * This class implements client network.
 * Uses for organise connection between client and server.
 *
 * @author @FlameXander
 */
final class Network {

    /**
     * Local variable 'String host'.
     * Contain connection point to server.
     */
    private static final String HOST = "localhost";

    /**
     * Local variable 'int port'.
     * Contain connection point port value.
     */
    private static final int PORT = 8189;

    /**
     * Local variable 'Socket socket'.
     * Contain server socket to specified host and port.
     */
    private static Socket socket;

    /**
     * Local variable 'ObjectEncoderOutputStream out'.
     * Serializable netty Object Encoder Output Stream.
     */
    private static ObjectEncoderOutputStream out;

    /**
     * Local variable 'ObjectDecoderInputStream in'.
     * Serializable netty Object Decoder Input Stream.
     */
    private static ObjectDecoderInputStream in;

    /**
     * Network class default constructor.
     */
    private Network() {
    }

    /**
     * Method to start network.
     */
    static void start() {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to stop network.
     */
    static void stop() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to send message through network.
     *
     * @param msg AbstractMessage
     */
    static void sendMsg(final AbstractMessage msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to send message through network.
     *
     * @return obj
     * @throws ClassNotFoundException if there is an issue
     * @throws IOException            if there is an issue
     */
    static AbstractMessage readObject()
            throws ClassNotFoundException, IOException {
        Object obj = in.readObject();
        return (AbstractMessage) obj;
    }
}
