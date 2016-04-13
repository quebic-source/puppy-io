package com.lovi.puppy.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

public class MessageBodyCodec implements MessageCodec<MessageBody, MessageBody> {

	private static final Logger logger = LoggerFactory.getLogger(MessageBodyCodec.class);
	
	@Override
	public void encodeToWire(Buffer buffer, MessageBody messageBody) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(output);
			objectOutputStream.writeObject(messageBody);
			byte[] bytes = output.toByteArray();
			buffer.appendInt(bytes.length);
			buffer.appendBytes(bytes);
		} catch (Exception e) {
			logger.error("encodeToWire {}",e.toString());
		}
	}

	@Override
	public MessageBody decodeFromWire(int position, Buffer buffer) {
		
		//message starting from this *position* of buffer
		int _pos = position;
		
		int length = buffer.getInt(_pos);

		// Jump 4 because getInt() == 4 bytes
		int start = _pos + 4;
		int end = start + length;
		byte[] bytes = buffer.getBytes(start, end);
		MessageBody messageBody = null;
		
		try {
			InputStream input = new ByteArrayInputStream(bytes);
        	ObjectInputStream objectInputStream = new ObjectInputStream(input);
        	messageBody = (MessageBody)objectInputStream.readObject();
		} catch (Exception e) {
			logger.error("decodeFromWire {}",e.toString());
		}
		return messageBody;
	}

	@Override
	public MessageBody transform(MessageBody messageBody) {
		return messageBody;
	}

	@Override
	public String name() {
		return this.getClass().getSimpleName();
	}

	@Override
	public byte systemCodecID() {
		return -1;
	}

}
