import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.util.ArrayList;
import java.util.List;

public class MainHandler extends SimpleChannelInboundHandler<String> {
    private static final List<Channel> channels = new ArrayList<>();
    private static int newClientIndex = 1;
    private String clientName;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client has been connected" + ctx);
        channels.add(ctx.channel());
        clientName = "Client#" + channels.size();
        newClientIndex++;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println("Got the Message: " + s);
        String out = String.format("[%s]: %s\n",clientName, s);
        for (Channel c : channels){
            c.writeAndFlush(out);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Client " + clientName + " disconnected");
        channels.remove(ctx.channel());
        ctx.close();
    }
}
