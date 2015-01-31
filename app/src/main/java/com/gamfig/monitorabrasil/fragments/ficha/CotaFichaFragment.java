package com.gamfig.monitorabrasil.fragments.ficha;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.DAO.DeputadoDAO;
import com.gamfig.monitorabrasil.adapter.CotaAdapter;
import com.gamfig.monitorabrasil.adapter.ProjetoVotoAdapter;
import com.gamfig.monitorabrasil.classes.Beneficiario;
import com.gamfig.monitorabrasil.classes.Cota;
import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.pojo.Util;

public class CotaFichaFragment extends TabFactory {
	private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.DKGRAY, Color.RED, Color.YELLOW };

	List<NameValuePair> listDataHeader;
	HashMap<String, List<Beneficiario>> listDataChild;

	ProjetoVotoAdapter adapter;
	ExpandableListView lv;
	EditText edtTexto;
	int idPolitico;
	int currentPage;
	String casa;
	boolean chegouFim = false;

	public CotaFichaFragment() {

	}

	public void montaLayout() {

		lv = (ExpandableListView) getActivity().findViewById(R.id.listView_cotas_ficha);
		// buscar as infos do deputado
		idPolitico = getBundle().getInt("idPolitico");
		casa = getBundle().getString("casa");

		// busca politico
		Politico politico = new Politico();
		politico.setIdCadastro(idPolitico);
		politico.setTipoParlamentar(casa);
		politico = new DeputadoDAO(getActivity()).buscaPolitico(politico);

		new buscaCota(politico).execute();

		// montar o grafico

	}

	public class buscaCota extends AsyncTask<Void, Void, ArrayList<Cota>> {
		Politico politico;

		public buscaCota(Politico politico) {
			this.politico = politico;
		}

		@Override
		protected ArrayList<Cota> doInBackground(Void... params) {

			// buscar os projetos da lista do user

			new DeputadoDAO();

			return DeputadoDAO.buscaCotasAgrupada(politico.getIdCadastro());
		}

		protected void onPostExecute(ArrayList<Cota> cotas) {

			try {
				listDataHeader = new ArrayList<NameValuePair>();
				listDataChild = new HashMap<String, List<Beneficiario>>();
				double valorTotal = 0;
				double valorGasolina = 0;

				for (Cota cota : cotas) {
					// zera o total do tipo de cota
					double valorTotalTipo = 0;

					List<Beneficiario> textos = new ArrayList<Beneficiario>();
					for (Beneficiario beneficiario : cota.getBeneficiario()) {
						textos.add(beneficiario);
						valorTotal += beneficiario.getValor();
						valorTotalTipo += beneficiario.getValor();
						if (cota.getId() == 3) { // gasolina
							valorGasolina += beneficiario.getValor();
						}
					}
					listDataHeader.add(new BasicNameValuePair(cota.getTipo(), String.valueOf(valorTotalTipo)));
					listDataChild.put(cota.getTipo(), textos);
				}

				ExpandableListView mExpLv = (ExpandableListView) getActivity().findViewById(R.id.listView_cotas_ficha);
				CotaAdapter listAdapter = new CotaAdapter(getActivity(), listDataHeader, listDataChild, politico);
				// setting list adapter
				mExpLv.setAdapter(listAdapter);

				// atualiza topo
				TextView txtGastosTotais = (TextView) getActivity().findViewById(R.id.txtGastosTotais);
				DecimalFormat df = new DecimalFormat("#,###,##0.00");
				txtGastosTotais.setText(Html.fromHtml("<B>R$ " + df.format(valorTotal) + "</B><BR>GASTOS"));

				// gasolina
				double litros = valorGasolina / 3;
				// 10km por litro
				double km = litros * 10;
				DecimalFormat df2 = new DecimalFormat("#,###,###");
				TextView txtKM = (TextView) getActivity().findViewById(R.id.txtKM);
				txtKM.setText(Html.fromHtml("<B>" + df2.format(km) + "km</B><BR>Percorridos"));

				// posicao

				// montar grafico
				GraphicalView gv = montaGrafico(listDataHeader);

				LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart_cotas);
				layout.addView(gv);

				ViewFlipper vflip = (ViewFlipper) getActivity().findViewById(R.id.flipper_cotas_ficha);
				vflip.setVisibility(View.VISIBLE);

				RelativeLayout rlPb = (RelativeLayout) getActivity().findViewById(R.id.rl_cotas);
				rlPb.setVisibility(View.INVISIBLE);

			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		private GraphicalView montaGrafico(List<NameValuePair> listDataHeader) {

			// para piechart
			CategorySeries mSeries = new CategorySeries("");
			DefaultRenderer mRenderer = new DefaultRenderer();
			SimpleSeriesRenderer render;
			for (NameValuePair categoria : listDataHeader) {
				mSeries.add(Util.converteStringPrimeiraMaiuscula(categoria.getName().toLowerCase()), Double.parseDouble(categoria.getValue()));
				render = new SimpleSeriesRenderer();
				mRenderer.setLabelsColor(Color.BLACK);
				render.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
				mRenderer.addSeriesRenderer(render);
			}

			return ChartFactory.getPieChartView(getActivity(), mSeries, mRenderer);

		}
	}

}
