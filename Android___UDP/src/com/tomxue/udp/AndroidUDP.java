package com.tomxue.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AndroidUDP extends Activity {

	private String recvString;
	private EditText recvText;
	private DatagramSocket socket;
	private EditText localPort;
	private EditText destinationIP;
	private EditText destinationPort;
	private EditText sentContent;
	private EditText peerAddr;
	private EditText myAddr;
	private Button btSend, btClear;
	private final int RECV_BUF_SZE = 4096;

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

	private int sendPacket(int localPort, String remoteIP, int remotePort,
			String payload) throws IOException {
		InetAddress ipTarget = InetAddress.getByName(remoteIP);

		// remote port to generate the packet for sending
		DatagramPacket packet = new DatagramPacket(payload.getBytes(),
				payload.length(), ipTarget, remotePort);

		socket.send(packet);
		// socket.disconnect();
		// socket.close();

		return 0;
	}

	private void recvPacket() throws IOException {
		byte[] recvByteArray = new byte[RECV_BUF_SZE];
		DatagramPacket packet = new DatagramPacket(recvByteArray,
				recvByteArray.length);
		socket.receive(packet);
		recvString = new String(recvByteArray, 0, packet.getLength());
		// Log.i("Udp tutorial", "message:" + recvString);

		Message message = new Message();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("recvStr", recvString);
		bundle.putString("peerAddr", packet.getAddress().getHostAddress().toString());
		bundle.putString("localAddr", GetLocalIpAddress());
		message.setData(bundle);
		mHandler.sendMessage(message);

		// socket.disconnect();
		// socket.close();
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				recvText.append(msg.getData().getString("recvStr"));
				recvText.append(" ");
				peerAddr.setText(msg.getData().getString("peerAddr"));
				myAddr.setText(msg.getData().getString("localAddr"));
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void socketCreate() {
		try { // local port to generate the socket
			socket = new DatagramSocket(Integer.parseInt(localPort.getText()
					.toString()));
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		localPort = (EditText) findViewById(R.id.editTextLocalPort);
		destinationIP = (EditText) findViewById(R.id.editTextIp);
		destinationPort = (EditText) findViewById(R.id.editTextPorta);
		sentContent = (EditText) findViewById(R.id.editTextPayload);
		btSend = (Button) findViewById(R.id.buttonSend);
		btClear = (Button) findViewById(R.id.buttonClear);
		recvText = (EditText) findViewById(R.id.RecvText);
		peerAddr = (EditText) findViewById(R.id.EditText_peerAddr);
		myAddr = (EditText) findViewById(R.id.EditText_myIP);

		socketCreate();
		recvText.setKeyListener(null);

		btSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int rport = Integer.parseInt(destinationPort.getText()
						.toString());
				int lport = Integer.parseInt(localPort.getText().toString());
				try {
					sendPacket(lport, destinationIP.getText().toString(),
							rport, sentContent.getText().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		btClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				recvText.setText("");
			}
		});
		
		Toast.makeText(this, "Author: tomxue@outlook.com", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		RecvThread recvThread = new RecvThread();
		new Thread(recvThread).start();
	}

	public class RecvThread implements Runnable {
		public RecvThread() {
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					recvPacket();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				// try {
				// Thread.sleep(1000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
			}
		}
	}
}