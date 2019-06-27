package br.com.marcelo.testesUnitarios.servicos;

import br.com.marcelo.testesUnitarios.entidades.Usuario;

public interface SPCService
{
	public boolean possuiNegativacao(Usuario usuario) throws Exception;
}
