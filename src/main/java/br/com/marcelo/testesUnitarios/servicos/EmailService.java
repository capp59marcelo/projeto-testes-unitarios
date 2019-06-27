package br.com.marcelo.testesUnitarios.servicos;

import br.com.marcelo.testesUnitarios.entidades.Usuario;

public interface EmailService
{
	public void notificarAtraso(Usuario usuario);
}
