package com.example.ejemplomindwavemobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.*;

@SuppressLint("ValidFragment")
public class MainActivity extends Activity {
	
	BluetoothAdapter bluetoothAdapter;
	com.neurosky.thinkgear.TGDevice tgDevice;
	boolean activarRaw = false;
	int pestaneos=0;
	Activity activity;
	
	//Variables de interfaz de usuario
	Button btnConectar;
	TextView txtEstado;
	TextView txtConcentracion;
	TextView txtMeditacion;
	TextView txtPestaneos;
	TextView txtRawdata;
	CheckBox checkRaw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		//Se obtiene un adaptador bluetooth
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		//Se compruba si el bluetooth esta disponible
	    if(bluetoothAdapter == null) {
	    	//Indicar al usuario que el bluetooth no esta disponible
	    	Toast.makeText(activity, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
	    	activity.finish(); //Terminar el programa
	    }else {
	    	//Crear un nuevo dispositivo con el adaptador bluetooth y el manejador
	    	tgDevice = new TGDevice(bluetoothAdapter, handler);
	    }
		
	    //Se obtiene la referencia a los objetos de la interfaz grafica
	    btnConectar = (Button)findViewById(R.id.btnConectar);
		txtEstado = (TextView)findViewById(R.id.txtEstado);
		txtConcentracion = (TextView)findViewById(R.id.txtConcentracion);
		txtMeditacion = (TextView)findViewById(R.id.txtMeditacion);
		txtPestaneos = (TextView)findViewById(R.id.txtPestaneos);
		txtRawdata = (TextView)findViewById(R.id.txtRawdata);
		checkRaw = (CheckBox)findViewById(R.id.checkRaw);
		
		//Acciones
		btnConectar.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        conectar();
		    }
		});
		checkRaw.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				activarRaw=checked;
				tgDevice.stop();
				tgDevice.close();
				tgDevice.connect(activarRaw);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

    public void conectar() {
    	if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
    		tgDevice.connect(activarRaw); 
    }
    
	private final Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	switch (msg.what) {
	            case TGDevice.MSG_STATE_CHANGE:
	                switch (msg.arg1) {
		                case TGDevice.STATE_IDLE:
		                	txtEstado.setText("Dispositivo en reposo");
		                    break;
		                case TGDevice.STATE_CONNECTING:
		                	txtEstado.setText("Conectando...");
		                	break;		                    
		                case TGDevice.STATE_CONNECTED:
		                	tgDevice.start();
		                	txtEstado.setText("Conectado");
		                    break;
		                case TGDevice.STATE_NOT_FOUND:
		                	txtEstado.setText("Dispositivo no encontrado");
		                	break;
		                case TGDevice.STATE_NOT_PAIRED:
		                	txtEstado.setText("Dispositivo no vinculado");
		                	break;
		                case TGDevice.STATE_DISCONNECTED:
		                	txtEstado.setText("Desconectado");
	                }
	
	                break;
	            case TGDevice.MSG_POOR_SIGNAL:
	                break;
	            case TGDevice.MSG_RAW_DATA:
	            	txtRawdata.setText(String.valueOf(msg.arg1)+"\n");
	            	break;
	            case TGDevice.MSG_HEART_RATE:
	                break;
	            case TGDevice.MSG_ATTENTION:
	            	txtConcentracion.setText(String.valueOf(msg.arg1));
	            	break;
	            case TGDevice.MSG_MEDITATION:
	            	txtMeditacion.setText(String.valueOf(String.valueOf(msg.arg1)));
	            	break;
	            case TGDevice.MSG_BLINK:
	            	pestaneos++;
	            	txtPestaneos.setText(String.valueOf(pestaneos));
	            	break;
	            case TGDevice.MSG_RAW_COUNT:
	            	break;
	            case TGDevice.MSG_LOW_BATTERY:
	            	Toast.makeText(activity, "¡Bateria baja!", Toast.LENGTH_SHORT).show();
	            	break;
	            case TGDevice.MSG_RAW_MULTI:
	            	TGRawMulti rawM = (TGRawMulti)msg.obj;
	            default:
	            	break;
	    	}
	    }
	};
}
