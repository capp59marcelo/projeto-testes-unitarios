package br.com.marcelo.testesUnitarios.servicos;

import static br.com.marcelo.testesUnitarios.utils.DataUtils.adicionarDias;

import java.util.Date;

import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.exceptions.FilmeSemEstoqueException;
import br.com.marcelo.testesUnitarios.exceptions.LocadoraException;

public class LocacaoService
{
	public Locacao alugarFilme(Usuario usuario, Filme filme) throws LocadoraException, FilmeSemEstoqueException
	{

		if (usuario == null)
		{
			throw new LocadoraException("Usuario vazio");
		}

		if (filme == null)
		{
			throw new LocadoraException("Filme vazio");
		}

		if (filme.getEstoque() == 0)
		{
			throw new FilmeSemEstoqueException();
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
