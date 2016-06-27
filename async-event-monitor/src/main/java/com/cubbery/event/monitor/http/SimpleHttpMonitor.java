/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.monitor.http;

import com.cubbery.event.Statistics;
import com.cubbery.event.monitor.MonitorServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>修改人</b>：   <br>
 * <b>创建时间</b>： 2016/3/28 - 14:19  <br>
 * @version 1.0.0   <br>
 */
public final class SimpleHttpMonitor extends MonitorServer {
    private final static Logger _LOG = LoggerFactory.getLogger(SimpleHttpMonitor.class);
    private static final Set<String> _PAGE = new HashSet<String>(2);
    private static final String _CHANNEL = "/channel";
    private static final String _RETRY = "/retry";
    private volatile boolean running;

    static {
        _PAGE.add("/retry.html");
        _PAGE.add("/channel.html");
    }

    private final int port ;
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    public SimpleHttpMonitor() {
        this.port = 8888;
    }

    public SimpleHttpMonitor(int port) {
        this.port = port;
    }

    public SimpleHttpMonitor(int port, List<Statistics> samples) {
        super(samples);
        this.port = port;
    }

    @Override
    public synchronized void close() {
        try {
            running = false;
            this.eventLoopGroup.shutdownGracefully().await(5 * 1000L);
            _LOG.info("Monitor Server Have Stopped!");
        } catch (InterruptedException e) {
            _LOG.info("Try To Close Server Err! ",e);
        }
    }

    @Override
    public synchronized void startUp() {
        if(running) {
            _LOG.info("Monitor Server Has Been Started!");
            return;
        }
        ServerBootstrap b = new ServerBootstrap();
        b.option(ChannelOption.SO_BACKLOG, 1024);
        b.option(ChannelOption.SO_REUSEADDR, true);
        b.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new ChannelInit());
        try {
            b.bind(port).sync();
            running = true;
            _LOG.info("Monitor Server Have Started!");
        } catch (Exception ex) {
            _LOG.error("Error create http server", ex);
        }
    }


    class ChannelInit extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline p = socketChannel.pipeline();
            p.addLast(new HttpServerCodec());
            p.addLast(new ChannelAdapter());
        }
    }

    class ChannelAdapter extends ChannelInboundHandlerAdapter {

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            _LOG.error("Http Server Has Some UnExpected Err !",cause);
            ctx.close();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                resolve(ctx,req);
            }
        }

        public void resolve(ChannelHandlerContext ctx,HttpRequest req) {
            String uri = req.getUri();
            if(uri.endsWith(".html") && _PAGE.contains(uri)) {
                responseHtml(ctx,uri);
            } else if (_CHANNEL.equals(uri)) {
                String data = samples.get(Statistics.Type.CHANNEL).toString();
                response(ctx,data);
            } else if (_RETRY.equals(uri)) {
                response(ctx,"SUCCESS");
            } else {
                ctx.close();
            }
        }

        private void response(ChannelHandlerContext ctx, String s) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(s.getBytes()));
            response.headers().set(CONTENT_TYPE, "text/plain;charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }

        private void responseHtml(ChannelHandlerContext ctx, String page) {
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(readHtml(page).getBytes()));
            response.headers().set(CONTENT_TYPE, "text/html;charset=UTF-8");
            response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }

        private String readHtml(String html) {
            StringBuilder buf = new StringBuilder();
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(SimpleHttpMonitor.class.getResourceAsStream("/html/" + html)));
                String line;
                while ((line = in.readLine()) != null) {
                    buf.append(line);
                    buf.append("\n");
                }
            } catch (Exception ex) {
                _LOG.error("Read Html Err", ex);
            } finally {
                if (null != in) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        _LOG.error("Close Err", e);
                    }
                }
            }
            return buf.toString();
        }
    }
}