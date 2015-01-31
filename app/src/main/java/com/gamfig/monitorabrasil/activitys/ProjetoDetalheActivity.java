package com.gamfig.monitorabrasil.activitys;

import java.text.DecimalFormat;
import java.util.HashMap;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.classes.Projeto;
import com.gamfig.monitorabrasil.dialog.DialogComentario;
import com.gamfig.monitorabrasil.fragments.listviews.ComentariosFragment;
import com.google.gson.Gson;

public class ProjetoDetalheActivity extends Activity {

	ProgressDialog dialog;
	boolean isChecked;
	Activity mActivity;
	private float notaProjeto;
	private String idUser;
	private String casa;
	private Projeto projeto;
	private ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_projeto_detalhe);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		pb.setVisibility(View.VISIBLE);
		mActivity = this;
		Bundle receiveBundle = this.getIntent().getExtras();
		Gson gson = new Gson();
		projeto = gson.fromJson(receiveBundle.getString("projeto"), Projeto.class);
		//verifica se somente id foi passado
		if(projeto.getEmenta()== null){
			//busca o projeto que foi salvo. Isso pq foi recebido um push para abrir o projeto
			projeto = new UserDAO(getApplicationContext()).buscaProjetoSalvo(projeto);
		}
		casa = projeto.getTipo();
		if(casa == null)
			casa = receiveBundle.getString("casa");
		new BuscaProjeto().execute();

		montaFormIncial();
		montaBotoes();

		Switch swtAcompanhamento = (Switch) findViewById(R.id.switch1);
		if (new UserDAO(getApplicationContext()).estaMonitorado(projeto.getId())) {
			swtAcompanhamento.setChecked(true);
		}
		swtAcompanhamento.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String acao;
				if (isChecked) {
					// adiciona monitoramento
					acao = "add";
				} else {
					// remove monitoramento
					acao = "del";
				}
				int idUser = new UserDAO(getApplicationContext()).getIdUser();
				new UserDAO(getApplicationContext()).salvaProjetoMonitorado(projeto, isChecked);
				new DeputadoDAO().marcaProjetoFavorito(idUser, projeto.getId(), acao);

			}
		});

		// buscar os comentarios
		ComentariosFragment mComentariosFrag = new ComentariosFragment();
		receiveBundle.putInt("idProposta", projeto.getId());
		mComentariosFrag.setArguments(receiveBundle);

		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.frameComentarios, mComentariosFrag);
		fragmentTransaction.commit();

	}

	private void montaBotoes() {
		final Button btnNao = (Button) findViewById(R.id.btnNao);
		final Button btnSim = (Button) findViewById(R.id.btnSim);

		String voto = buscaVoto(projeto.getId());
		if (voto != null || projeto.getVotoUser() != null) {
			voto = (voto == null) ? projeto.getVotoUser() : voto;
			if (voto.equals("s")) {
				btnSim.setText("Votou Sim");
				btnNao.setText("Não");
			} else {
				btnNao.setText("Votou Não");
				btnSim.setText("Sim");
			}

		}

		// listener voto Sim
		btnSim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnSim.getText().equals("Sim")) {
					btnSim.setText("Votou Sim");
					projeto.setS(projeto.getS() + 1);
					// verificar se ja votou
					if (!btnNao.getText().equals("Não")) {
						btnNao.setText("Não");
						// Diminui do voto sim
						projeto.setN(projeto.getN() - 1);
					}
					votar("s");
					montaVotos();
				}

			}
		});

		// listener voto Nao
		btnNao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnNao.getText().equals("Não")) {
					btnNao.setText("Votou Não");
					projeto.setN(projeto.getN() + 1);
					// verificar se ja votou
					if (!btnSim.getText().equals("Sim")) {
						btnSim.setText("Sim");
						// Diminui do voto sim
						projeto.setS(projeto.getS() - 1);
					}
					votar("n");
					montaVotos();
				}

			}
		});

	}

	private String buscaVoto(int id) {
		HashMap<String, String> votos = new UserDAO(this).buscaVotos();
		if (votos == null)
			return null;
		if (votos.containsKey(String.valueOf(id))) {
			return votos.get(String.valueOf(id));
		}

		return null;
	}

	private void votar(String voto) {
		UserDAO userdao = new UserDAO(this);
		int idUser = userdao.getIdUser();

		// salvar no banco

		new insereVoto(voto, projeto.getId(), idUser).execute();

		// salvar no SharedPreferences
		userdao.salvaVoto(voto, String.valueOf(projeto.getId()));

	}

	private void montaFormIncial() {
		TextView tx = (TextView) findViewById(R.id.txtNomeProjeto);
		tx.setText(projeto.getNome());

		tx = (TextView) findViewById(R.id.txtTipoProp);
		tx.setText(projeto.getTipoProposicao());

		tx = (TextView) findViewById(R.id.txtAutor);

		tx.setText((projeto.getNomeAutor() == null ? projeto.getAutor().getNomeParlamentar() : projeto.getNomeAutor()));

		tx = (TextView) findViewById(R.id.txtEmenta);
		tx.setText(projeto.getEmenta());

		// votos
		montaVotos();

	}

	public void montaVotos() {
		DecimalFormat df = new DecimalFormat("#,###,##0");
		int total = projeto.getN() + projeto.getS();
		String votos = df.format(total) + " votos, " + df.format(projeto.getS()) + " sim, " + df.format(projeto.getN()) + " não.";
		TextView tx = (TextView) findViewById(R.id.txtVotos);
		tx.setText(votos);
	}

	public class avaliaProjeto extends AsyncTask<Void, Void, String> {

		public avaliaProjeto() {
		}

		@Override
		protected String doInBackground(Void... params) {

			new DeputadoDAO().avaliaProjeto(idUser, projeto.getId(), notaProjeto);

			return null;
		}

		protected void onPostExecute(String results) {

			;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.ic_share:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);


			String mensagem = projeto.getNome() + "\n " + "http://www.monitorabrasil.com/pp.php?id=" + projeto.getId();
			mensagem = mensagem + " \n " + projeto.getEmenta() + "\n  #monitoraBrasil";
			sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem);
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			break;
		case R.id.ic_comment:
			DialogFragment dialog = new DialogComentario(new UserDAO(getApplicationContext()).getIdUser(), projeto.getId(), projeto.getNome());
			dialog.show(getFragmentManager(), "Cometario");

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ficha_politico, menu);

		return true;
	}

	public class BuscaProjeto extends AsyncTask<Void, Void, Projeto> {

		public BuscaProjeto() {
		}

		@Override
		protected Projeto doInBackground(Void... params) {
			return new DeputadoDAO().buscaProjeto(projeto.getId(), casa);

		}

		protected void onPostExecute(Projeto results) {
			try {
				if (results != null) {
					projeto.setLink(results.getLink());
					projeto.setFormaApreciacao(results.getFormaApreciacao());
					projeto.setRegime(results.getRegime());
					projeto.setUltimoDespacho(results.getUltimoDespacho());
					projeto.setSituacao(results.getSituacao());
					if ((projeto.getS() + projeto.getN()) < (results.getS() + results.getN())) {
						projeto.setS(results.getS());
						projeto.setN(results.getN());
						montaVotos();
					}
				}
				//
				// // montar formulario com os dados recebidos
				//
				// link
				TextView tx = (TextView) findViewById(R.id.txtLinkInteiroTeor);
				if (!casa.equals("s")) {
					tx.setText(projeto.getLink());
				} else {
					tx.setText("http://www.senado.leg.br/atividade/materia/detalhes.asp?p_cod_mate=" + String.valueOf(projeto.getId()));
				}
				// so tem para camara
				if (!casa.equals("s")) {
					// forma
					tx = (TextView) findViewById(R.id.txtForma);
					tx.setText(projeto.getFormaApreciacao());

					// regime
					tx = (TextView) findViewById(R.id.txtRegime);
					tx.setText(projeto.getRegime());
				} else {
					tx = (TextView) findViewById(R.id.TextView11);
					tx.setVisibility(View.GONE);
					tx = (TextView) findViewById(R.id.txtForma);
					tx.setVisibility(View.GONE);
					tx = (TextView) findViewById(R.id.TextView14);
					tx.setVisibility(View.GONE);
					tx = (TextView) findViewById(R.id.txtRegime);
					tx.setVisibility(View.GONE);
				}

				// despacho
				tx = (TextView) findViewById(R.id.txtDespacho);
				tx.setText(projeto.getUltimoDespacho());

				// situaacao
				tx = (TextView) findViewById(R.id.txtSituacao);
				tx.setText(projeto.getSituacao());
				pb.setVisibility(View.INVISIBLE);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	/**
	 * Insere o voto
	 * 
	 */
	public class insereVoto extends AsyncTask<Void, Void, String> {
		String mVoto;
		int mIdProposta;
		int mIdUser;

		public insereVoto(String voto, int i, int idUser) {
			mVoto = voto;
			mIdProposta = i;
			mIdUser = idUser;
		}

		@Override
		protected String doInBackground(Void... params) {

			new DeputadoDAO().insereVoto(mVoto, String.valueOf(mIdProposta), mIdUser);

			return null;
		}

		protected void onPostExecute(String results) {

			Toast.makeText(mActivity, "Voto registrado", Toast.LENGTH_SHORT).show();
			new UserDAO(mActivity).buscaPontuacao();
		}
	}
}
