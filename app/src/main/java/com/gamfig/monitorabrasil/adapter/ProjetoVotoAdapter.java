/*******************************************************************************
 * Copyright  2013 de Geraldo Augusto de Morais Figueiredo
 * Este arquivo � parte do programa Monitora, Brasil!. O Monitora, Brasil! � um software livre.
 * Voc� pode redistribu�-lo e/ou modific�-lo dentro dos termos da GNU Affero General Public License 
 * como publicada pela Funda��o do Software Livre (FSF); na vers�o 3 da Licen�a. 
 * Este programa � distribu�do na esperan�a que possa ser �til, mas SEM NENHUMA GARANTIA,
 * sem uma garantia impl�cita de ADEQUA��O a qualquer MERCADO ou APLICA��O EM PARTICULAR. 
 * Veja a licen�a para maiores detalhes. 
 * Voc� deve ter recebido uma c�pia da GNU Affero General Public License, sob o t�tulo "LICENSE.txt", 
 * junto com este programa, se n�o, acesse http://www.gnu.org/licenses/
 ******************************************************************************/
package com.gamfig.monitorabrasil.adapter;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.activitys.ProjetoDetalheActivity;
import com.gamfig.monitorabrasil.classes.Projeto;
import com.gamfig.monitorabrasil.dialog.DialogComentario;
import com.google.gson.Gson;

public class ProjetoVotoAdapter extends ArrayAdapter<Projeto> {
	Context context;
	int layoutResourceId;
	FragmentManager fragmentManager;
	List<Projeto> data = null;
	String casa = null;

	public ProjetoVotoAdapter(Context context, int layoutResourceId, List<Projeto> projetos, FragmentManager fragmentManager, String casa) {
		super(context, layoutResourceId, projetos);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.data = projetos;
		this.fragmentManager = fragmentManager;
		this.casa = casa;

		// limpar votos
		// SharedPreferences sharedPref = getContext().getSharedPreferences(
		// getContext().getString(R.string.id_key_preferencias),
		// getContext().MODE_PRIVATE);
		// SharedPreferences.Editor editor = sharedPref.edit();
		// editor.putString(getContext().getString(R.string.id_key_votos),
		// null);
		// editor.commit();
	}

	private String buscaVoto(int id) {
		HashMap<String, String> votos = new UserDAO(getContext()).buscaVotos();
		if (votos == null)
			return null;
		if (votos.containsKey(String.valueOf(id))) {
			return votos.get(String.valueOf(id));
		}

		return null;
	}

	public boolean votar(String voto, String idProposta) {

		UserDAO userdao = new UserDAO(getContext());
		int idUser = userdao.getIdUser();

		// salvar no banco

		new insereVoto(voto, idProposta, idUser).execute();

		// salvar no SharedPreferences
		userdao.salvaVoto(voto, idProposta);

		for (Projeto projeto : data) {
			if (projeto.getId() == Integer.parseInt(idProposta)) {
				if (voto.equals("s")) {
					projeto.setS(projeto.getS() + 1);
				} else {
					projeto.setN(projeto.getN() + 1);
				}
			}
		}
		return true;

	}

	public class insereVoto extends AsyncTask<Void, Void, String> {
		String mVoto;
		String mIdProposta;
		int mIdUser;

		public insereVoto(String voto, String idProposta, int idUser) {
			mVoto = voto;
			mIdProposta = idProposta;
			mIdUser = idUser;
		}

		@Override
		protected String doInBackground(Void... params) {

			new DeputadoDAO().insereVoto(mVoto, mIdProposta, mIdUser);

			return null;
		}

		protected void onPostExecute(String results) {

			Toast.makeText(getContext(), "Voto registrado", Toast.LENGTH_SHORT).show();
			new UserDAO(getContext()).buscaPontuacao();
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ProjetoVotoHolder holder = null;

		if (row == null) {
			LayoutInflater infater = ((Activity) context).getLayoutInflater();
			row = infater.inflate(layoutResourceId, parent, false);
			holder = new ProjetoVotoHolder();
			holder.txtPropostaTitulo = (TextView) row.findViewById(R.id.txtPropostaTitulo);

			holder.txtPropostaEmenta = (TextView) row.findViewById(R.id.txtPropostaEmenta);
			holder.txtPropostaAutor = (TextView) row.findViewById(R.id.txtPropostaAutor);
			holder.txtVotoTotal = (TextView) row.findViewById(R.id.txtVotoTotal);
			holder.txtVotoSim = (TextView) row.findViewById(R.id.txtVotoSim);
			holder.txtVotoNao = (TextView) row.findViewById(R.id.txtVotoNao);
			holder.dtProposicao = (TextView) row.findViewById(R.id.dtProposicao);

			holder.btnVotoSim = (Button) row.findViewById(R.id.btnVotoSim);

			holder.btnVotoSim.setTag(holder);
			holder.btnVotoNao = (Button) row.findViewById(R.id.btnVotoNao);
			holder.btnVotoNao.setTag(holder);

			holder.btnComentar = (Button) row.findViewById(R.id.btnComentar);
			holder.btnComentar.setTag(holder);

			holder.btnInfoProjeto = (ImageButton) row.findViewById(R.id.btnInfoProjeto);

			// parte do comentario se houver
			holder.imgUserComentario = (ImageView) row.findViewById(R.id.imgUserComentario);
			holder.txtNomeComentario = (TextView) row.findViewById(R.id.txtNomeComentario);
			holder.txtUserComentario = (TextView) row.findViewById(R.id.txtUserComentario);
			holder.rlComentario = (LinearLayout) row.findViewById(R.id.llComentario);

			row.setTag(holder);

			// click para inserir coment�rio
			holder.btnComentar.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// passar idProposta,idUser,TituloProposta
					Button bt = (Button) v;
					ProjetoVotoHolder p = (ProjetoVotoHolder) bt.getTag();

					// TODO chamar a atividade Comentario

					DialogFragment dialog = new DialogComentario(new UserDAO(getContext()).getIdUser(), p.id, p.txtPropostaTitulo.getText()
							.toString());
					dialog.show(fragmentManager, "Cometario");

				}

			});

