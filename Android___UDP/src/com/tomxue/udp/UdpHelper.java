package com.tomxue.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;

public class UdpHelper implements Runnable {
	public Boolean IsThreadEnable = true;
	private WifiManager.MulticastLock lock;
	public DatagramSocket socket;
	private final int RECV_BUF_SZE = 4096;
	private String recvString;
	private Context mainContext;

	public UdpHelper(Context context, WifiManager manager) {
		this.lock = manager.createMulticastLock("UDPwifi");
		mainContext = context;
	}

	@Override
	public void run() {
		StartListen();
	}

	public int sendPacket(int localPort, String remoteIP, int remotePort,
			String payload) throws IOException {
		InetAddress ipTarget = InetAddress.getByName(remoteIP);

		// remote port to generate the packet for sending
		DatagramPacket packet = new DatagramPacket(payload.getBytes(),
				payload.length(), ipTarget, remotePort);

		if (socket != null) {
			try {
				this.lock.acquire();
				socket.send(packet);
				this.lock.release();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// socket.disconnect();
		// socket.close();

		return 0;
	}

	public void StartListen() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				this.lock.acquire();
				recvPacket();
				this.lock.release();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}
	}

	private void recvPacket() throws IOException {
		byte[] recvByteArray = new byte[RECV_BUF_SZE];
		DatagramPacket packet = null;
		try {
			packet = new DatagramPacket(recvByteArray, recvByteArray.length);
			try {
				socket.receive(packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (packet != null) {
			recvString = new String(recvByteArray, 0, packet.getLength());
			GUImessageSend(recvString, packet);
		}

		// socket.disconnect();
		// socket.close();
	}

	private void GUImessageSend(String str, DatagramPacket p) {
		Message message = new Message();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("recvStr", str);
		bundle.putString("peerAddr", p.getAddress().getHostAddress().toString());
		bundle.putString("localAddr", GetLocalIpAddress());
		message.setData(bundle);
		mGUIHandler.sendMessage(message);
	}
	
	@SuppressLint("HandlerLeak")
	public Handler mGUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
//				recvText.append(msg.getData().getString("recvStr"));
//				recvText.append(" ");
//				peerAddr.setText(msg.getData().getString("peerAddr"));
//				myAddr.setText(msg.getData().getString("localAddr"));
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private String GetLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			return "ERROR Obtaining IP";
		}
		return "No IP Available";
	}
}
