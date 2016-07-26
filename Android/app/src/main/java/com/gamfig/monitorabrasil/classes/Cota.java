
package com.gamfig.monitorabrasil.classes;

import java.util.List;

public class Cota {
	private int id;
	private String tipo;
	private double valor;
	private int variacaoPercentual;
	private List<Beneficiario> beneficiario;
	private int mes;
	private int ano;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public int getVariacaoPercentual() {
		return variacaoPercentual;
	}
	public void setVariacaoPercentual(int variacaoPercentual) {
		this.variacaoPercentual = variacaoPercentual;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public List<Beneficiario> getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(List<Beneficiario> beneficiario) {
		this.beneficiario = beneficiario;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
}
