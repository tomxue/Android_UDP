package com.tomxue.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.benito.udpterminal.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AndroidUDP extends Activity {

	private String recvContentText;
	private EditText recvText;
	private DatagramSocket socket;
	private EditText localPort;
	private EditText destinationIP;
	private EditText destinationPort;
	private EditText sentContent;
	private Button btSend, btClear;

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
		byte[] sentContent = new byte[4096];
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
	
	private void socketInit()
	{
		try {
			socket  = new DatagramSocket(Integer.parseInt(localPort.getText().toString()));
		} catch (SocketException e1) {
			e1.printStackTrace();
		}		
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

		localPort = (EditText) findViewById(R.id.editTextLocalPort);
		destinationIP = (EditText) findViewById(R.id.editTextIp);
		destinationPort = (EditText) findViewById(R.id.editTextPorta);
		sentContent = (EditText) findViewById(R.id.editTextPayload);
		btSend = (Button) findViewById(R.id.buttonSend);
		btClear = (Button) findViewById(R.id.buttonClear);
		recvText = (EditText) findViewById(R.id.RecvText);
		
		socketInit();

		btSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

//				String texto = destinationIP.getText() + ":"
//						+ destinationPort.getText() + " - Payload: "
//						+ sentContent.getText();
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
		
		btClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				recvText.setText(".");
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