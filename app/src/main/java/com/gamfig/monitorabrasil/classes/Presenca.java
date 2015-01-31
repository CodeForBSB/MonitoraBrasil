
package com.gamfig.monitorabrasil.classes;

import java.text.DecimalFormat;

public class Presenca {
	private int ano;
	private int nrPresenca;
	private int nrAusenciaJustificada;
	private int nrAusenciaNaoJustificada;
	private int total;
	
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public int getNrPresenca() {
		return nrPresenca;
	}
	public void setNrPresenca(int nrPresenca) {
		this.nrPresenca = nrPresenca;
	}
	public int getNrAusenciaJustificada() {
		return nrAusenciaJustificada;
	}
	public void setNrAusenciaJustificada(int nrAusenciaJustificada) {
		this.nrAusenciaJustificada = nrAusenciaJustificada;
	}
	public int getNrAusenciaNaoJustificada() {
		return nrAusenciaNaoJustificada;
	}
	public void setNrAusenciaNaoJustificada(int nrAusenciaNaoJustificada) {
		this.nrAusenciaNaoJustificada = nrAusenciaNaoJustificada;
	}
	
	public String getPercentualPresenca(){
		DecimalFormat form = new DecimalFormat("0.00");
		return form.format(Float.valueOf(this.nrPresenca)/Float.valueOf((this.nrPresenca+this.nrAusenciaJustificada+this.nrAusenciaNaoJustificada))*100);
	}
	
	public String getPercentualAusenciaJustificada(){
		DecimalFormat form = new DecimalFormat("0.00");
		return form.format(Float.valueOf(this.nrAusenciaJustificada)/Float.valueOf((this.nrPresenca+this.nrAusenciaJustificada+this.nrAusenciaNaoJustificada))*100);
	}
	
	public String getPercentualAusenciaNaoJustificada(){
		DecimalFormat form = new DecimalFormat("0.00");
		return form.format(Float.valueOf(this.nrAusenciaNaoJustificada)/Float.valueOf((this.nrPresenca+this.nrAusenciaJustificada+this.nrAusenciaNaoJustificada))*100);
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
}
