package com.geekbrains.cloudstorage.server;

import com.geekbrains.cloudstorage.common.FileDeleteRequest;
import com.geekbrains.cloudstorage.common.FileListMessage;
import com.geekbrains.cloudstorage.common.FileMessage;
import com.geekbrains.cloudstorage.common.FileRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements netty Server MainHandler.
 *
 * @author @FlameXander
 */
public class MainHandler extends ChannelInboundHandlerAdapter {

    /**
     * Method channelRegistered.
     * Uses for transfer list of client files when client is connected.
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelRegistered(final ChannelHandlerContext ctx) {
        FileListMessage flm = new FileListMessage(getFilesList());
        ctx.writeAndFlush(flm);
    }

    /**
     * Method channelRead.
     * Uses for reading any messages from client through network.
     * Defines client request and run different activities on server-side.
     *
     * @param ctx ChannelHandlerContext
     * @param msg Object
     * @throws Exception if there is an issue.
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx,
                            final Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }

            // Send list of client files to client
            if (msg instanceof FileListMessage) {
                FileListMessage flm = new FileListMessage(getFilesList());
                ctx.writeAndFlush(flm);
            }

            // Send requested file to client
            if (msg instanceof FileRequest) {
                FileRequest fr = (FileRequest) msg;
                if (Files.exists(
                        Paths.get("server_storage/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(
                            Paths.get("server_storage/" + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                }
            }

            // Receiving file from client
            if (msg instanceof FileMessage) {
                FileMessage fm = (FileMessage) msg;
                Files.write(
                        Paths.get(
                                "server_storage/" + fm.getFilename()),
                        fm.getData(),
                        StandardOpenOption.CREATE);
                // Send list of client files to client
                FileListMessage flm = new FileListMessage(getFilesList());
                ctx.writeAndFlush(flm);
            }

            // Remove file from server by client request
            if (msg instanceof FileDeleteRequest) {
                FileDeleteRequest fdr = (FileDeleteRequest) msg;
                if (Files.exists(
                        Paths.get("server_storage/" + fdr.getFilename()))) {
                    Files.delete(
                            Paths.get("server_storage/" + fdr.getFilename()));
                    // Send list of client files to client
                    FileListMessage flm = new FileListMessage(getFilesList());
                    ctx.writeAndFlush(flm);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * Method channelReadComplete.
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.flush();
    }

    /**
     * Method exceptionCaught.
     *
     * @param ctx   ChannelHandlerContext
     * @param cause Throwable
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx,
                                final Throwable cause) {
        ctx.close();
    }

    /**
     * Method getFileList().
     * Uses for generating client file names array, stored on server-side.
     *
     * @return files
     */
    private List<String> getFilesList() {
        List<String> files = new ArrayList<>();
        try {
            Files.newDirectoryStream(
                    Paths.get("server_storage/")).forEach(
                    path -> files.add(path.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
}
