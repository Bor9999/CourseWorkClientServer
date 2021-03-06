package chatClient.pac;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Network {
    private SocketChannel channel;
    private static final String HOST = "localhost";
    private static final int PORT = 8189;
    private Callback onMessageReceivedCallback;
    public Network(Callback onMessageReceivedCallback){
this.onMessageReceivedCallback = onMessageReceivedCallback;
        new Thread(()->{
            EventLoopGroup workergroup = new NioEventLoopGroup();
            try{
                Bootstrap b = new Bootstrap();
                b.group(workergroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        channel = socketChannel;
                        socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new SimpleChannelInboundHandler<String>() {

                            @Override
                            protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                                if(onMessageReceivedCallback != null){
                                    onMessageReceivedCallback.callback(s);
                                }
                            }
                        });

                    }
                });
                ChannelFuture future = b.connect(HOST,PORT).sync();
                future.channel().closeFuture().sync();
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
    public void sendMessage(String str) {
        channel.writeAndFlush(str);
    }
}
