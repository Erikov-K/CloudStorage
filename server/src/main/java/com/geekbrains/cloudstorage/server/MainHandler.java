package com.geekbrains.cloudstorage.server;

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

public class MainHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        // обновление сиска файлов на сервере при подключени клиента
        FileListMessage flm = new FileListMessage(getFilesList());
        ctx.writeAndFlush(flm);
//        super.channelRegistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg == null) {
                return;
            }
            // отправка клиенту списка файлов сервера
            if (msg instanceof FileListMessage) {
                FileListMessage flm = new FileListMessage(getFilesList());
                ctx.writeAndFlush(flm);
            }

            // отправка файла сервером клиенту
            if (msg instanceof FileRequest) {

                FileRequest fr = (FileRequest) msg;
                if (Files.exists(Paths.get("server_storage/" + fr.getFilename()))) {
                    FileMessage fm = new FileMessage(Paths.get("server_storage/" + fr.getFilename()));
                    ctx.writeAndFlush(fm);
                }
            }
            // прием файла сервером
            if (msg instanceof FileMessage) {
                FileMessage fm = (FileMessage) msg;
                Files.write(Paths.get("server_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                // обновление содержимого сервера на клиенте
                FileListMessage flm = new FileListMessage(getFilesList());
                ctx.writeAndFlush(flm);
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    public List<String> getFilesList() {
        List<String> files = new ArrayList<>();
        try {
            Files.newDirectoryStream(Paths.get("server_storage/")).forEach(path -> files.add(path.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
}
