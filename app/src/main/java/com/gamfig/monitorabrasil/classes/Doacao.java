
package com.gamfig.monitorabrasil.classes;

import java.util.List;

public class Doacao {
	private String tipo;
	private List<Doador> doador;

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public List<Doador> getDoador() {
		return doador;
	}

	public void setDoador(List<Doador> doador) {
		this.doador = doador;
	}
}
