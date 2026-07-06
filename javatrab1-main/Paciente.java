package javatrab1;

import java.sql.Date;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class Paciente extends Pessoa { 
    private Date dataConsulta; 
    private ArrayList<Procedimento> procedimentos; // Lista de procedimentos

    public Paciente(String nome, String cpf, Date dataConsulta) {
        super(nome, cpf); // Chama o construtor da superclasse
        this.dataConsulta = dataConsulta;
        this.procedimentos = new ArrayList<>(); // Inicializa a lista de procedimentos
    }

    public Date getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(Date dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public ArrayList<Procedimento> getProcedimentos() {
        return procedimentos;
    }

    public void adicionarProcedimento(Procedimento procedimento) {
        this.procedimentos.add(procedimento);
    }

    private String formatarData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(data);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append("\n") // Chama o toString da classe Pessoa
          .append("Data da Consulta: ").append(formatarData(dataConsulta)).append("\n") // Formata a data
          .append("Procedimentos: ");
        
        for (Procedimento p : procedimentos) {
            sb.append("\n - ").append(p.toString());
        }
        
        return sb.toString();
    }
}
