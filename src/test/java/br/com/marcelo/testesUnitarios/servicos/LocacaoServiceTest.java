package br.com.marcelo.testesUnitarios.servicos;

import static br.com.marcelo.testesUnitarios.builders.FilmeBuilder.umFilme;
import static br.com.marcelo.testesUnitarios.builders.FilmeBuilder.umFilmeSemEstoque;
import static br.com.marcelo.testesUnitarios.builders.LocacaoBuilder.umLocacao;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.marcelo.testesUnitarios.daos.LocacaoDAO;
import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.exceptions.FilmeSemEstoqueException;
import br.com.marcelo.testesUnitarios.exceptions.LocadoraException;
import br.com.marcelo.testesUnitarios.matchers.MatchersProprios;
import br.com.marcelo.testesUnitarios.utils.DataUtils;

public class LocacaoServiceTest
{
	@InjectMocks
	private LocacaoService locacaoService;
	
	@Mock
	private LocacaoDAO locacaoDAO;
	@Mock
	private SPCService spcService;
	@Mock
	private EmailService emailService;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
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
	
	@Test
	public void naoDeveAlugarFilmeParaNegativadoSPC() throws Exception {
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spcService.possuiNegativacao(Mockito.any(Usuario.class))).thenReturn(true);
		
		//acao
		try
		{
			locacaoService.alugarFilme(usuario, filmes);
			//verificacao
			Assert.fail();
		}
		catch (LocadoraException e)
		{
			Assert.assertThat(e.getMessage(), is("Usuário Negativado"));
		}
		
		Mockito.verify(spcService).possuiNegativacao(usuario);
	}
	
	@Test
	public void deveEnviarEmailParaLocacoesAtrasadas(){
		//cenario
		Usuario usuario = umUsuario().agora();
		Usuario usuario2 = umUsuario().comNome("Usuario em dia").agora();
		Usuario usuario3 = umUsuario().comNome("Outro atrasado").agora();
		List<Locacao> locacoes = Arrays.asList(
				umLocacao().atrasada().comUsuario(usuario).agora(),
				umLocacao().comUsuario(usuario2).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora(),
				umLocacao().atrasada().comUsuario(usuario3).agora());
		Mockito.when(locacaoDAO.obterLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		locacaoService.notificarAtrasos();
		
		//verificacao
		Mockito.verify(emailService, Mockito.times(3)).notificarAtraso(Mockito.any(Usuario.class));
		Mockito.verify(emailService).notificarAtraso(usuario);
		Mockito.verify(emailService, Mockito.atLeastOnce()).notificarAtraso(usuario3);
		Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
		Mockito.verifyNoMoreInteractions(emailService);
	}
	
	@Test
	public void deveTratarErronoSPC() throws Exception
	{
		//cenario
		Usuario usuario = umUsuario().agora();
		List<Filme> filmes = Arrays.asList(umFilme().agora());
		
		Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha catastrófica"));
		
		//verificacao
		exception.expect(LocadoraException.class);
		exception.expectMessage("Problemas com SPC, tente novamente");
		
		//acao
		locacaoService.alugarFilme(usuario, filmes);
	}
}
