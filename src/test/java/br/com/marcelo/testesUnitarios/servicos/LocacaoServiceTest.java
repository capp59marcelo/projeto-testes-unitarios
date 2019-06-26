package br.com.marcelo.testesUnitarios.servicos;

import static br.com.marcelo.testesUnitarios.builders.FilmeBuilder.umFilme;
import static br.com.marcelo.testesUnitarios.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.marcelo.testesUnitarios.builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.com.marcelo.testesUnitarios.daos.LocacaoDAO;
import br.com.marcelo.testesUnitarios.daos.LocacaoDAOFake;
import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.exceptions.FilmeSemEstoqueException;
import br.com.marcelo.testesUnitarios.exceptions.LocadoraException;
import br.com.marcelo.testesUnitarios.matchers.MatchersProprios;
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
		locacaoService = new LocacaoService();
		LocacaoDAO locacaoDAO = Mockito.mock(LocacaoDAO.class);
		locacaoService.setLocacaoDAO(locacaoDAO);
	}

	@Test
	public void deveAlugarFilme() throws Exception
	{
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList( umFilme().comValor(5.0).agora() );

		// acao
		Locacao locacao = locacaoService.alugarFilme(usuario, filmes);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
	}

	// Primeira forma do teste esperar uma Exception
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception
	{
		// cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilmeSemEstoque().agora());

		// acao
		locacaoService.alugarFilme(usuario, filmes);
	}


	// Segunda forma do teste esperar uma Exception
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException
	{
		// cenario
		List<Filme> filmes = Arrays.asList( umFilme().agora());

		// acao
		try
		{
			locacaoService.alugarFilme(null, filmes);
			Assert.fail();
		}
		catch (LocadoraException e)
		{
			Assert.assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}
	

	// Terceira forma do teste esperar uma Exception
	@Test
	public void naoDeveAlugarFilmeSemFilme() throws LocadoraException, FilmeSemEstoqueException
	{
		// cenario
		Usuario usuario = umUsuario().agora();
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		locacaoService.alugarFilme(usuario, null);
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException
	{
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(	umFilme().agora());
		
		//acao
		Locacao retorno = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), MatchersProprios.caiNumaSegunda());
	}
}
