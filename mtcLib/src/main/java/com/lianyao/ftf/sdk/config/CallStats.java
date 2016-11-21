package com.lianyao.ftf.sdk.config;

/**
 * 通话统计信息
 */
public class CallStats {
	//发送和接收的帧率
	String fpsSent = null;
	String fpsRecv = null;
	//发送和接收的带宽
	String bandwidthSent = null;
	String bandwidthRecv = null;
	//发送和接收的宽高
	String sendFrameWidth = null;
	String sendFrameHeight = null;
	String recvFrameWidth = null;
	String recvFrameHeight = null;
	//发送和接收所丢的包的数量
	String packetsLostSent = null;
	String packetsLostRecv = null;
	//发送和接收的总包数量
	String packetsSent = null;
	String packetsRecv = null;
	
	public String getFpsSent() {
		return fpsSent;
	}
	public void setFpsSent(String fpsSent) {
		this.fpsSent = fpsSent;
	}
	public String getFpsRecv() {
		return fpsRecv;
	}
	public void setFpsRecv(String fpsRecv) {
		this.fpsRecv = fpsRecv;
	}
	public String getBandwidthSent() {
		return bandwidthSent;
	}
	public void setBandwidthSent(String bandwidthSent) {
		this.bandwidthSent = bandwidthSent;
	}
	public String getBandwidthRecv() {
		return bandwidthRecv;
	}
	public void setBandwidthRecv(String bandwidthRecv) {
		this.bandwidthRecv = bandwidthRecv;
	}
	public String getSendFrameWidth() {
		return sendFrameWidth;
	}
	public void setSendFrameWidth(String sendFrameWidth) {
		this.sendFrameWidth = sendFrameWidth;
	}
	public String getSendFrameHeight() {
		return sendFrameHeight;
	}
	public void setSendFrameHeight(String sendFrameHeight) {
		this.sendFrameHeight = sendFrameHeight;
	}
	public String getRecvFrameWidth() {
		return recvFrameWidth;
	}
	public void setRecvFrameWidth(String recvFrameWidth) {
		this.recvFrameWidth = recvFrameWidth;
	}
	public String getRecvFrameHeight() {
		return recvFrameHeight;
	}
	public void setRecvFrameHeight(String recvFrameHeight) {
		this.recvFrameHeight = recvFrameHeight;
	}
	public String getPacketsLostSent() {
		return packetsLostSent;
	}
	public void setPacketsLostSent(String packetsLostSent) {
		this.packetsLostSent = packetsLostSent;
	}
	public String getPacketsLostRecv() {
		return packetsLostRecv;
	}
	public void setPacketsLostRecv(String packetsLostRecv) {
		this.packetsLostRecv = packetsLostRecv;
	}
	public String getPacketsSent() {
		return packetsSent;
	}
	public void setPacketsSent(String packetsSent) {
		this.packetsSent = packetsSent;
	}
	public String getPacketsRecv() {
		return packetsRecv;
	}
	public void setPacketsRecv(String packetsRecv) {
		this.packetsRecv = packetsRecv;
	}
}
