package br.com.marcelo.testesUnitarios.daos;

import java.util.List;

import br.com.marcelo.testesUnitarios.entidades.Locacao;

public class LocacaoDAOFake implements LocacaoDAO
{
	@Override
	public void salvar(Locacao locacao) { }

	@Override
	public List<Locacao> obterLocacoesPendentes() { return null; }
}
