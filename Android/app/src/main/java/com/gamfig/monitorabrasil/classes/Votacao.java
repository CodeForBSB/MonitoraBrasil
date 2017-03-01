
package com.gamfig.monitorabrasil.classes;

import java.util.ArrayList;

public class Votacao {
	private int id;
	private String resumo;
	private String data;
	private String objetivo;
	private Projeto projeto;
	private ArrayList<Politico> politicos = new ArrayList<Politico>();
	
	public void adicionarPolitico(Politico politico){
		politicos.add(politico);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getResumo() {
		return resumo;
	}
	public void setResumo(String resumo) {
		this.resumo = resumo;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getObjetivo() {
		return objetivo;
	}
	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}
	public Projeto getProjeto() {
		return projeto;
	}
	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
	public ArrayList<Politico> getPoliticos() {
		return politicos;
	}
	public void setPoliticos(ArrayList<Politico> politicos) {
		this.politicos = politicos;
	}
}
