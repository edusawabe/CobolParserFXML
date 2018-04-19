package cobolparser;

import javafx.beans.property.SimpleStringProperty;

public class ProgramsTableLine {
	SimpleStringProperty id;
	SimpleStringProperty arquivo;
	SimpleStringProperty nomePrograma;
	SimpleStringProperty status;

	public ProgramsTableLine() {
		id = new SimpleStringProperty();
		arquivo = new SimpleStringProperty();
		nomePrograma = new SimpleStringProperty();
		status = new SimpleStringProperty();
	}

	public String getId() {
		return id.get();
	}
	public void setId(String id) {
		this.id.set(id);
	}
	public String getArquivo() {
		return arquivo.get();
	}
	public void setArquivo(String arquivo) {
		this.arquivo.set(arquivo);
	}
	public String getNomePrograma() {
		return nomePrograma.get();
	}
	public void setNomePrograma(String nomePrograma) {
		this.nomePrograma.set(nomePrograma);
	}
	public String getStatus() {
		return status.get();
	}
	public void setStatus(String status) {
		this.status.set(status);
	}
}
