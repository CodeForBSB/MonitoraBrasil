package com.gamfig.monitorabrasil.fragments.ficha;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gamfig.monitorabrasil.R;
import com.gamfig.monitorabrasil.adapter.PresencaAdapter;
import com.gamfig.monitorabrasil.classes.Presenca;
import com.gamfig.monitorabrasil.pojo.Grafico;
import com.gamfig.monitorabrasil.pojo.PreferenciasUtil;

public class PresencaFragment extends TabFactory {
	private ProgressBar pb;
	// private RelativeLayout rl;

	List<String> listDataHeader;
	HashMap<String, List<NameValuePair>> listDataChild;

	public PresencaFragment() {

	}

	public void montaLayout() {

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<NameValuePair>>();

		// buscar as presencas
		List<Presenca> presencas = new PreferenciasUtil().getPresenca(getActivity());
		int auseJustifTotal = 0;
		int auseNJustifTotal = 0;
		int presencaTotal = 0;
		listDataHeader.add(getActivity().getString(R.string.toda_legislatura));
		try {
			for (Presenca presenca : presencas) {
				List<NameValuePair> textos = new ArrayList<NameValuePair>();
				listDataHeader.add(String.valueOf(presenca.getAno()));

				textos.add(new BasicNameValuePair(getActivity().getString(R.string.ausenciaJustificada), String.valueOf(presenca.getNrAusenciaJustificada())));
				textos.add(new BasicNameValuePair(getActivity().getString(R.string.ausenciaNJustificada), String.valueOf(presenca.getNrAusenciaNaoJustificada())));
				textos.add(new BasicNameValuePair(getActivity().getString(R.string.ausenciatotal), String.valueOf(presenca.getNrAusenciaJustificada()
						+ presenca.getNrAusenciaNaoJustificada())));
				textos.add(new BasicNameValuePair(getActivity().getString(R.string.preseca), String.valueOf(presenca.getNrPresenca())));
				textos.add(new BasicNameValuePair(getActivity().getString(R.string.total), String.valueOf(presenca.getNrAusenciaJustificada()
						+ presenca.getNrAusenciaNaoJustificada() + presenca.getNrPresenca())));

				listDataChild.put(String.valueOf(presenca.getAno()), textos);

				auseJustifTotal += presenca.getNrAusenciaJustificada();
				auseNJustifTotal += presenca.getNrAusenciaNaoJustificada();
				presencaTotal += presenca.getNrPresenca();

			}

			List<NameValuePair> textos = new ArrayList<NameValuePair>();
			textos.add(new BasicNameValuePair(getActivity().getString(R.string.ausenciaJustificada), String.valueOf(auseJustifTotal)));
			textos.add(new BasicNameValuePair(getActivity().getString(R.string.ausenciaNJustificada), String.valueOf(auseNJustifTotal)));
			textos.add(new BasicNameValuePair(getActivity().getString(R.string.ausenciatotal), String.valueOf(auseJustifTotal + auseNJustifTotal)));
			textos.add(new BasicNameValuePair(getActivity().getString(R.string.preseca), String.valueOf(presencaTotal)));
			textos.add(new BasicNameValuePair(getActivity().getString(R.string.total), String.valueOf(auseJustifTotal + auseNJustifTotal + presencaTotal)));
			listDataChild.put(getActivity().getString(R.string.toda_legislatura), textos);

			// busca o expande list view
			ExpandableListView mExpLv = (ExpandableListView) getActivity().findViewById(R.id.expandableListView1);
			PresencaAdapter listAdapter = new PresencaAdapter(getActivity(), listDataHeader, listDataChild);
			// setting list adapter
			mExpLv.setAdapter(listAdapter);
			mExpLv.expandGroup(0);

			// montar o grafico
			GraphicalView gv = montaGrafico(presencas);

			LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.chart);
			layout.addView(gv);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private GraphicalView montaGrafico(List<Presenca> presencas) {
		Grafico graficoUtil = new Grafico();
		String[] titles = new String[] { getActivity().getString(R.string.ausenciaJustificada), getActivity().getString(R.string.ausenciaNJustificada),
				getActivity().getString(R.string.ausenciatotal), getActivity().getString(R.string.preseca) };
		List<double[]> anosList = new ArrayList<double[]>();

		int i = 0;
		double[] anos = new double[presencas.size()];

		double[] ausJustif = new double[presencas.size()];
		double[] ausNaoJustif = new double[presencas.size()];
		double[] ausTotal = new double[presencas.size()];
		double[] numPresenca = new double[presencas.size()];

		for (Presenca presenca : presencas) {
			// pega os anos disponiveis
			anos[i] = presenca.getAno();

			// numero de ausencias e presencas
			ausJustif[i] = presenca.getNrAusenciaJustificada();
			ausNaoJustif[i] = presenca.getNrAusenciaNaoJustificada();
			ausTotal[i] = presenca.getNrAusenciaJustificada() + presenca.getNrAusenciaNaoJustificada();
			numPresenca[i] = presenca.getNrPresenca();
			i++;
		}
		for (i = 0; i < titles.length; i++) {
			anosList.add(anos);
		}
		List<double[]> values = new ArrayList<double[]>();
		values.add(ausJustif);
		values.add(ausNaoJustif);
		values.add(ausTotal);
		values.add(numPresenca);
		int[] colors = new int[] { Color.rgb(20, 422, 511), Color.MAGENTA, Color.RED, getActivity().getResources().getColor(R.color.bandeira_verde) };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = graficoUtil.buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}
		graficoUtil.setChartSettings(renderer, "Presença", "Ano", "Nº", anos[0], anos[presencas.size() - 1], 0, 100, Color.BLACK, Color.BLACK);

		// renderer.setXLabels(12);
		// renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setGridColor(Color.LTGRAY);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		renderer.setMarginsColor(Color.WHITE);
		renderer.setAxesColor(Color.BLACK);
		renderer.setLabelsColor(Color.BLACK);
		// renderer.setZoomButtonsVisible(true);
		renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		// para linechart
		XYMultipleSeriesDataset dataset = graficoUtil.buildDataset(titles, anosList, values);

		return ChartFactory.getLineChartView(getActivity(), dataset, renderer);

	}

}
