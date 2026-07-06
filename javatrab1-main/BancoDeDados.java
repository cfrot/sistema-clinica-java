package javatrab1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BancoDeDados {
    private static final String URL = "jdbc:sqlite:sistemaRegistro.db";

    public BancoDeDados() {
        criarTabelaPaciente();
        criarTabelaProcedimento();
    }

    private void criarTabelaPaciente() {
        String sql = "CREATE TABLE IF NOT EXISTS Paciente (" +
                     "nome TEXT NOT NULL, " +
                     "cpf TEXT PRIMARY KEY, " +
                     "dataConsulta TEXT NOT NULL" + 
                     ");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void criarTabelaProcedimento() {
        String sql = "CREATE TABLE IF NOT EXISTS Procedimento (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "cpfPaciente TEXT NOT NULL," +
                     "nome TEXT NOT NULL," +
                     "valor REAL NOT NULL," +
                     "FOREIGN KEY(cpfPaciente) REFERENCES Paciente(cpf)" +
                     ");";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void registrarPacienteNoBanco(Paciente paciente) {
        String sql = "INSERT INTO Paciente (nome, cpf, dataConsulta) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paciente.getNome());
            pstmt.setString(2, paciente.getCpf());
            pstmt.setString(3, paciente.getDataConsulta().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Procedimento p : paciente.getProcedimentos()) {
            adicionarProcedimentoAoPaciente(paciente.getCpf(), p);
        }
    }

    public List<Paciente> listarPacientes() {
        List<Paciente> pacientes = new ArrayList<>();
        String sql = "SELECT nome, cpf, dataConsulta FROM Paciente";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String nome = rs.getString("nome");
                String cpf = rs.getString("cpf");
                String dataConsulta = rs.getString("dataConsulta");

                Paciente paciente = new Paciente(nome, cpf, Date.valueOf(dataConsulta));
                paciente.getProcedimentos().addAll(buscarProcedimentosDoPaciente(cpf));
                pacientes.add(paciente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pacientes;
    }

    public Paciente buscarPaciente(String cpf) {
        String sql = "SELECT nome, cpf, dataConsulta FROM Paciente WHERE cpf = ?";
        Paciente paciente = null;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpf);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nome = rs.getString("nome");
                String dataConsulta = rs.getString("dataConsulta");

                paciente = new Paciente(nome, cpf, Date.valueOf(dataConsulta));
                paciente.getProcedimentos().addAll(buscarProcedimentosDoPaciente(cpf));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paciente;
    }

    public void removerPaciente(String cpf) {
        String sqlPaciente = "DELETE FROM Paciente WHERE cpf = ?";
        String sqlProcedimentos = "DELETE FROM Procedimento WHERE cpfPaciente = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtPaciente = conn.prepareStatement(sqlPaciente);
             PreparedStatement pstmtProcedimentos = conn.prepareStatement(sqlProcedimentos)) {

            pstmtProcedimentos.setString(1, cpf);
            pstmtProcedimentos.executeUpdate();

            pstmtPaciente.setString(1, cpf);
            pstmtPaciente.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean pacienteExiste(String cpf) {
        String sql = "SELECT COUNT(*) FROM Paciente WHERE cpf = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void adicionarProcedimentoAoPaciente(String cpf, Procedimento procedimento) {
        String sql = "INSERT INTO Procedimento (cpfPaciente, nome, valor) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpf);
            pstmt.setString(2, procedimento.getNome());
            pstmt.setDouble(3, procedimento.getValor());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Procedimento> buscarProcedimentosDoPaciente(String cpf) {
        List<Procedimento> procedimentos = new ArrayList<>();
        String sql = "SELECT nome, valor FROM Procedimento WHERE cpfPaciente = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpf);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String nome = rs.getString("nome");
                double valor = rs.getDouble("valor");
                procedimentos.add(new Procedimento(nome, valor));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return procedimentos;
    }
}
