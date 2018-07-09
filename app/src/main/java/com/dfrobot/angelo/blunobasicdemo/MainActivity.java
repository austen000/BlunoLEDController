package com.dfrobot.angelo.blunobasicdemo;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity  extends BlunoLibrary {
	private Button buttonScan;
	private Button buttonSerialSend;
	private Button buttonAddOne;
	private Button buttonMinusOne;
	private EditText serialSendText;
	private TextView serialReceivedText;
	private SeekBar seekBar;
	private Toast mToast;
	private int moduleNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onCreateProcess();                                                        //onCreate Process by BlunoLibrary


		serialBegin(115200);                                                    //set the Uart Baudrate on BLE chip to 115200
		mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

		serialReceivedText = (TextView) findViewById(R.id.serialReveicedText);    //initial the EditText of the received data
		serialSendText = (EditText) findViewById(R.id.serialSendText);            //initial the EditText of the sending data

		buttonSerialSend = (Button) findViewById(R.id.buttonSerialSend);        //initial the button for sending the data
		buttonSerialSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				serialSend(serialSendText.getText().toString());                //send the data to the BLUNO
			}
		});

		buttonScan = (Button) findViewById(R.id.buttonScan);                    //initial the button for scanning the BLE device
		buttonScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				buttonScanOnClickProcess();                                        //Alert Dialog for selecting the BLE device
			}
		});

		buttonMinusOne = (Button) findViewById(R.id.buttonMinusOne);
		buttonMinusOne.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(seekBar.getProgress()!=0) {
					serialSend(Integer.toString(seekBar.getProgress() - 1) + "~");
					seekBar.setProgress(seekBar.getProgress() - 1);
					mToast.setText(seekBar.getProgress() + "% brightness");
					mToast.show();
				}else{
					mToast.setText(seekBar.getProgress() + "% brightness");
					mToast.show();
				}
			}
		});

		buttonAddOne = (Button) findViewById(R.id.buttonAddOne);
		buttonAddOne.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if(seekBar.getProgress()!=100) {
					serialSend(Integer.toString(seekBar.getProgress() + 1) + "~");
					seekBar.setProgress(seekBar.getProgress() + 1);
					mToast.setText(seekBar.getProgress() + "% brightness");
					mToast.show();
				}else{
					mToast.setText(seekBar.getProgress() + "% brightness");
					mToast.show();
				}

			}
		});

		seekBar = (SeekBar) findViewById(R.id.seekBar3);
		if (seekBar != null) {
			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					// Write code to perform some action when progress is changed.
					if(fromUser) {
						serialSend(Integer.toString(progress) + "~");
					}
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// Write code to perform some action when touch is started.
				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// Write code to perform some action when touch is stopped.
					//Toast.makeText(MainActivity.this, seekBar.getProgress() + "% brightness", Toast.LENGTH_SHORT).show();
					mToast.setText(seekBar.getProgress() + "% brightness");
					mToast.show();
					//serialSend(Integer.toString(seekBar.getProgress()));
					//serialSend("Sent: " + Integer.toString(seekBar.getProgress()));
				}
			});
		}
	}

	protected void onResume(){
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();														//onResume Process by BlunoLibrary
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
		super.onActivityResult(requestCode, resultCode, data);
	}
	/*
    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }
	
	protected void onStop() {
		super.onStop();
		onStopProcess();														//onStop Process by BlunoLibrary
	}
    */
	@Override
    protected void onDestroy() {
        super.onDestroy();	
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
		switch (theConnectionState) {											//Four connection state
		case isConnected:
			buttonScan.setText("Connected");
			serialSend("Rqst");                      //request starting brightness
			break;
		case isConnecting:
			buttonScan.setText("Connecting");
			break;
		case isToScan:
			buttonScan.setText("Scan");
			break;
		case isScanning:
			buttonScan.setText("Scanning");
			break;
		case isDisconnecting:
			buttonScan.setText("isDisconnecting");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {							//Once connection data received, this function will be called
		// TODO Auto-generated method stub

		if(theString.startsWith("E:")){
			//System.out.println("*****************" + theString + "**********");
			if(theString.startsWith("E: +5")){
				try {
					int updatedProgress = Integer.parseInt(theString.substring(6, theString.length() - 2));
					if(moduleNumber == 1) {
						seekBar.setProgress((int) ((updatedProgress - 15) / 2.4));
					}
					if(moduleNumber == 2) {
						seekBar.setProgress((int) ((updatedProgress - 90) / 1.65));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				//System.out.println("*****************" + theString);
			}else if(theString.startsWith("E: -5")){
				try {
					int updatedProgress = Integer.parseInt(theString.substring(6, theString.length() - 2));
					if(moduleNumber == 1) {
						seekBar.setProgress((int) ((updatedProgress - 15) / 2.4));
					}
					if(moduleNumber == 2) {
						seekBar.setProgress((int) ((updatedProgress - 90) / 1.65));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			//serialReceivedText.append(theString);							//append the text into the EditText
		}else if(theString.startsWith("start1")){
			moduleNumber = 1;
			int startPosition = Integer.parseInt(theString.substring(7));
			seekBar.setProgress(startPosition);
		}else if(theString.startsWith("start2")){
			moduleNumber = 2;
			int startPosition = Integer.parseInt(theString.substring(7));
			seekBar.setProgress(startPosition);
		}else{
			//serialReceivedText.append(theString);							//append the text into the EditText
		}
		//The Serial data from the BLUNO may be sub-packaged, so using a buffer to hold the String is a good choice.
		((ScrollView)serialReceivedText.getParent()).fullScroll(View.FOCUS_DOWN);
		//seekBar.setProgress(10);
	}

}