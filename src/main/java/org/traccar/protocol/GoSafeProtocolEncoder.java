package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.traccar.BaseProtocolEncoder;
import org.traccar.Protocol;
import org.traccar.model.Command;

import java.nio.charset.StandardCharsets;

public class GoSafeProtocolEncoder extends BaseProtocolEncoder {
    public GoSafeProtocolEncoder (Protocol protocol) {
        super(protocol);
    }

    @Override
    protected Object encodeCommand(Command command) {
        ByteBuf response = Unpooled.buffer();
        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                response.writeBytes("*GS06,DOO0;0#".getBytes(StandardCharsets.US_ASCII));
                return response;
            case Command.TYPE_ENGINE_RESUME:
                response.writeBytes("*GS06,DOO0;1#".getBytes(StandardCharsets.US_ASCII));
                return response;
            default:
                return null;
        }
    }
}
