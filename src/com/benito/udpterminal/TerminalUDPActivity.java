package com.benito.udpterminal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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

public class TerminalUDPActivity extends Activity {

	private String recvContentText;
	private EditText recvText;
	private DatagramSocket socket;

	private int sendPacket(int port_local, String ip_target, int port_target,
			String payload) throws IOException {
		InetAddress ipTarget = InetAddress.getByName(ip_target);
		
		DatagramPacket packet = new DatagramPacket(payload.getBytes(),
				payload.length(), ipTarget, port_target);

		socket.send(packet);
//		socket.disconnect();
//		socket.close();

		return 0;
	}

	private void recvPacket() throws IOException {
		byte[] sentContent = new byte[256];
		DatagramPacket packet = new DatagramPacket(sentContent,
				sentContent.length);
		socket.receive(packet);
		recvContentText = new String(sentContent, 0, packet.getLength());
		Log.i("Udp tutorial", "message:" + recvContentText);
		Message message = new Message();
		message.what = 1;
		mHandler.sendMessage(message);
//		socket.disconnect();
//		socket.close();
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				recvText.setText(recvContentText);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final EditText localPort = (EditText) findViewById(R.id.editTextLocalPort);
		final EditText destinationIP = (EditText) findViewById(R.id.editTextIp);
		final EditText destinationPort = (EditText) findViewById(R.id.editTextPorta);
		final EditText sentContent = (EditText) findViewById(R.id.editTextPayload);
		final Button btSend = (Button) findViewById(R.id.buttonSend);
		recvText = (EditText) findViewById(R.id.RecvText);
		try {
			socket  = new DatagramSocket(5000);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		btSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				String texto = destinationIP.getText() + ":"
						+ destinationPort.getText() + " - Payload: "
						+ sentContent.getText();
//				Toast.makeText(TerminalUDPActivity.this, "Sending:\n" + texto,
//						Toast.LENGTH_LONG).show();

				int port = Integer.parseInt(destinationPort.getText()
						.toString());
				int lport = Integer.parseInt(localPort.getText().toString());
				try {
					sendPacket(lport, destinationIP.getText().toString(), port,
							sentContent.getText().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
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
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}