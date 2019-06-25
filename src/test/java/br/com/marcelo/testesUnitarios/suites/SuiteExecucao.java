package br.com.marcelo.testesUnitarios.suites;

import org.junit.runners.Suite.SuiteClasses;

import br.com.marcelo.testesUnitarios.servicos.CalculoValorLocacaoTest;
import br.com.marcelo.testesUnitarios.servicos.LocacaoServiceTest;

@SuiteClasses({
	CalculoValorLocacaoTest.class,
	LocacaoServiceTest.class
})
public class SuiteExecucao
{
	//remova se puder
}
