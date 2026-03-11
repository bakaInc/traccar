package org.traccar.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.checkerframework.checker.units.qual.C;
import org.traccar.BaseProtocolEncoder;
import org.traccar.Protocol;
import org.traccar.helper.Checksum;
import org.traccar.model.Command;
import org.traccar.model.Device;

import java.nio.ByteBuffer;

public class ArnaviBinaryProtocolEncoder extends BaseProtocolEncoder {
    public ArnaviBinaryProtocolEncoder(Protocol protocol) {
        super(protocol);
    }

    private ByteBuf encodeBytes(byte[] bytes, long deviceId) {
        ByteBuf response = Unpooled.buffer();

        byte START = 0x7B; // Стандартный заголовок начала ответа
        byte COMMAND_SIZE = (byte) bytes.length; // Размер поля команды
        byte PARCEL_NUMBER = (byte) getCacheManager().getObject(Device.class, deviceId).getArnaviParcelNumber(); // На что отвечаем
        byte COMMAND_CHECKSUM = (byte) Checksum.sum(ByteBuffer.wrap(bytes));
        byte END = 0x7D; // Конец ответа

        response.writeByte(START);
        response.writeByte(COMMAND_SIZE);
        response.writeByte(PARCEL_NUMBER);
        response.writeByte(COMMAND_CHECKSUM);
        response.writeBytes(bytes);
        response.writeByte(END);

        return response;
    }

    @Override
    protected Object encodeCommand(Command command) {
        switch (command.getType()) {
            case Command.TYPE_ENGINE_RESUME:
                return encodeBytes(new byte[] {0x08, 0x01, 0x00}, command.getDeviceId());
            case Command.TYPE_ENGINE_STOP:
                return encodeBytes(new byte[] {0x08, 0x01, 0x01}, command.getDeviceId());
            default:
                return null;
        }
    }
}
