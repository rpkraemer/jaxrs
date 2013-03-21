package exemplo.jaxrs.resource;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.spi.validation.ValidateRequest;

import exemplo.jaxrs.bd.ClusterDatabase;
import exemplo.jaxrs.bd.ClusterInexistenteException;
import exemplo.jaxrs.modelo.Cluster;

@Path("/clusters")
public class ClusterResource {

	private ClusterDatabase db = new ClusterDatabase();
	
	@GET
	@Path("{nome}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getByNome(@PathParam("nome") String nome) {
		Response response;
		try {
			Cluster cluster = db.getByNome(nome);
			response = Response
					.ok(cluster)
					.header("Link", generateLink(cluster))
					.build();
		} catch (ClusterInexistenteException e) {
			response = Response.status(Status.NOT_FOUND).build();
		}
		return response;
	}
	
	private String generateLink(Cluster cluster) {
		String linkHeader = "<%s>; rel=\"%s\";";
		URI uri;
		if (cluster.isAtivo()) {
			uri = UriBuilder.fromUri("/clusters/" + cluster.getNome() + "/ativo/false").build();
			linkHeader =  String.format(linkHeader, uri.toString(), "desativar");
		} else {
			uri = UriBuilder.fromUri("/clusters/" + cluster.getNome() + "/ativo/true").build();
			linkHeader = String.format(linkHeader, uri.toString(), "ativar");
		}
		return linkHeader;
	}

	@PUT
	@Path("{nome}/ativo/{ativar}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response ativarDesativarCluster(@PathParam("nome") String nome, 
									       @PathParam("ativar") boolean ativar) {
		Cluster cluster;
		try {
			cluster = db.getByNome(nome);
			cluster.setAtivo(ativar);
			db.atualizar(cluster);
			return Response.ok(cluster).build();
		} catch (ClusterInexistenteException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Wrapped(element = "clusters")
	public List<Cluster> listaTodos() {
		return db.getTodos();
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@ValidateRequest
	public Response criaCluster(@Valid Cluster cluster) {
		db.criar(cluster);
		return Response.created(
					UriBuilder.fromUri("/clusters/" + cluster.getNome()).build()
				).build();
	}
	
	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@ValidateRequest
	public Response atualizaCluster(@Valid Cluster cluster) {
		db.atualizar(cluster);
		return Response.ok(cluster).build();
	}
	
	@DELETE
	@Path("{nome}")
	public Response deletaCluster(@PathParam("nome") String nome) {
		Response response;
		try {
			Cluster cluster;
			cluster = db.getByNome(nome);
			db.remover(cluster);
			response = Response.noContent().build();
		} catch (ClusterInexistenteException e) {
			response = Response.status(Status.NOT_FOUND).build();
		}
		return response;
	}
}