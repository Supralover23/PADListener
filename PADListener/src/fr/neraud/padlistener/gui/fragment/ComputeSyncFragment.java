
package fr.neraud.padlistener.gui.fragment;

import java.text.DateFormat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.neraud.padlistener.R;
import fr.neraud.padlistener.gui.AbstractPADListenerActivity;
import fr.neraud.padlistener.gui.constant.GuiScreen;
import fr.neraud.padlistener.helper.DefaultSharedPreferencesHelper;
import fr.neraud.padlistener.helper.TechnicalSharedPreferencesHelper;
import fr.neraud.padlistener.model.SyncComputeResultModel;
import fr.neraud.padlistener.service.ComputeSyncService;
import fr.neraud.padlistener.service.constant.RestCallError;
import fr.neraud.padlistener.service.constant.RestCallRunningStep;
import fr.neraud.padlistener.service.receiver.AbstractRestResultReceiver;

public class ComputeSyncFragment extends Fragment {

	private Button button;
	private ProgressBar syncProgress;
	private TextView status;

	private class MyComputeSyncReceiver extends AbstractRestResultReceiver<SyncComputeResultModel> {

		public MyComputeSyncReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveProgress(RestCallRunningStep progress) {
			Log.d(getClass().getName(), "onReceiveProgress : " + progress);
			switch (progress) {
			case STARTED:
				status.setText(getString(R.string.compute_sync_status, getString(R.string.compute_sync_status_calling)));
				syncProgress.setIndeterminate(false);
				syncProgress.setProgress(1);
				syncProgress.setMax(4);
				break;
			case RESPONSE_RECEIVED:
				status.setText(getString(R.string.compute_sync_status, getString(R.string.compute_sync_status_parsing)));
				syncProgress.setProgress(2);
				break;
			case RESPONSE_PARSED:
				status.setText(getString(R.string.compute_sync_status, getString(R.string.compute_sync_status_computing)));
				syncProgress.setProgress(3);
				break;
			default:
				break;
			}
		}

		@Override
		protected void onReceiveSuccess(SyncComputeResultModel result) {
			Log.d(getClass().getName(), "onReceiveSuccess");
			status.setText(getString(R.string.compute_sync_status, getString(R.string.compute_sync_status_finished)));
			syncProgress.setProgress(4);

			final Bundle extras = new Bundle();
			extras.putSerializable(ChooseSyncFragment.EXTRA_SYNC_RESULT_NAME, result);
			((AbstractPADListenerActivity) getActivity()).goToScreen(GuiScreen.CHOOSE_SYNC, extras);
		}

		@Override
		protected void onReceiveError(RestCallError error, String errorMessage) {
			Log.d(getClass().getName(), "onReceiveError : " + error);

			syncProgress.setProgress(4);
			status.setText(getString(R.string.compute_sync_status, getString(R.string.compute_sync_status_failed, errorMessage)));
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(getClass().getName(), "onCreateView");

		final View view = inflater.inflate(R.layout.compute_sync_fragment, container, false);

		final DefaultSharedPreferencesHelper defaultPrefHelper = new DefaultSharedPreferencesHelper(getActivity());
		final TechnicalSharedPreferencesHelper techPrefHelper = new TechnicalSharedPreferencesHelper(getActivity());

		final TextView explain = (TextView) view.findViewById(R.id.compute_sync_explain);
		final String refreshDate = DateFormat.getDateTimeInstance().format(techPrefHelper.getLastCaptureDate());
		explain.setText(getString(R.string.compute_sync_explain, defaultPrefHelper.getPadHerderUserName(), refreshDate));
		button = (Button) view.findViewById(R.id.compute_sync_button);
		syncProgress = (ProgressBar) view.findViewById(R.id.compute_sync_progress);
		status = (TextView) view.findViewById(R.id.compute_sync_status);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(getClass().getName(), "onClick");

				final Intent startIntent = new Intent(getActivity(), ComputeSyncService.class);
				startIntent.putExtra(AbstractRestResultReceiver.RECEIVER_EXTRA_NAME, new MyComputeSyncReceiver(new Handler()));
				getActivity().startService(startIntent);

				button.setVisibility(View.GONE);
				syncProgress.setVisibility(View.VISIBLE);
				status.setVisibility(View.VISIBLE);
			}
		});
		syncProgress.setVisibility(View.GONE);
		status.setVisibility(View.GONE);

		return view;
	}
}
