package exemplo.jaxrs.cliente;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import exemplo.jaxrs.app.Server;
import exemplo.jaxrs.cliente.ExtractLinks.Links;
import exemplo.jaxrs.modelo.Cluster;
import exemplo.jaxrs.modelo.Computador;

public class ClusterResourceTeste {
	
	private Client cliente;
	private WebTarget app;
	
	@BeforeClass
	public static void iniciarServidor() {
		Server.iniciar();
	}
	
	@AfterClass
	public static void pararServidor() {
		Server.parar();
	}
	
	@Before
	public void setUp() {
		cliente = ClientBuilder.newClient();
		app = cliente.target(Server.BASE_URI);
	}
	
	@Test
	public void deveRecuperarUmClusterEmXML() {		
		Response resp = app.path("/clusters/cluster-1")
				.request(MediaType.APPLICATION_XML).get();
		
		Cluster c = resp.readEntity(Cluster.class);
		
		Assert.assertEquals(200, resp.getStatus());
		Assert.assertEquals("cluster-1", c.getNome());
		Assert.assertEquals(1, c.getComputadores().size());
		Assert.assertEquals("pc1-ximango", c.getComputadores().get(0).getNome());
	}
	
	@Test
	public void deveRecuperarUmClusterEmJSON() {		
		Response resp = app.path("/clusters/cluster-1")
				.request(MediaType.APPLICATION_JSON).get();
		
		Cluster c = resp.readEntity(Cluster.class);
		
		Assert.assertEquals(200, resp.getStatus());
		Assert.assertEquals("cluster-1", c.getNome());
		Assert.assertEquals(1, c.getComputadores().size());
		Assert.assertEquals("pc1-ximango", c.getComputadores().get(0).getNome());
	}
	
	@Test
	public void deveRetornar404PraClusterInexistente() {		
		Response resp = app.path("/clusters/nao-existe")
				.request(MediaType.APPLICATION_JSON).get();
		
		Assert.assertEquals(404, resp.getStatus());
	}
	
	@Test
	public void deveListarTodosOsClustersEmXML() {
		Response resp = app.path("/clusters")
				.request(MediaType.APPLICATION_XML).get();
		List<Cluster> clusters = resp.readEntity(new GenericType<List<Cluster>>(){});
		Assert.assertEquals(3, clusters.size());
	}
	
	@Test
	public void deveListarTodosOsClustersEmJSON() {
		Response resp = app.path("/clusters")
				.request(MediaType.APPLICATION_JSON).get();
		List<Cluster> clusters = resp.readEntity(new GenericType<List<Cluster>>(){});
		Assert.assertEquals(3, clusters.size());
	}
	
	@Test
	public void deveCriarUmNovoClusterEmXML() {
		Cluster cluster = new Cluster("novo-cluster-xml", true);
		cluster.adicionarComputador(new Computador("pc1"));
		cluster.adicionarComputador(new Computador("pc2"));
		cluster.adicionarComputador(new Computador("pc3"));
		
		Response resp = app.path("/clusters").request(MediaType.APPLICATION_XML)
				.post(Entity.entity(cluster, MediaType.APPLICATION_XML));
		
		Assert.assertEquals(201, resp.getStatus());
		
		String createdClusterPath = resp.getLocation().toString();
		resp.close();
		
		resp = cliente.target(createdClusterPath).request(MediaType.APPLICATION_XML).get();
		Cluster created = resp.readEntity(Cluster.class);
		
		Assert.assertEquals("novo-cluster-xml", created.getNome());
	}
	
	@Test
	public void deveCriarUmNovoClusterEmJSON() {
		Cluster cluster = new Cluster("novo-cluster-json", true);
		cluster.adicionarComputador(new Computador("pc1"));
		cluster.adicionarComputador(new Computador("pc2"));
		cluster.adicionarComputador(new Computador("pc3"));
		
		Response resp = app.path("/clusters")
				.request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(cluster, MediaType.APPLICATION_JSON));
		
		Assert.assertEquals(201, resp.getStatus());
		
		String createdClusterPath = resp.getLocation().toString();
		resp.close();
		
		resp = cliente.target(createdClusterPath)
				.request(MediaType.APPLICATION_JSON)
				.get();
		Cluster created = resp.readEntity(Cluster.class);
		
		Assert.assertEquals("novo-cluster-json", created.getNome());
	}
	
	@Test
	public void deveAtualizarUmClusterEmXML() {
		Invocation clusterGetInvoc = app.path("/clusters/cluster-2")
				.request(MediaType.APPLICATION_XML)
				.buildGet();
		
		Cluster cluster = clusterGetInvoc.invoke().readEntity(Cluster.class);
		Assert.assertEquals(0, cluster.getComputadores().size());

		cluster.getComputadores().add(new Computador("new-pc"));
		app.path("/clusters")
			.request()
			.put(Entity.entity(cluster, MediaType.APPLICATION_XML))
			.close();
		
		cluster = null;
		cluster = clusterGetInvoc.invoke().readEntity(Cluster.class);
		Assert.assertEquals(1, cluster.getComputadores().size());
	}
	
	@Test
	public void deveAtualizarUmClusterEmJSON() {
		Invocation clusterGetInvoc = app.path("/clusters/cluster-2")
				.request(MediaType.APPLICATION_JSON)
				.buildGet();
		
		Cluster cluster = clusterGetInvoc.invoke().readEntity(Cluster.class);
		Assert.assertEquals(1, cluster.getComputadores().size());

		cluster.getComputadores().add(new Computador("new-pc"));
		app.path("/clusters")
			.request()
			.put(Entity.entity(cluster, MediaType.APPLICATION_JSON))
			.close();
		
		cluster = null;
		cluster = clusterGetInvoc.invoke().readEntity(Cluster.class);
		Assert.assertEquals(2, cluster.getComputadores().size());
	}
	
	@Ignore
	public void naoDeveCriarUmClusterInvalido() {
		Cluster cluster = new Cluster(null, false);
		Response resp = app.path("/clusters")
				.request()
				.post(Entity.entity(cluster, MediaType.APPLICATION_XML));

		Assert.assertEquals(200, resp.getStatus());
	}
	
	/*
	 * Testes Hypermedia (HATEOAS)
	 */
	
	@Test
	public void deveDesativarUmCluster() {
		String clusterName = "cluster-1";
		Response r = app.path("/clusters/" + clusterName).request().get();
		
		Cluster cluster = r.readEntity(Cluster.class);
		Assert.assertTrue(cluster.isAtivo());
		
		Links links = ExtractLinks.from(r);
		Link desativar = links.getByRel("desativar");
		r.close();
		
		r = app.path(desativar.getUri().toString()).request().put(null);
		Assert.assertEquals(200, r.getStatus());
		r.close();
		
		cluster = app.path("/clusters/" + clusterName).request().get(Cluster.class);
		Assert.assertFalse(cluster.isAtivo());
	}
	
	@Test
	public void deveAtivarUmCluster() {
		String clusterName = "cluster-3";
		Response r = app.path("/clusters/" + clusterName).request().get();
		
		Cluster cluster = r.readEntity(Cluster.class);
		Assert.assertFalse(cluster.isAtivo());
		
		Links links = ExtractLinks.from(r);
		Link desativar = links.getByRel("ativar");
		r.close();
		
		r = app.path(desativar.getUri().toString()).request().put(null);
		Assert.assertEquals(200, r.getStatus());
		r.close();
		
		cluster = app.path("/clusters/" + clusterName).request().get(Cluster.class);
		Assert.assertTrue(cluster.isAtivo());
	}
	
}
