package br.com.marcelo.testesUnitarios.servicos;

import static br.com.marcelo.testesUnitarios.builders.FilmeBuilder.umFilme;
import static br.com.marcelo.testesUnitarios.builders.UsuarioBuilder.umUsuario;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import br.com.marcelo.testesUnitarios.daos.LocacaoDAO;
import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.exceptions.FilmeSemEstoqueException;
import br.com.marcelo.testesUnitarios.exceptions.LocadoraException;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest
{
	private LocacaoService locacaoService;
	private LocacaoDAO locacaoDAO;
	private SPCService spcService;
	
	@Parameter
	public List<Filme> filmes;
	
	@Parameter(value=1)
	public Double valorLocacao;
	
	@Parameter(value=2)
	public String cenario;
	
	private static Filme filme1 = umFilme().agora();
	private static Filme filme2 = umFilme().agora();
	private static Filme filme3 = umFilme().agora();
	private static Filme filme4 = umFilme().agora();
	private static Filme filme5 = umFilme().agora();
	private static Filme filme6 = umFilme().agora();
	private static Filme filme7 = umFilme().agora();
	
	@Before
	public void setup()
	{
		locacaoService = new LocacaoService();
		locacaoDAO = Mockito.mock(LocacaoDAO.class);
		locacaoService.setLocacaoDAO(locacaoDAO);
		spcService = Mockito.mock(SPCService.class);
		locacaoService.setSPCService(spcService);
	}
	
	@Parameters(name="{2}")
	public static Collection<Object[]> getParametros()
	{
		return Arrays.asList(new Object[][] {
			{Arrays.asList(filme1, filme2), 8.0, "2 filmes: sem desconto"},
			{Arrays.asList(filme1, filme2, filme3), 11.0, "3 filmes: 75%"},
			{Arrays.asList(filme1, filme2, filme3, filme4), 13.0, "4 filmes: 50%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5), 14.0, "5 filmes: 25%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6), 14.0, "6 filmes: 0%"},
			{Arrays.asList(filme1, filme2, filme3, filme4, filme5, filme6, filme7), 18.0, "7 filmes: sem desconto"}
			
		});
	}

	@Test
	public void deveCalcularValorLocacaoConsiderandoDescontos() throws LocadoraException, FilmeSemEstoqueException
	{
		//cenario
		Usuario usuario = umUsuario().agora();
		
		//acao
		Locacao resultado = locacaoService.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(resultado.getValor(), is(valorLocacao));
	}
}
