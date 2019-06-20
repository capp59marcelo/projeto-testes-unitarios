package br.com.marcelo.testesUnitarios.servicos;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.exceptions.FilmeSemEstoqueException;
import br.com.marcelo.testesUnitarios.exceptions.LocadoraException;
import br.com.marcelo.testesUnitarios.utils.DataUtils;

public class LocacaoServiceTest
{

	private LocacaoService locacaoService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup()
	{
		System.out.println("before");
		locacaoService = new LocacaoService();
	}
	
	@After
	public void tearDown()
	{
		System.out.println("after");
	}
	
	@BeforeClass
	public static void setupClass()
	{
		System.out.println("before Class");
	}
	
	@AfterClass
	public static void tearDownClass()
	{
		System.out.println("after Class");
	}

	@Test
	public void testeLocacao() throws Exception
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 4.0);

		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filme);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(4.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}

	// Primeira forma do teste esperar uma Exception
	@Test(expected = FilmeSemEstoqueException.class)
	public void testeLocacaoFilmeSemEstoque() throws Exception
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 4.0);

		// acao
		locacaoService.alugarFilme(usuario, filme);
	}


	// Segunda forma do teste esperar uma Exception
	@Test
	public void testeLocacaoUsuarioVazio() throws FilmeSemEstoqueException
	{
		// cenario
		
		Filme filme = new Filme("Filme 2", 2, 4.0);

		// acao

		try
		{
			locacaoService.alugarFilme(null, filme);
			Assert.fail();
		}
		catch (LocadoraException e)
		{
			Assert.assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	

	// Terceira forma do teste esperar uma Exception
	@Test
	public void testeFilmeVazio() throws LocadoraException, FilmeSemEstoqueException
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 2");
		
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		locacaoService.alugarFilme(usuario, null);
	}
}
