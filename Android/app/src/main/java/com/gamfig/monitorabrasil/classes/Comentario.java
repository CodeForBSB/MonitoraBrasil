
package com.gamfig.monitorabrasil.classes;


public class Comentario {
	private Usuario user;
	private String comentario;
	private Projeto projeto;
	private String data;
	//temporario, dps mudar o json
	private String id; //id FAcebook
	private String coment;
	private String nome;
	private int idComentario;
	private int like;
	private int unlike;
	
	private boolean monitorado;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComent() {
		return coment;
	}
	public void setComent(String coment) {
		this.coment = coment;
	}
	public Usuario getUser() {
		return user;
	}
	public void setUser(Usuario user) {
		this.user = user;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public Projeto getProjeto() {
		return projeto;
	}
	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public boolean isMonitarado() {
		return monitorado;
	}
	public void setMonitarado(boolean monitarado) {
		this.monitorado = monitarado;
	}
	public int getIdComentario() {
		return idComentario;
	}
	public void setIdComentario(int idComentario) {
		this.idComentario = idComentario;
	}
	public int getLike() {
		return like;
	}
	public void setLike(int like) {
		this.like = like;
	}
	public int getUnlike() {
		return unlike;
	}
	public void setUnlike(int unlike) {
		this.unlike = unlike;
	}

}
