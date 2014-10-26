package com.benito.udpterminal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TerminalUDPActivity extends Activity {
	
	private int sendPacket(int lPorta,String ipHost, int port, String payload) throws IOException {
		
		InetAddress address = InetAddress.getByName(ipHost);
				
		DatagramSocket socket = new DatagramSocket(lPorta);
		
		DatagramPacket packet = new DatagramPacket(payload.getBytes(),payload.length(),address,port);
		
		socket.send(packet);
		socket.disconnect();
		socket.close();
		
		return 0;
	}
	
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
        
        btSend.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {

        		String texto = destinationIP.getText()+":"+destinationPort.getText()+
        		               " - Payload: "+sentContent.getText();
        		Toast.makeText(TerminalUDPActivity.this, "enviando:\n"+texto ,Toast.LENGTH_LONG).show();
        		
        		int port = Integer.parseInt(destinationPort.getText().toString());
        		int lport = Integer.parseInt(localPort.getText().toString());
        		try {
					sendPacket(lport,destinationIP.getText().toString(), port, sentContent.getText().toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        });
        
    }
}