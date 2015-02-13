package com.gamfig.monitorabrasil.activitys;

import android.app.Activity;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.classes.Imagens;
import com.gamfig.monitorabrasil.classes.Usuario;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.people.Person;

public class LoginActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {
	private ProgressBar pb;
	private PlusClient mPlusClient;
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		pb = (ProgressBar) findViewById(R.id.progressBar1);

		Usuario user = new UserDAO(getApplicationContext()).getUserCompleto();
		if (user != null) {
			fillForm(user);
		}

//		mPlusClient = new PlusClient.Builder(this, this, this)
//		.setActions("http://schemas.google.com/AddActivity")
//		.setScopes(Scopes.PLUS_LOGIN, Scopes.PROFILE).build();
	}

	private void fillForm(Usuario user) {
		Bitmap foto = new UserDAO(getApplicationContext()).buscaFotoCache(0);
		ImageView img = (ImageView) findViewById(R.id.imageView1);
		img.setImageBitmap(foto);

		// nome
		TextView txtNome = (TextView) findViewById(R.id.txtNome);
		txtNome.setText(user.getNome());

		// email
		TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
		txtEmail.setText(user.getEmail());

		// uf
		Spinner uf = (Spinner) findViewById(R.id.uf);
		String[] ufs = getResources().getStringArray(R.array.ufs);
		int i = 0;
		for (String ufItem : ufs) {
			if (ufItem.equals(user.getUf())) {
				uf.setSelection(i);
			}
			i++;
		}

		// faixa etaria
		Spinner faixaEtaria = (Spinner) findViewById(R.id.faixaEtaria);
		String[] faixasEtaria = getResources().getStringArray(R.array.faixa);
		i = 0;
		for (String faixaItem : faixasEtaria) {
			if (faixaItem.equals(user.getFaixaEtaria())) {
				faixaEtaria.setSelection(i);
			}
			i++;
		}

		// sexo
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		if (user.getSexo() != null) {
			if (user.getSexo().equals("f")) {
				rg.check(R.id.radioFem);
			} else {
				if (user.getSexo().equals("m")) {
					rg.check(R.id.radioMasc);
				} else {
					rg.check(R.id.radioOutro);
				}
			}
		}

		// btnGoogle
        Button btnLogin = (Button) findViewById(R.id.btnLoginGoogle);
        btnLogin.setVisibility(View.GONE);
		if (user.getIdGoogle() != null) {
			btnLogin.setVisibility(View.GONE);
			btnLogin.setText("Logado");
			btnLogin.setEnabled(false);
		}

		// receber notificacao
		Switch sw = (Switch) findViewById(R.id.switch1);
		if (user.getReceberNotificacao() == null) {
			user.setReceberNotificacao("true");
		}
		sw.setChecked(Boolean.valueOf(user.getReceberNotificacao()));

	}

	public void actionSalvar(View view) {
		salvarUser();

	}

	public void salvarUser() {

		Usuario user = new Usuario();
		user.setId(new UserDAO(getApplicationContext()).getIdUser());
		// nome
		TextView txtNome = (TextView) findViewById(R.id.txtNome);
		user.setNome(txtNome.getText().toString());
		// email
		TextView txtEmail = (TextView) findViewById(R.id.txtEmail);
		user.setEmail(txtEmail.getText().toString());
		// uf
		Spinner uf = (Spinner) findViewById(R.id.uf);
		String ufSelecionada = (String) uf.getItemAtPosition(uf.getSelectedItemPosition());
		user.setUf(ufSelecionada);

		// faixaetaria
		Spinner faixaEtaria = (Spinner) findViewById(R.id.faixaEtaria);
		String faixaSelecionada = (String) faixaEtaria.getItemAtPosition(faixaEtaria.getSelectedItemPosition());
		user.setFaixaEtaria(faixaSelecionada);

		// sexo
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		int idSelecionado = rg.getCheckedRadioButtonId();
		switch (idSelecionado) {
		case R.id.radioFem:
			user.setSexo("f");
			break;
		case R.id.radioMasc:
			user.setSexo("m");
			break;
		case R.id.radioOutro:
			user.setSexo("o");
			break;

		default:
			break;
		}
		// receber notificacao
		Switch sw = (Switch) findViewById(R.id.switch1);
		user.setReceberNotificacao(String.valueOf(sw.isChecked()));

		new SalvaUser(user).execute();
		Toast.makeText(LoginActivity.this, "Informações salvas com sucesso!", Toast.LENGTH_SHORT).show();
	}

	// private String transformaData(String data) {
	// String[] d = data.split("/");
	// if (d.length == 3) {
	// data = d[2] + "-" + d[1] + "-" + d[0];
	// }
	// return data;
	// }

	public void loginGoogle(View view) {
		Toast.makeText(LoginActivity.this, "Aguarde", Toast.LENGTH_LONG).show();
		mPlusClient.connect();
		// new connectGoogle(this).execute();
		pb.setVisibility(View.VISIBLE);

	}
	
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
			}
		}
		// Salvar o resultado e solucionar a falha de conexao mediante clique do usuario.
//		ConnectionResult mConnectionResult = result;

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Usuario user = new Usuario();
		// email
		String accountName = mPlusClient.getAccountName();
		user.setEmail(accountName);

		try {

			Person person = mPlusClient.getCurrentPerson();
			// id
			String id = person.getId();
			user.setIdGoogle(id);

			user.setNome(person.getName().getGivenName() + " " + person.getName().getFamilyName());
			// get imagem
			String urlImagem = person.getImage().getUrl();
			try {
				Bitmap bitmap = Imagens.getCroppedBitmap(Imagens.getImageBitmap(urlImagem));
				ImageView foto = (ImageView) findViewById(R.id.imageView1);
				foto.setImageBitmap(bitmap);
				// para salvar a foto
				UserDAO userdao = new UserDAO(getApplicationContext());
				userdao.salvaFotoDeputado(0, bitmap);
				user.setUrlFoto(urlImagem);
			} catch (Exception e) {
				// TODO: handle exception
			}

			// sexo
			int sexo = person.getGender();

			switch (sexo) {
			case 0:
				user.setSexo("m");
				break;
			case 1:
				user.setSexo("f");
				break;
			case 2:
				user.setSexo("o");
				break;

			default:
				break;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		fillForm(user);
		// salvar usuario
		// Button btnLogin = (Button) findViewById(R.id.btnLoginGoogle);
		// btnLogin.setText("Logado");
		// btnLogin.setEnabled(false);

		new UserDAO(getApplicationContext()).atualizaUsuario(user);
		Toast.makeText(getApplicationContext(), "Informações salvas com sucesso!", Toast.LENGTH_SHORT).show();

		pb.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();

	}

	public class SalvaUser extends AsyncTask<Void, Void, Void> {
		Usuario user;

		public SalvaUser(Usuario user) {
			this.user = user;
		}

		@Override
		protected Void doInBackground(Void... params) {
			new UserDAO(getApplicationContext()).atualizaUsuario(user);

			return null;
		}

		protected void onPostExecute() {

		}
	}

	/**
	 * Conecta a conta do facebook
	 * 
	 * @author 89741803168
	 * 
	 */
	public class connectFacebook extends AsyncTask<Void, Void, Void> {

		public connectFacebook() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		protected void onPostExecute() {
			try {
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	/**
	 * Conecta a conta do twitter
	 * 
	 * @author 89741803168
	 * 
	 */
	public class connectTwitter extends AsyncTask<Void, Void, Void> {

		public connectTwitter() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			return null;
		}

		protected void onPostExecute() {
			try {
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	
}
