package br.com.marcelo.testesUnitarios.servicos;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.utils.DataUtils;

public class LocacaoServiceTest
{

	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testeLocacao() throws Exception
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);

		// acao
		Locacao locacao = new LocacaoService().alugarFilme(usuario, filme);
			
		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true) );
	}
	
	//Primeira forma do teste esperar uma Exception
	@Test(expected=Exception.class)
	public void testeLocacaoFilmeSemEstoque1() throws Exception
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		// acao
		new LocacaoService().alugarFilme(usuario, filme);
	}
	
	//Segunda forma do teste esperar uma Exception
	@Test
	public void testeLocacaoFilmeSemEstoque2()
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		// acao
		try
		{
			new LocacaoService().alugarFilme(usuario, filme);
			Assert.fail("Deveria ter lancado uma excecao");
		}
		catch (Exception e)
		{
			Assert.assertThat(e.getMessage(), is("Filme sem estoque"));
		}
	}
	
	//Terceira forma do teste esperar uma Exception
	@Test
	public void testeLocacaoFilmeSemEstoque3() throws Exception
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");
		
		// acao
		new LocacaoService().alugarFilme(usuario, filme);
		
		
	}
}
