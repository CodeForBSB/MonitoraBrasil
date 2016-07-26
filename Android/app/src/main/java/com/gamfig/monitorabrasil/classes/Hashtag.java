package com.gamfig.monitorabrasil.classes;

public class Hashtag {

	private String hashtag;
	private int id;
	private int nrLikes;
	private int nrUnlikes;
	private int idDeputado;	
	private int idUser;
	private Politico politico;

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNrLikes() {
		return nrLikes;
	}

	public void setNrLikes(int nrLikes) {
		this.nrLikes = nrLikes;
	}

	public int getNrUnlikes() {
		return nrUnlikes;
	}

	public void setNrUnlikes(int nrUnlikes) {
		this.nrUnlikes = nrUnlikes;
	}

	public int getIdDeputado() {
		return idDeputado;
	}

	public void setIdDeputado(int idDeputado) {
		this.idDeputado = idDeputado;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public Politico getPolitico() {
		return politico;
	}

	public void setPolitico(Politico politico) {
		this.politico = politico;
	}

}
