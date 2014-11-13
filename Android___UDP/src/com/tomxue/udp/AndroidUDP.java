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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.wifi.WifiManager;
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
	
	public EditText recvText;	
	private EditText localPort;
	private EditText destinationIP;
	private EditText destinationPort;
	private EditText sentContent;
	private Button btSend, btClear, btClose;	
	public EditText peerAddr;
	public EditText myAddr;

	WifiApManager wifiApManager;
	wifiEnabler wifiEn;
	UdpHelper udphelper;	

	private void socketCreate() {
		try { // local port to generate the socket
			udphelper.socket = new DatagramSocket(Integer.parseInt(localPort.getText()
					.toString()));
			udphelper.socket.setBroadcast(true);
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
		scanNetwork();
		wifiApManager.setWifiAp(null, true);

		wifiEn = new wifiEnabler();

		btSend.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int rport = Integer.parseInt(destinationPort.getText()
						.toString());
				int lport = Integer.parseInt(localPort.getText().toString());
				try {
					udphelper.sendPacket(lport, destinationIP.getText().toString(),
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
				wifiApManager.setWifiAp(null, false);

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

				wifiEn.setWifi(AndroidUDP.this, true);

				AndroidUDP.this.finish();
			}
		});

		Toast.makeText(this, "Author: tomxue@outlook.com", Toast.LENGTH_LONG)
				.show();
	}

	private void scanNetwork() {
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
			scanNetwork();
			break;
		case 1:
			wifiApManager.setWifiAp(null, true);
			break;
		case 2:
			wifiApManager.setWifiAp(null, false);
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		WifiManager wfmanager = (WifiManager) this
				.getSystemService(AndroidUDP.WIFI_SERVICE);
		udphelper = new UdpHelper(this, wfmanager);
		new Thread(udphelper).start();

		initWork();
	}

	private void initWork() {
		TimerTask task1 = new TimerTask() {
			public void run() {
				wifiEn.setWifi(AndroidUDP.this, false);
			}
		};

		TimerTask task2 = new TimerTask() {
			public void run() {
				wifiApManager.setWifiAp(null, true);
			}
		};

		TimerTask task3 = new TimerTask() {
			public void run() {
				scanNetwork();
			}
		};

		Timer timer = new Timer(true);
		timer.schedule(task1, 1000);
		timer.schedule(task2, 2000);
		timer.schedule(task3, 4000);
	}	
}