			holder.btnVotoSim.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					Button bt = (Button) v;
					if (!bt.getText().equals("Votou Sim")) {
						bt.setText("Votou Sim");
						ProjetoVotoHolder p = (ProjetoVotoHolder) bt.getTag();

						int sim = Integer.valueOf(p.txtVotoSim.getText().toString()) + 1;
						p.txtVotoSim.setText(String.valueOf(sim));

						// verificar se j� votou
						if (p.btnVotoNao.getText().equals("N�o")) {
							int total = Integer.valueOf(p.txtVotoTotal.getText().toString()) + 1;
							p.txtVotoTotal.setText(String.valueOf(total));
						} else {
							p.btnVotoNao.setText("N�o");
							// diminui do voto nao
							int nao = Integer.valueOf(p.txtVotoNao.getText().toString()) - 1;
							p.txtVotoNao.setText(String.valueOf(nao));
						}
						votar("s", String.valueOf(p.id));
					}

				}

			});

			holder.btnVotoNao.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {

					Button bt = (Button) v;
					if (bt.getText().equals("N�o")) {
						bt.setText("Votou N�o");
						ProjetoVotoHolder p = (ProjetoVotoHolder) bt.getTag();
						int nao = Integer.valueOf(p.txtVotoNao.getText().toString()) + 1;
						p.txtVotoNao.setText(String.valueOf(nao));
						// verificar se j� votou
						if (p.btnVotoSim.getText().equals("Sim")) {
							int total = Integer.valueOf(p.txtVotoTotal.getText().toString()) + 1;
							p.txtVotoTotal.setText(String.valueOf(total));
						} else {
							p.btnVotoSim.setText("Sim");
							// Diminui do voto sim
							int sim = Integer.valueOf(p.txtVotoSim.getText().toString()) - 1;
							p.txtVotoSim.setText(String.valueOf(sim));
						}
						votar("n", String.valueOf(p.id));
					}
				}

			});

		} else {
			holder = (ProjetoVotoHolder) row.getTag();
		}
		Projeto projeto = data.get(position);
		if (projeto.getNome().equals("Nenhum Projeto"))
			return null;
		holder.id = projeto.getId();
		holder.txtPropostaEmenta.setText(projeto.getEmenta());
		holder.txtPropostaAutor.setText(projeto.getNomeAutor());
		// Evento para abrir projeto
		OnClickListener onclicklistener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// buscar detalhes do projeto clicado

				Projeto projeto = (Projeto) v.getTag();

				Intent intent = new Intent();
				intent.setClass(getContext(), ProjetoDetalheActivity.class);
				Gson gson = new Gson();
				intent.putExtra("projeto", gson.toJson(projeto));
				getContext().startActivity(intent);
			}
		};
		holder.txtPropostaEmenta.setOnClickListener(onclicklistener);
		holder.txtPropostaEmenta.setTag(projeto);

		holder.btnInfoProjeto.setOnClickListener(onclicklistener);
		holder.btnInfoProjeto.setTag(projeto);

		holder.txtPropostaTitulo.setText(projeto.getNome());
		if (projeto.getDtApresentacao() != null)
			holder.dtProposicao.setText("Dt. Apresenta��o: " + projeto.getDtApresentacao());
		else
			holder.dtProposicao.setText("");
		
		holder.txtVotoTotal.setText(String.valueOf(projeto.getS() + projeto.getN()));
		holder.txtVotoSim.setText(String.valueOf(projeto.getS()));
		holder.txtVotoNao.setText(String.valueOf(projeto.getN()));
		String voto = buscaVoto(projeto.getId());
		holder.btnVotoNao.setText("N�o");
		holder.btnVotoSim.setText("Sim");
		holder.email = projeto.getEmail();
		if (voto != null) {
			if (voto.equals("s")) {
				holder.btnVotoSim.setText("Votou Sim");
				holder.btnVotoNao.setText("N�o");
			}
			if (voto.equals("n")) {
				holder.btnVotoNao.setText("Votou N�o");
				holder.btnVotoSim.setText("Sim");
			}
		} else {
			if (projeto.getVotoUser() != null) {
				if (projeto.getVotoUser().equals("s")) {
					holder.btnVotoSim.setText("Votou Sim");
					holder.btnVotoNao.setText("N�o");
				} else {
					holder.btnVotoNao.setText("Votou N�o");
					holder.btnVotoSim.setText("Sim");
				}
			}
		}
		if (projeto.getComentario() != null) {
			holder.txtUserComentario.setText(projeto.getComentario().getComent());
			// holder.imgUserComentario.setPresetSize(ProfilePictureView.SMALL);
			// holder.fotoFaceComentario.setProfileId(projeto.getComentario().getId());
			holder.txtNomeComentario.setText(projeto.getComentario().getNome());
			holder.rlComentario.setVisibility(View.VISIBLE);
		} else {
			holder.rlComentario.setVisibility(View.GONE);
		}
		holder.rlComentario.setVisibility(View.GONE);

		// evento para abrir votos
		OnClickListener onclicklistenerVotos = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// buscar detalhes do projeto clicado
				Bundle sendBundle = new Bundle();
				Projeto projeto = (Projeto) v.getTag();
				int id = Integer.valueOf(projeto.getId());
				sendBundle.putInt("idProjeto", id);
				sendBundle.putString("nomePec", projeto.getNome());
				// Intent i = new Intent(getContext(), VotosActivity.class);
				// i.putExtras(sendBundle);
				// getContext().startActivity(i);
			}
		};

		// verifica se j� foi votado
		// if (projeto.isVotado()) {
		// holder.txtPropostaVotado.setVisibility(View.VISIBLE);
		// holder.imgVotado.setVisibility(View.VISIBLE);
		// holder.txtPropostaVotado.setOnClickListener(onclicklistenerVotos);
		// holder.txtPropostaVotado.setTag(projeto);
		//
		// holder.imgVotado.setOnClickListener(onclicklistenerVotos);
		// holder.imgVotado.setTag(projeto.getId());
		// } else {
		// holder.txtPropostaVotado.setVisibility(View.INVISIBLE);
		// holder.imgVotado.setVisibility(View.INVISIBLE);
		// }

		// verifica se tem coment�rios

		if (projeto.getQtdComentario() > 0) {
			holder.btnComentar.setText("   " + String.valueOf(projeto.getQtdComentario()));
			holder.btnComentar.setTextSize(15);

		} else {
			holder.btnComentar.setText("Comentar");
		}

		return row;
	}

	/*
	 * public void showShareCommentDialog(Context context, final DialogFlowController controller) {
	 * 
	 * final EditText text = new EditText(context); text.setMinLines(3); text.setGravity(Gravity.TOP);
	 * 
	 * AlertDialog.Builder builder = new AlertDialog.Builder(context); builder.setTitle("Compartilhar");
	 * builder.setMessage("Insira um coment�rio (Opcional)"); builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { controller.onContinue(text.getText().toString()); } });
	 * builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { controller.onCancel(); } });
	 * 
	 * AlertDialog dialog = builder.create(); dialog.setView(text); dialog.show(); }
	 * 
	 * private JSONObject getMetaData(ProjetoVotoHolder ch) { // Store a custom dictionary as a JSON object JSONObject metaData = new JSONObject();
	 * try { String hashTag = ch.txtPropostaTitulo.getText().toString().replace(" ", ""); hashTag = "#" + hashTag.substring(0, hashTag.length() - 5);
	 * 
	 * String texto = "Proposta: " + ch.txtPropostaTitulo.getText() + "<br>Autor: " + ch.txtPropostaAutor.getText() + "<br>Ementa<br>" +
	 * ch.txtPropostaEmenta.getText() + "<br><br>Votos:" + ch.txtPropostaVotos.getText() + " Sim:" + ch.txtPropostaSim.getText() + " N�o:" +
	 * ch.txtPropostaNao.getText() + "<br><br>#MonitoraBrasil";
	 * 
	 * metaData.put("szsd_title", hashTag + " #MonitoraBrasil"); metaData.put("szsd_description", texto); metaData.put("szsd_creator",
	 * "@monitoraBrasil");
	 * 
	 * // Optionally add a thumbnail URL to be rendered on the entity page metaData.put("szsd_thumb", "http://www.monitorabrasil.com/topo.jpg"); }
	 * catch (Exception e) { // TODO: handle exception } return metaData; }
	 */
	static class ProjetoVotoHolder {

		int id;
		TextView txtPropostaTitulo;
		TextView dtProposicao;

		TextView txtPropostaAutor;
		TextView txtPropostaEmenta;
		String email;

		TextView txtVotoSim;
		TextView txtVotoNao;
		TextView txtVotoTotal;

		Button btnVotoSim;
		Button btnVotoNao;
		Button btnComentar;

		ImageView imgUserComentario;

		TextView txtNomeComentario;
		TextView txtUserComentario;

		LinearLayout rlComentario;

		ImageButton btnInfoProjeto;

	}
}
