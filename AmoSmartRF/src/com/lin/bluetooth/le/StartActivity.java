package com.lin.bluetooth.le;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lin.bluetooth.le.R;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class StartActivity extends Activity implements OnClickListener {

	private final static String TAG = "StartActivity"; // StartActivity.class.getSimpleName();
	
	// SmartRF 开发板的按键值定义
	final static int BLE_KEY_UP = 1;
	final static int BLE_KEY_DOWN = 16;
	final static int BLE_KEY_LEFT = 8;
	final static int BLE_KEY_RIGHT = 2;
	final static int BLE_KEY_CENTER = 4;
	final static int BLE_KEY_S1 = 32;
	final static int BLE_KEY_RELEASE = 0;
			
	
	static byte keyValue_save = 0;
	
	static Handler mHandler = new Handler();

	static EditText start_edit_SetDeviceName = null;

	static String DeviceName = null;
	static byte[] dht11_Sensor = new byte[4];

	static TextView start_txt_temperature = null;

	static byte[] adc0_value = new byte[2];
	static byte[] adc1_value = new byte[2];
	static TextView start_txt_ADC0ADC1 = null;

	static Button start_button_Read_ADC0ADC1 = null;

	static Button start_button_SetPwm = null;
	
	byte[] ledx_value = new byte[1];
	
	static TextView board_info_log = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		getActionBar().setTitle("AmoSmartRF蓝牙APP v1.0 20150129");

		// Intent intent = getIntent();
		// String value = intent.getStringExtra("mac_addr");

		findViewById(R.id.start_button_SetDeviceName).setOnClickListener(this);
		findViewById(R.id.start_button_Read_ADC0ADC1).setOnClickListener(this);
		findViewById(R.id.start_button_SetPwm).setOnClickListener(this);

		board_info_log = (TextView) findViewById(R.id.board_info_log);
		start_edit_SetDeviceName = (EditText) findViewById(R.id.start_edit_SetDeviceName);
		start_txt_temperature = (TextView) findViewById(R.id.start_txt_temperature);
		start_txt_ADC0ADC1 = (TextView) findViewById(R.id.start_txt_ADC0ADC1);

		start_button_Read_ADC0ADC1 = (Button) findViewById(R.id.start_button_Read_ADC0ADC1);
		start_button_SetPwm = (Button) findViewById(R.id.start_button_SetPwm);

		adc0_value[0] = 0;
		adc0_value[1] = 0;

		adc1_value[0] = 0;
		adc1_value[1] = 0;

		dht11_Sensor[0] = 0;
		dht11_Sensor[1] = 0;
		dht11_Sensor[2] = 0;
		dht11_Sensor[3] = 0;

		UpdateDeviceName();

		((Switch) findViewById(R.id.led1_switch))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Log.i(TAG, "led1_switch isChecked = " + isChecked);
				if (isChecked) {
					ledx_value[0] = 0x11;
				} else {
					ledx_value[0] = 0x10;
				}
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char1,
						ledx_value);				
			}
		});

		((Switch) findViewById(R.id.led2_switch))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Log.i(TAG, "led2_switch isChecked = " + isChecked);
				if (isChecked) {
					ledx_value[0] = 0x21;
				} else {
					ledx_value[0] = 0x20;
				}
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char1,
						ledx_value);
			}
		});
		
		((Switch) findViewById(R.id.led3_switch))
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				Log.i(TAG, "led3_switch isChecked = " + isChecked);
				if (isChecked) {
					ledx_value[0] = 0x41;
				} else {
					ledx_value[0] = 0x40;
				}
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char1,
						ledx_value);
			}
		});
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_button_SetDeviceName: {
			TextView start_edit_SetDeviceName = (TextView) this
					.findViewById(R.id.start_edit_SetDeviceName);
			if (start_edit_SetDeviceName.length() > 0) {
				String str = start_edit_SetDeviceName.getText().toString();
				DeviceScanActivity.WriteCharX(
						DeviceScanActivity.gattCharacteristic_char7,
						str.getBytes());
			} else {
				Toast.makeText(this, "请输入设备名称 ：BLE4.0-Device",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		case R.id.start_button_Read_ADC0ADC1: {
			DeviceScanActivity
					.ReadCharX(DeviceScanActivity.gattCharacteristic_char9);
			break;
		}
		case R.id.start_button_SetPwm: {
			TextView start_edit_SetPWM = (TextView) this
					.findViewById(R.id.start_txt_SetPWM);
			if (start_edit_SetPWM.length() > 0
					&& start_edit_SetPWM.length() <= 8) {
				String pwm = start_edit_SetPWM.getText().toString();
				if (Utils.isHexChar(pwm) == true) {
					byte[] PwmValue = new byte[4];
					PwmValue = Utils.hexStringToBytes(pwm);
					DeviceScanActivity.WriteCharX(
							DeviceScanActivity.gattCharacteristic_charA,
							PwmValue);
				} else {
					Toast.makeText(this, "请输入4位十六进制数据如 ：102030F5",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(this, "请输入4位十六进制数据如 ：102030F5",
						Toast.LENGTH_SHORT).show();
			}
			break;
		}
		}
	}

	public static synchronized void onCharacteristicRead(BluetoothGatt gatt,
			BluetoothGattCharacteristic characteristic) {
		// Log.i(TAG, "onCharacteristicRead str = " + str);

		if (DeviceScanActivity.gattCharacteristic_keydata.equals(characteristic)) {// 按键
			byte[] key_value = new byte[1];
			key_value = characteristic.getValue();
			Log.i(TAG, "key_value[0] = " + key_value[0]);			
			keyValue_save = key_value[0];			
		} else if (DeviceScanActivity.gattCharacteristic_char5.equals(characteristic)) {

		} else if (DeviceScanActivity.gattCharacteristic_char6
				.equals(characteristic)) {
			// Log.i(TAG, "onCharacteristicRead str = " + str);
			int i = characteristic.getValue().length;

			dht11_Sensor = characteristic.getValue();
			Log.i(TAG, "dht11_Sensor[2] = " + dht11_Sensor[2]);
		} else if (DeviceScanActivity.gattCharacteristic_char7
				.equals(characteristic)) {
			int i = characteristic.getValue().length;
			DeviceName = Utils.bytesToString(characteristic.getValue());
			Log.i(TAG, "DeviceName = " + DeviceName);
		} else if (DeviceScanActivity.gattCharacteristic_char9
				.equals(characteristic)) {// adc0 adc1 数据
			byte[] adc0_adc1_value = new byte[4];
			adc0_adc1_value = characteristic.getValue();
			adc0_value[0] = adc0_adc1_value[0];
			adc0_value[1] = adc0_adc1_value[1];
			adc1_value[0] = adc0_adc1_value[2];
			adc1_value[1] = adc0_adc1_value[3];
		} else {
			return;
		}

		mHandler.post(new Runnable() {
			@Override
			public synchronized void run() {
				// 显示设备名称
				start_edit_SetDeviceName.setText(DeviceName);

				// 显示当前温湿度
				String current_temperature = "当前温度：" + dht11_Sensor[2] + "."
						+ dht11_Sensor[3] + "℃";
				String current_humitidy = "当前湿度：" + dht11_Sensor[0] + "."
						+ dht11_Sensor[1] + "%";
				start_txt_temperature.setText(current_temperature + "\r\n"
						+ current_humitidy);

				// 显示当前adc0 adc1的值
				String current_adc0 = "当前ADC0：0x"
						+ Utils.bytesToHexString(adc0_value);
				String current_adc1 = "当前ADC1：0x"
						+ Utils.bytesToHexString(adc1_value);
				start_txt_ADC0ADC1
						.setText(current_adc0 + "\r\n" + current_adc1);
				
			    // 显示按键状态
				switch(keyValue_save)
				{
				case BLE_KEY_UP:
					board_info_log.setText("按键信息: BLE_KEY_UP");
					break;
				case BLE_KEY_DOWN:
					board_info_log.setText("按键信息: BLE_KEY_DOWN");
					break;
				case BLE_KEY_LEFT:
					board_info_log.setText("按键信息: BLE_KEY_LEFT");
					break;
				case BLE_KEY_RIGHT:
					board_info_log.setText("按键信息: BLE_KEY_RIGHT");
					break;
				case BLE_KEY_CENTER:
					board_info_log.setText("按键信息: BLE_KEY_CENTER");
					break;
				case BLE_KEY_S1:
					board_info_log.setText("按键信息: BLE_KEY_S1");
					break;
				case BLE_KEY_RELEASE:
					board_info_log.setText("按键信息: BLE_KEY_RELEASE");
					break;
				}
								
				
			}
		});
	}

	@SuppressWarnings("unused")
	private void UpdateDeviceName() {
		DeviceScanActivity
				.ReadCharX(DeviceScanActivity.gattCharacteristic_char7);
	}

	private void SetTemperatureNotifyUpdate(boolean enable) {
		DeviceScanActivity.setCharacteristicNotification(
				DeviceScanActivity.gattCharacteristic_char6, enable);
	}

	// @Override
	// protected void onResume() {
	// Log.i(TAG, "---> onResume");
	// super.onResume();
	// }
	//
	// @Override
	// protected void onPause() {
	// Log.i(TAG, "---> onPause");
	// super.onPause();
	// }
	//
	@Override
	protected void onStop() {
		Log.i(TAG, "---> onStop");
		SetTemperatureNotifyUpdate(false);

		super.onStop();
	}
	//
	//
	// @Override
	// protected void onDestroy() {
	// Log.i(TAG, "---> onDestroy");
	// super.onDestroy();
	// SetTemperatureNotifyUpdate(false);
	// }
	//
}
