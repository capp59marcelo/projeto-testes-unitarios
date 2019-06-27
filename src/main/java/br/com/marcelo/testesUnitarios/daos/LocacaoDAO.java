package br.com.marcelo.testesUnitarios.daos;

import java.util.List;

import br.com.marcelo.testesUnitarios.entidades.Locacao;

public interface LocacaoDAO
{
	public void salvar(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();
}
