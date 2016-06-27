package hqio.hqpbservice;

import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.Message;

public class HQPacket {
	private int opcode;
	private byte[] data;
	
	public HQPacket(int opcode, byte[] pbdata) {
		this.opcode = opcode;
		this.data = pbdata;
	}
	
	public HQPacket(int opcode, Builder<?> builder) {
		this.opcode = opcode; 
		Message msg = builder.build();
		this.data = msg.toByteArray();
	}
	
	public int getOpcode() {
		return opcode;
	}
	public void setOpcode(int opcode) {
		this.opcode = opcode;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
}
