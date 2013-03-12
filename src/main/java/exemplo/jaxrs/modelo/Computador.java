package exemplo.jaxrs.modelo;

public class Computador {

	private String nome;
	private short numeroDeCPUs;
	private int capacidadeDeRAM;
	private int capacidadeDeHD;

	public Computador() {
	}

	public Computador(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public short getNumeroDeCPUs() {
		return numeroDeCPUs;
	}

	public void setNumeroDeCPUs(short numeroDeCPUs) {
		this.numeroDeCPUs = numeroDeCPUs;
	}

	public int getCapacidadeDeRAM() {
		return capacidadeDeRAM;
	}

	public void setCapacidadeDeRAM(int capacidadeDeRAM) {
		this.capacidadeDeRAM = capacidadeDeRAM;
	}

	public int getCapacidadeDeHD() {
		return capacidadeDeHD;
	}

	public void setCapacidadeDeHD(int capacidadeDeHD) {
		this.capacidadeDeHD = capacidadeDeHD;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Computador))
			return false;
		Computador that = (Computador) obj;
		return this.getNome().equals(that.getNome());
	}

}
