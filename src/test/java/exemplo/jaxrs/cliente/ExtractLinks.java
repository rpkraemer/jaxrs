package exemplo.jaxrs.cliente;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class ExtractLinks {
	
	public static class Links {
		private final List<Link> jaxLinks;
		
		Links(List<Link> jaxLinks) {
			this.jaxLinks = jaxLinks;
		}
		
		public Link getByRel(String rel) {
			for (Link link : jaxLinks)
				if (rel.equals(link.getRel()))
					return link;
			return null;
		}
	}

	static public Links from(Response response) {
		String[] links = response.getHeaderString("Link").split(",");
		if (links == null) return null;
		List<Link> jaxLinks = new ArrayList<Link>();
		for (String link : links) {
			final String uri = link.split(";")[0].trim().replaceAll("[<>]", "");
			final String rel = link.split(";")[1].trim().replaceAll("(rel=)|(\")", "");
			Link jaxLink = createJaxRSLink(uri, rel);
			jaxLinks.add(jaxLink);
		}
		return new Links(jaxLinks);
	}

	private static Link createJaxRSLink(final String uri, final String rel) {
		return new Link() {
			@Override
			public String toString() { return uri + " - " + rel; }
			@Override
			public UriBuilder getUriBuilder() { return null; }
			@Override
			public URI getUri() {
				try {
					return new URI(uri);
				} catch (URISyntaxException e) {
					return null;
				}
			}
			@Override
			public String getType() { return null; }
			@Override
			public String getTitle() { return null; }
			@Override
			public List<String> getRels() { return null; }
			@Override
			public String getRel() { return rel; }
			@Override
			public Map<String, String> getParams() { return null; }
		};
	}
}
