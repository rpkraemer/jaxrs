package exemplo.jaxrs.bd;

public class ClusterInexistenteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClusterInexistenteException(String mensagem) {
		super (mensagem);
	}
	
	public ClusterInexistenteException(String mensagem, Throwable ex) {
		super (mensagem, ex);
	}
}
