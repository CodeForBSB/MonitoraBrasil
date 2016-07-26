package com.gamfig.monitorabrasil.activitys;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.gamfig.monitorabrasil.R;

public class SobreActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sobre);
		TextView txt = (TextView) findViewById(R.id.textView1);
		txt.setText(Html
				.fromHtml("<p><b>Monitora, Brasil!</b></p><p>O aplicativo Monitora Brasil utiliza os dados disponibilizados pelo site da Câmara dos Deputados, do Senado Federal, do TSE e da Trasparência Brasil. </p>"
						+ "<p>O objetivo do app é aproximar o cidadão da política. Não basta votar a cada 4 anos, tem que acompanhar o que eles estão fazendo. Além disso, a ferramenta possibilita ao cidadão comunicar-se com o parlamentar, seja através do Twitter ou email."
						+ "Precisamos nos envolver mais com o assunto, e a tecnologia permite fazer isso de uma forma mais fácil. </p>"
						+ "Contribua com o projeto divulgando o app para seus amigos nas redes sociais. Quanto mais pessoas monitorando, mais importante será o app.<br>"
						+ " Caso encontre algum problema, favor entre em contato conosco.</p>"
						+ "<p>Vamos juntos construir uma ferramenta referência na política brasileira!</p>"
						+ "<p>Equipe Monitora, Brasil! <br><a href='http://monitorabrasil.com'>www.monitorabrasil.com</a>" +
						"<br><a href='https://twitter.com/monitorabrasil'>@MonitoraBrasil</a></p>"));
		txt.setMovementMethod(LinkMovementMethod.getInstance());
		TextView txtVersao = (TextView) findViewById(R.id.txtVersao);
		try {
			txtVersao.setText("Versão " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
