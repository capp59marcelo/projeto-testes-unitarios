package br.com.marcelo.testesUnitarios.servicos;

import static br.com.marcelo.testesUnitarios.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.marcelo.testesUnitarios.daos.LocacaoDAO;
import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.exceptions.FilmeSemEstoqueException;
import br.com.marcelo.testesUnitarios.exceptions.LocadoraException;
import br.com.marcelo.testesUnitarios.utils.DataUtils;

public class LocacaoService
{
	private LocacaoDAO locacaoDAO;
	private SPCService spcService;
	private EmailService emailService;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws LocadoraException, FilmeSemEstoqueException
	{

		if (usuario == null)
		{
			throw new LocadoraException("Usuario vazio");
		}

		if (filmes == null || filmes.isEmpty())
		{
			throw new LocadoraException("Filme vazio");
		}

		for (Filme filme : filmes)
		{
			if (filme.getEstoque() == 0)
			{
				throw new FilmeSemEstoqueException();
			}
		}
		
		boolean negativado;
		try
		{
			negativado = spcService.possuiNegativacao(usuario);
		}
		catch (Exception e)
		{
			throw new LocadoraException("Problemas com SPC, tente novamente");
		}
		
		if(negativado) {
			throw new LocadoraException("Usuário Negativado");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(Calendar.getInstance().getTime());
		locacao.setValor(calcularValorLocacao(filmes));

		// Entrega no dia seguinte
		Date dataEntrega = Calendar.getInstance().getTime();
		dataEntrega = adicionarDias(dataEntrega, 1);
		if(DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY))
		{
			dataEntrega = adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);

		// Salvando a locacao...
		locacaoDAO.salvar(locacao);

		return locacao;
	}
	
	private Double calcularValorLocacao(List<Filme> filmes)
	{
		Double valorTotal = 0d;
		for (int i = 0; i < filmes.size(); i++)
		{
			Filme filme = filmes.get(i);
			Double valorFilme = filme.getPrecoLocacao();
			switch (i)
			{
				case 2: valorFilme *= 0.75; break;
				case 3: valorFilme *= 0.5; 	break;
				case 4: valorFilme *= 0.25; break;
				case 5: valorFilme = 0d; 	break;
			}
			valorTotal += valorFilme;
		}
		return valorTotal;
	}
	public void notificarAtrasos()
	{
		List<Locacao> locacoes = locacaoDAO.obterLocacoesPendentes();
		for (Locacao locacao : locacoes)
		{
			if(locacao.getDataRetorno().before(new Date()))
			{
				emailService.notificarAtraso(locacao.getUsuario());
			}
		}
	}
	
	public void prorrogarLocacao(Locacao locacao, int dias)
	{
		Locacao novaLocacao = new Locacao();
		novaLocacao.setUsuario(locacao.getUsuario());
		novaLocacao.setFilmes(locacao.getFilmes());
		novaLocacao.setDataLocacao(new Date());
		novaLocacao.setDataRetorno(DataUtils.obterDataComDiferencaDias(dias));
		novaLocacao.setValor(locacao.getValor() * dias);
		locacaoDAO.salvar(novaLocacao);
	}
}