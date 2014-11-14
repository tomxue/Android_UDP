package com.tomxue.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import com.whitebyte.wifihotspotutils.ClientScanResult;
import com.whitebyte.wifihotspotutils.FinishScanListener;
import com.whitebyte.wifihotspotutils.WifiApManager;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
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
	private Button btSend, btClear, btClose;
	private final int RECV_BUF_SZE = 4096;

	WifiApManager wifiApManager;
	wifiManager wifimanager;
	private ScreenManager sManager;

	private int sendPacket(int localPort, String remoteIP, int remotePort,
			String payload) throws IOException {
		InetAddress ipTarget = InetAddress.getByName(remoteIP);

		// remote port to generate the packet for sending
		DatagramPacket packet = new DatagramPacket(payload.getBytes(),
				payload.length(), ipTarget, remotePort);

		if (socket != null) {
			try {
				socket.send(packet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// socket.disconnect();
		// socket.close();

		return 0;
	}

	private void recvPacket() throws IOException {
		byte[] recvByteArray = new byte[RECV_BUF_SZE];
		DatagramPacket packet = new DatagramPacket(recvByteArray,
				recvByteArray.length);
		if (socket != null)
			socket.receive(packet);
		else {
			return;
		}
		recvString = new String(recvByteArray, 0, packet.getLength());
		// Log.i("Udp tutorial", "message:" + recvString);

		Message message = new Message();
		message.what = 1;
		Bundle bundle = new Bundle();
		bundle.putString("recvStr", recvString);
		bundle.putString("peerAddr", packet.getAddress().getHostAddress()
				.toString());
		bundle.putString("localAddr", GetLocalIpAddress());
		message.setData(bundle);
		mHandler.sendMessage(message);

		// socket.disconnect();
		// socket.close();
	}

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
		btClose = (Button) findViewById(R.id.buttonClose);
		btClear = (Button) findViewById(R.id.buttonClear);
		recvText = (EditText) findViewById(R.id.RecvText);
		peerAddr = (EditText) findViewById(R.id.EditText_peerAddr);
		myAddr = (EditText) findViewById(R.id.EditText_myIP);

		socketCreate();
		recvText.setKeyListener(null);

		wifiApManager = new WifiApManager(this);
//		scan();
//		wifiApManager.setWifiApEnabled(null, true);
//
		wifimanager = new wifiManager();
		
		sManager = new ScreenManager(this);
		sManager.keepScreenOn(true);

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

		btClose.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				wifiApManager.setWifiApEnabled(null, false);

				// open wifi settings GUI for users, user has choice for the
				// next action: to enable wifi or not
				// final Intent intent = new Intent(Intent.ACTION_MAIN, null);
				// intent.addCategory(Intent.CATEGORY_LAUNCHER);
				// final ComponentName cn = new ComponentName(
				// "com.android.settings",
				// "com.android.settings.wifi.WifiSettings");
				// intent.setComponent(cn);
				// intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);

//				wifiEn.enableWifi(AndroidUDP.this, true);

				sManager.keepScreenOn(false);
				AndroidUDP.this.finish();
			}
		});

		Toast.makeText(this, "Author: tomxue@outlook.com", Toast.LENGTH_LONG)
				.show();
	}

	private void scan() {
		wifiApManager.getClientList(false, new FinishScanListener() {

			@Override
			public void onFinishScan(final ArrayList<ClientScanResult> clients) {

				recvText.setText("WifiApState: "
						+ wifiApManager.getWifiApState() + "\n\n");
				recvText.append("Clients: \n");
				for (ClientScanResult clientScanResult : clients) {
					recvText.append("####################\n");
					recvText.append("IpAddr: " + clientScanResult.getIpAddr()
							+ "\n");
					recvText.append("Device: " + clientScanResult.getDevice()
							+ "\n");
					recvText.append("HWAddr: " + clientScanResult.getHWAddr()
							+ "\n");
					recvText.append("isReachable: "
							+ clientScanResult.isReachable() + "\n");
				}
			}
		});
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Get Clients");
		menu.add(0, 1, 0, "Open AP");
		menu.add(0, 2, 0, "Close AP");
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			scan();
			break;
		case 1:
			wifimanager.setWifi(this, false);
			wifiApManager.setWifiApEnabled(null, true);
			break;
		case 2:
			wifimanager.setWifi(this, true);
			wifiApManager.setWifiApEnabled(null, false);
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		RecvThread recvThread = new RecvThread();
		new Thread(recvThread).start();

//		TimerTask task1 = new TimerTask() {
//			public void run() {
//				wifimanager.setWifi(AndroidUDP.this, false);
//			}
//		};
//
//		TimerTask task2 = new TimerTask() {
//			public void run() {
//				wifiApManager.setWifiApEnabled(null, true);
//			}
//		};
//
//		TimerTask task3 = new TimerTask() {
//			public void run() {
//				scan();
//			}
//		};
//
//		Timer timer = new Timer(true);
//		timer.schedule(task1, 1000);
//		timer.schedule(task2, 2000);
//		timer.schedule(task3, 4000);
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
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}