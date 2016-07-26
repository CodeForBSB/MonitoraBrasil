package com.gamfig.monitorabrasil.classes;

public class Evento {

	private String nome;
	private String evento;
	private int idElemento;
	private String desc;
	private String tempo;
	private String dtLog;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public int getIdElemento() {
		return idElemento;
	}

	public void setIdElemento(int idElemento) {
		this.idElemento = idElemento;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public String getData() {
		return dtLog;
	}

	public void setData(String data) {
		this.dtLog = data;
	}

}
