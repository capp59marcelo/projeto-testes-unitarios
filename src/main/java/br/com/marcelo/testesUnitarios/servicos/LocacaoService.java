package br.com.marcelo.testesUnitarios.servicos;

import static br.com.marcelo.testesUnitarios.utils.DataUtils.adicionarDias;

import java.util.Date;

import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;

public class LocacaoService
{
	public Locacao alugarFilme(Usuario usuario, Filme filme) throws Exception
	{
		if(filme.getEstoque() == 0)
		{
			throw new Exception("Filme sem estoque");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		// Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		// TODO adicionar método para salvar

		return locacao;
	}
}
