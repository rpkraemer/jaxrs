package exemplo.jaxrs.modelo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cluster")
public class Cluster {

	@NotNull
	private String nome;
	
	private boolean ativo;
	
	@XmlElementWrapper(name = "computadores")
	@XmlElement(name = "computador")
	@NotNull 
	@Size(min = 1)
	private List<Computador> computadores = new ArrayList<Computador>();

	public Cluster() {}

	public Cluster(String nome, boolean ativo) {
		this.nome = nome;
		this.ativo = ativo;
	}

	public void adicionarComputador(Computador computador) {
		computadores.add(computador);
	}

	public void removerComputador(Computador computador) {
		computadores.remove(computador);
	}

	public List<Computador> getComputadores() {
		return computadores;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	@Override
	public String toString() {
		return getNome() + " - " + getComputadores();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Cluster))
			return false;
		Cluster that = (Cluster) obj;
		return this.getNome().equals(that.getNome());
	}
}