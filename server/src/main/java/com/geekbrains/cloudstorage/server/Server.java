package com.geekbrains.cloudstorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * This class implements simple netty Server.
 *
 * @author @FlameXander
 */
public final class Server {

    /**
     * Server class default constructor.
     */
    private Server() {
    }

    /**
     * Method to start server.
     *
     * @throws Exception if there is an issue.
     */
    private void run() throws Exception {

        //  Configuration for local variables.
        final int maxObjectSize = 1024 * 1024;
        final int soBackLog = 128;
        final int bindPort = 8189;

        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(mainGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        protected void initChannel(
                                final SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(maxObjectSize,
                                            ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new MainHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, soBackLog)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = b.bind(bindPort).sync();
            future.channel().closeFuture().sync();
        } finally {
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Main method, that launch server.
     *
     * @param args String[] of args
     * @throws Exception if there is an issue.
     */
    public static void main(final String[] args) throws Exception {
        new Server().run();
    }
}
