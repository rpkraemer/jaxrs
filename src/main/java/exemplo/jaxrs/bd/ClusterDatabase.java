package exemplo.jaxrs.bd;

import java.util.ArrayList;
import java.util.List;
import exemplo.jaxrs.modelo.Cluster;
import exemplo.jaxrs.modelo.Computador;

public class ClusterDatabase {

	private List<Cluster> clusters;
	
	public ClusterDatabase() {
		clusters = new ArrayList<Cluster>();
		
		Cluster c1 = new Cluster("cluster-1", true);
		Cluster c2 = new Cluster("cluster-2", true);
		Cluster c3 = new Cluster("cluster-3", false);
		
		Computador cp = new Computador("pc1-ximango");
		cp.setNumeroDeCPUs((short) 8);
		cp.setCapacidadeDeHD(1024);
		cp.setCapacidadeDeRAM(16000);
		
		c1.adicionarComputador(cp);
		
		clusters.add(c1);
		clusters.add(c2);
		clusters.add(c3);
	}
	
	public Cluster getByNome(String nome) throws ClusterInexistenteException {
		for (Cluster c : clusters)
			if (nome.equals(c.getNome()))
				return c;
		throw new ClusterInexistenteException(String.format("O cluster %s não existe", nome));
	}

	public List<Cluster> getTodos() {
		return clusters;
	}

	public void criar(Cluster cluster) {
		clusters.add(cluster);
	}

	public void atualizar(Cluster cluster) {
		remover(cluster);
		clusters.add(cluster);
	}
	
	public void remover(Cluster cluster) {
		clusters.remove(cluster);
	}
}