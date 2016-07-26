package com.gamfig.monitorabrasil.activitys;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.gamfig.monitorabrasil.DAO.DataBaseHelper;
import com.gamfig.monitorabrasil.DAO.PoliticoDAO;
import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.UserDAO;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.dialog.DialogComentario;
import com.gamfig.monitorabrasil.fragments.ficha.PoliticoDetalheFragment;

import java.sql.SQLException;

import static android.R.anim.fade_in;
import static android.R.anim.fade_out;

public class FichaActivity extends FragmentActivity {
	private PoliticoDetalheFragment politicoFragment;
	private int idPolitico;
	private String nome;
    private Politico p;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cota_parlamentar);

        // abre a ficha do poltico
        politicoFragment = new PoliticoDetalheFragment();
        Bundle bundle = getIntent().getExtras();
        idPolitico = bundle.getInt("idPolitico");
        p = new Politico(idPolitico);
        try {
            // buscar o politico
            DataBaseHelper dbh = new DataBaseHelper(FichaActivity.this);
            PoliticoDAO politicoDAO = new PoliticoDAO(dbh.getConnectionSource());
            p=politicoDAO.getPolitico(idPolitico);
            if(null!=p){
                nome = p.getNome() + " " + p.getTwitter();
                bundle.putString("twitter",p.getTwitter());
                politicoFragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.container, politicoFragment, "listaPoliticoMonitora");
                fragmentTransaction.commit();
            }else{
                Toast.makeText(FichaActivity.this,"Politico nao encontrado",Toast.LENGTH_SHORT);
            }



        } catch (SQLException e) {
            e.printStackTrace();
        }




	}

	public void abreGrafico(View v) {
		Button btn = (Button) v;
		ViewFlipper mVf = (ViewFlipper) findViewById(R.id.flipper_cotas_ficha);

		mVf.setInAnimation(this, fade_in);
		mVf.setOutAnimation(this, fade_out);

		if (mVf.getDisplayedChild() == 0) {
			mVf.showNext();
			btn.setText("Números");
		} else {
			mVf.showPrevious();
			btn.setText("Gráfico");
		}
	}

	public void abreFonte(View v) {
		new AlertDialog.Builder(this).setTitle(R.string.fonte).setMessage(R.string.msg_fonte_tbrasil).setIcon(R.drawable.ic_action_about)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// continue with delete
					}
				}).show();

	}

	public void compartilharBio(View v) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		TextView txtBio = (TextView) findViewById(R.id.txtBiografia);
		// pega nome
		TextView txtNome = (TextView) findViewById(R.id.txtNome);

		// pega twitter
		TextView txtTwitter = (TextView) findViewById(R.id.txtTwitterFicha);


		String mensagem = "Mini-biografia: " + txtNome.getText().toString() + " " + txtTwitter.getText().toString() + "\n";
		mensagem = mensagem + txtBio.getText().toString() + "\n" + getString(R.string.url_share) + String.valueOf(idPolitico) + "\n #MonitoraBrasil";
		sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	public void compartilharProcessos(View v) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		TextView txtProcessos = (TextView) findViewById(R.id.txtProcessos);
		// pega nome
		TextView txtNome = (TextView) findViewById(R.id.txtNome);

		// pega twitter
		TextView txtTwitter = (TextView) findViewById(R.id.txtTwitterFicha);


		String mensagem = "Processos: " + txtNome.getText().toString() + " " + txtTwitter.getText().toString();
		mensagem = mensagem + txtProcessos.getText().toString() + "\n" + getString(R.string.url_share) + String.valueOf(idPolitico)
				+ "\n #MonitoraBrasil";
		sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putString("listaPoliticos", "teste");

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.ic_share:
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
            //busca politico
            DataBaseHelper dbh = new DataBaseHelper(FichaActivity.this);
            PoliticoDAO politicoDAO = null;
            try {
                politicoDAO = new PoliticoDAO(dbh.getConnectionSource());
                Politico politico=politicoDAO.getPolitico(idPolitico);
                String tipo;
                tipo = ("c".equals(politico.getTipo()) ?"Dep. ":"Sen. ");
                String twitter;
                twitter = (politico.getTwitter().isEmpty()?"":politico.getTwitter());
                String mensagem = "Ficha " + tipo+politico.getNome() +" "+ twitter+"\n";
                mensagem = mensagem + getString(R.string.url_share) + String.valueOf(politico.getIdCadastro()) + " #monitoraBrasil";
                sendIntent.putExtra(Intent.EXTRA_TEXT, mensagem);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
			break;
		case R.id.ic_comment:
			DialogFragment dialog = new DialogComentario(new UserDAO(getApplicationContext()).getIdUser(), idPolitico, "Comente", 1);
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

}
