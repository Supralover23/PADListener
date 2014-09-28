package fr.neraud.padlistener.ui.activity;

import android.os.Bundle;
import android.util.Log;

import fr.neraud.padlistener.R;

/**
 * Activity to compute sync
 *
 * @author Neraud
 */
public class ComputeSyncActivity extends AbstractPADListenerActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(getClass().getName(), "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compute_sync_activity);
	}

}