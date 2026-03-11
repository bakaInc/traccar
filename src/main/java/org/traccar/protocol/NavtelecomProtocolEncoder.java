package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.traccar.BaseProtocolEncoder;
import org.traccar.Protocol;
import org.traccar.helper.Checksum;
import org.traccar.model.Command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class NavtelecomProtocolEncoder extends BaseProtocolEncoder {
    public NavtelecomProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private ByteBuf encodeContent(long deviceId, String content) {
        ByteBuf header = Unpooled.buffer();
        long sender = getNavtelecomSender(deviceId);
        long receiver = getNavtelecomReceiver(deviceId);

        header.writeBytes("@NTC".getBytes(StandardCharsets.US_ASCII));
        header.writeIntLE((int) sender);
        header.writeIntLE((int) receiver);

        byte[] content_bytes = content.getBytes(StandardCharsets.US_ASCII);

        header.writeByte(content_bytes.length);
        header.writeByte(0);
        header.writeByte(Checksum.xor(ByteBuffer.wrap(content_bytes)));

        header.writeByte(Checksum.xor(header.nioBuffer()));

        ByteBuf response = Unpooled.buffer();
        response.writeBytes(header);
        response.writeBytes(content_bytes);

        return response;
    }

    @Override
    protected Object encodeCommand(Command command) {
        return switch (command.getType()) {
            case Command.TYPE_ENGINE_RESUME -> encodeContent(command.getDeviceId(), "*!1N");
            case Command.TYPE_ENGINE_STOP -> encodeContent(command.getDeviceId(), "*!1Y");
            default -> null;
        };
    }
}
