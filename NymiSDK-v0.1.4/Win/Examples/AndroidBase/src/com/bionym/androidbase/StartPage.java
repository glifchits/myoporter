package com.bionym.androidbase;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bionym.ncl.Ncl;
import com.bionym.ncl.NclBool;
import com.bionym.ncl.NclCallback;
import com.bionym.ncl.NclEvent;
import com.bionym.ncl.NclEventType;
import com.bionym.ncl.NclMode;
import com.bionym.ncl.NclProvision;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class StartPage extends Activity {

	Button btnInit;
	Button btnDiscover, btnAgree, btnProvision, btnFind, btnSave, btnRevoke, btnValidate, btnDisconnect;
	Button btnCrkSig, btnGetSK, btnPrg;
	Button btnRssi;
	Button btnStopScan;

	public static int nymiHandle = 0;
	public static ArrayList<NclProvision> provisions = new ArrayList<NclProvision>();
	public static char[] vkid;
	public static char[] vk;
	public static char[] sig;
	public static char[] message = "TESTING".toCharArray();

	public static char[] crkVkId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_start_page);
		final String path = Environment.getExternalStorageDirectory() + "";
		// IMPORTANT ****
		// If you are running on he emulator, ip should be "10.0.2.2"
		// If you are running on a device, change it to the IP of the address
		//String ip = "10.0.2.2";
		String ip ="10.0.1.92";

		loadProvisions();

		btnDiscover = (Button) findViewById(R.id.btnDiscover);
		btnAgree = (Button) findViewById(R.id.btnAgree);
		btnProvision = (Button) findViewById(R.id.btnProvision);
		btnFind = (Button) findViewById(R.id.btnFind);
		btnValidate = (Button) findViewById(R.id.btnValidate);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnRevoke = (Button) findViewById(R.id.btnRevoke);
		btnRssi = (Button) findViewById(R.id.btnRssi);
		btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
		btnStopScan = (Button) findViewById(R.id.btnStopScan);

		btnCrkSig = (Button) findViewById(R.id.btnCrkSig);
		btnGetSK = (Button) findViewById(R.id.btnGetSK);
		btnPrg = (Button) findViewById(R.id.btnPrg);

		((TextView) findViewById(R.id.txtViewLog)).setMovementMethod(new ScrollingMovementMethod());

		//Ncl.InitiateLibrary(this, ip, 9089);
		Ncl.InitiateLibrary(this);

		final NclCallback cb = new NclCallback(this, "HandleCallBack", NclEventType.NCL_EVENT_ANY);
		new Thread(new Runnable() {

			@Override
			public void run() {
				//Boolean b = Ncl.init(cb, null, "LOCK", NclMode.NCL_MODE_DEV, path + "/SomeOtherFile.txt");
				Boolean b = Ncl.init(cb, null, "LOCK", NclMode.NCL_MODE_DEFAULT, path + "/SomeOtherFile.txt");
			}
		}).start();

		int i;
		// i = b ? 1 : 0;

		btnDiscover.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Boolean b = Ncl.startDiscovery();
				return;
			}
		});

		btnAgree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Ncl.agree(nymiHandle)) {
					writeToAppLog("nclAgree Failed\n");
				}
			}
		});

		btnProvision.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.provision(nymiHandle);
			}
		});

		btnFind.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.startFinding(provisions, provisions.size(), NclBool.NCL_FALSE);

			}
		});

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Save();

			}
		});

		btnRevoke.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				provisions.clear();
			}
		});

		btnValidate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.validate(nymiHandle);
			}
		});

		btnRssi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.getRssi(nymiHandle);
			}
		});

		btnDisconnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.disconnect(nymiHandle);
			}
		});

		btnCrkSig.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.createSk(nymiHandle);

			}
		});

		btnGetSK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.getSk(nymiHandle, crkVkId);

			}
		});

		btnPrg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Ncl.prg(nymiHandle);

			}
		});

		btnStopScan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				writeToAppLog("Stop Scan Clicked\n");
				Ncl.stopScan();

			}
		});

	}

	public void Save() {
		SharedPreferences prefs = this.getSharedPreferences("SK2", Context.MODE_PRIVATE);
		// writeToOnAppLog("Save Clicked\n", 1);
		JSONObject jObj = new JSONObject();
		JSONArray jArray = new JSONArray();

		JSONObject jPro = null;
		JSONArray jKey = null;
		JSONArray jId = null;

		for (NclProvision p : provisions) {
			// nclCalls.nclFind(p.key, p.id);

			jPro = new JSONObject();
			jKey = new JSONArray();
			jId = new JSONArray();

			for (int a = 0; a < NclProvision.NCL_PROVISION_KEY_SIZE; a++) {
				jKey.put(p.key[a]);
				jId.put(p.id[a]);
			}
			try {
				jPro.putOpt("key", jKey);
				jPro.putOpt("id", jId);
				jArray.put(jPro);
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			jObj.put("provisions", jArray);
			Log.d("Java Code", jObj.toString());
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("provisions", jObj.toString());
		editor.commit();
	}

	public void loadProvisions() {
		final SharedPreferences prefs = this.getSharedPreferences("SK2", Context.MODE_PRIVATE);

		String str = prefs.getString("provisions", "NULL");

		if (!str.equals("NULL")) {
			getSavedProvisions(str);
		}
	}

	private void getSavedProvisions(String str) {
		JSONObject jObj = null;
		JSONArray jArray = null;

		JSONObject jPro = null;
		JSONArray jKey = new JSONArray();
		JSONArray jId = new JSONArray();
		try {
			jObj = new JSONObject(str);
			jArray = jObj.getJSONArray("provisions");
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int len = jArray.length();

		for (int i = 0; i < len; i++) {
			try {
				jPro = jArray.getJSONObject(i);
				jKey = jPro.getJSONArray("key");
				jId = jPro.getJSONArray("id");
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			NclProvision p = new NclProvision();
			for (int a = 0; a < NclProvision.NCL_PROVISION_KEY_SIZE; a++) {
				try {
					p.key[a] = (char) jKey.getInt(a);
					p.id[a] = (char) jId.getInt(a);
				}
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			provisions.add(p);

		}

	}

	public void writeToAppLog(final String txt) {
		// txtViewLog.append(txt);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				TextView txtView = ((TextView) findViewById(R.id.txtViewLog));
				txtView.append(txt);
				int height = txtView.getHeight();
				int lines = txtView.getLineCount();
				int lineHeight = txtView.getLineHeight();
				if (height < lines * lineHeight) {
					txtView.scrollTo(0, (lines - 1) * lineHeight - height);
				}

			}
		});

	}

	int agreement_pattern = 0;

	public void showPattern(final int[][] leds) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LinearLayout llPattern = (LinearLayout) findViewById(R.id.llPattern);
				RadioButton rb;
				for (int i = 0; i < llPattern.getChildCount(); i++) {
					rb = (RadioButton) llPattern.getChildAt(i);
					rb.setChecked(false);
					rb.setChecked(leds[agreement_pattern][i] > 0);
				}
			}
		});

	}

	public void clearPattern() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LinearLayout llPattern = (LinearLayout) findViewById(R.id.llPattern);
				RadioButton rb;
				for (int i = 0; i < llPattern.getChildCount(); i++) {
					rb = (RadioButton) llPattern.getChildAt(i);
					rb.setChecked(false);
				}

			}
		});

	}

	public void HandleCallBack(NclEvent event, Object userData) {

		Log.d("HandleCallBack", "NclEvent: " + event.type.toString());

		switch (event.type) {
			case NCL_EVENT_INIT: {
				writeToAppLog("NCL_EVENT_INIT Returned " + NclBool.values()[event.init.success] + "\n");
				break;
			}

			case NCL_EVENT_DISCOVERY: {
				writeToAppLog("NCL_EVENT_DISCOVERY	Rssi: " + event.discovery.rssi + "    TxPowerLevel: " + event.discovery.txPowerLevel + "\n");
				if (userData instanceof Button) {
					Button b = (Button) userData;
					writeToAppLog("UserData is a Button with text : " + b.getText() + "\n");
				}
				nymiHandle = event.discovery.nymiHandle;

				break;
			}

			case NCL_EVENT_FIND: {
				writeToAppLog("NCL_EVENT_FIND Rssi: " + event.find.rssi + "    TxPowerLevel: " + event.find.txPowerLevel + "\n");
				nymiHandle = event.find.nymiHandle;
				break;
			}

			case NCL_EVENT_DETECTION: {
				break;
			}

			case NCL_EVENT_AGREEMENT: {
				writeToAppLog("NCL_EVENT_AGREEMENT\n");
				nymiHandle = event.agreement.nymiHandle;
				showPattern(event.agreement.leds);
				break;
			}

			case NCL_EVENT_PROVISION: {
				writeToAppLog("NCL_EVENT_PROVISION\n");
				clearPattern();
				StartPage.provisions.add(event.provision.provision);
				break;
			}

			case NCL_EVENT_VALIDATION: {
				writeToAppLog("NCL_EVENT_VALIDATION\n");
				nymiHandle = event.validation.nymiHandle;
				break;
			}

			case NCL_EVENT_CREATED_SK: {
				writeToAppLog("NCL_EVENT_CREATED_SK\n");
				nymiHandle = event.createdSk.nymiHandle;
				writeToAppLog("SK: " + SomeWhatReadable(event.createdSk.sk) + "\n");
				writeToAppLog("ID: " + SomeWhatReadable(event.createdSk.id) + "\n");
				crkVkId = event.createdSk.id.clone();
				break;
			}
			case NCL_EVENT_GOT_SK: {
				writeToAppLog("NCL_EVENT_GOT_SK\n");
				nymiHandle = event.gotSk.nymiHandle;
				writeToAppLog("SK: " + SomeWhatReadable(event.gotSk.sk) + "\n");
				break;
			}
			case NCL_EVENT_PRG: {
				writeToAppLog("NCL_EVENT_PRG\n");
				nymiHandle = event.prg.nymiHandle;
				writeToAppLog("Value: " + SomeWhatReadable(event.prg.value) + "\n");
				break;
			}

			case NCL_EVENT_RSSI: {
				writeToAppLog("NCL_EVENT_RSSI		Rssi:	" + event.rssi.rssi + "\n");
				nymiHandle = event.rssi.nymiHandle;
				break;
			}

			case NCL_EVENT_DISCONNECTION: {
				writeToAppLog("NCL_EVENT_DISCONNECTION\n");
				break;
			}

		}
	}

	private String SomeWhatReadable(char[] arr) {
		String str = "";
		for (char c : arr) {
			str += ((int) c) + " ";
		}
		return str;
	}

}
