package com.gamfig.monitorabrasil.dialog;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


import com.gamfig.monitorabrasil.R;

public class DialogGostou extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


		// Use the Builder class for convenient dialog construction
		Builder builder = new Builder(getActivity());
		builder.setTitle(R.string.mostre_seu_amor);

		builder.setItems(R.array.gostou_array, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// The 'which' argument contains the index position
				// of the selected item
				switch (which) {
				

				case 0:// facebook
					abreIntent("https://facebook.com/monitorabrasilapp");
					break;

				case 1:// twitter
					abreIntent("https://twitter.com/monitorabrasil");
					break;

				case 2:// google play
					abreIntent("https://play.google.com/store/apps/details?id=com.gamfig.monitorabrasil");
					break;
				case 3:// google play
					abreIntent("http://monitorabrasil.com");
					break;

				default:
					break;
				}
			}

			private void abreIntent(String url) {
				Uri uri = Uri.parse(url);
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				Intent chooserIntent = null;
				chooserIntent = Intent.createChooser(i, "Escolha qual app para abrir:");
				startActivity(chooserIntent);
			};
		});
		builder.setPositiveButton(R.string.outra_hora, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// FIRE ZE MISSILES!
			}
		});
		// Create the AlertDialog object and return it
		return builder.create();
	}
}