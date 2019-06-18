package br.com.marcelo.testesUnitarios.servicos;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import br.com.marcelo.testesUnitarios.entidades.Filme;
import br.com.marcelo.testesUnitarios.entidades.Locacao;
import br.com.marcelo.testesUnitarios.entidades.Usuario;
import br.com.marcelo.testesUnitarios.utils.DataUtils;

public class LocacaoServiceTest
{

	@Test
	public void teste()
	{
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 2, 5.0);

		// acao
		Locacao locacao = new LocacaoService().alugarFilme(usuario, filme);

		// verificacao
		Assert.assertThat(locacao.getValor(), is(equalTo(5.0)));
		Assert.assertThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		Assert.assertThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true) );
	}
}
