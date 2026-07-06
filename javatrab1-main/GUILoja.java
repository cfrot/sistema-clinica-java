package javatrab1;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GUILoja extends JFrame {
    private JTextField nomeField, cpfField, dataConsultaField;
    private JButton buscarButton, adicionarButton, removerButton, editarButton;
    private JComboBox<String> procedimentoComboBox;
    private BancoDeDados banco;
    private List<Procedimento> procedimentos;

    public GUILoja() {
        super("Clínica - Gerenciamento de Pacientes");
        banco = new BancoDeDados();
        procedimentos = criarListaDeProcedimentos(); // Cria a lista de procedimentos
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo de Nome
        JLabel nomeLabel = new JLabel("Nome:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(nomeLabel, gbc);

        nomeField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        mainPanel.add(nomeField, gbc);

        // Campo de CPF
        JLabel cpfLabel = new JLabel("CPF:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(cpfLabel, gbc);

        cpfField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(cpfField, gbc);

        // Campo de Data de Consulta
        JLabel dataConsultaLabel = new JLabel("Data da Consulta (dd/MM/yy):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(dataConsultaLabel, gbc);

        dataConsultaField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        mainPanel.add(dataConsultaField, gbc);

        // ComboBox de Procedimento
        JLabel procedimentoLabel = new JLabel("Procedimento:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(procedimentoLabel, gbc);

        procedimentoComboBox = new JComboBox<>();
        preencherProcedimentosComboBox(); // Preenche inicialmente com todos os procedimentos
        gbc.gridx = 1;
        gbc.gridy = 3;
        mainPanel.add(procedimentoComboBox, gbc);

        // Painel de botões de ação
        JPanel panelAcoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        buscarButton = new JButton("Buscar");
        buscarButton.addActionListener(e -> buscarPaciente());
        panelAcoes.add(buscarButton);

        adicionarButton = new JButton("Adicionar");
        adicionarButton.addActionListener(e -> adicionarPaciente());
        panelAcoes.add(adicionarButton);

        removerButton = new JButton("Remover");
        removerButton.addActionListener(e -> removerPaciente());
        panelAcoes.add(removerButton);

        editarButton = new JButton("Editar");
        editarButton.addActionListener(e -> editarPaciente());
        panelAcoes.add(editarButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        mainPanel.add(panelAcoes, gbc);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private List<Procedimento> criarListaDeProcedimentos() {
        List<Procedimento> lista = new ArrayList<>();
        lista.add(new Procedimento("Tratamento de estrias", 100.0));
        lista.add(new Procedimento("Drenagem linfática", 80.0));
        lista.add(new Procedimento("Preenchimento labial", 150.0));
        lista.add(new Procedimento("Peeling químico", 200.0));
        lista.add(new Procedimento("Limpeza de pele", 60.0));
        lista.add(new Procedimento("Tratamento de varizes", 80.0));
        return lista;
    }

    private void preencherProcedimentosComboBox() {
        procedimentoComboBox.removeAllItems();
        for (Procedimento procedimento : procedimentos) {
            procedimentoComboBox.addItem(procedimento.getNome());
        }
    }

    private void atualizarProcedimentosComboBox(List<Procedimento> procedimentosPaciente) {
        procedimentoComboBox.removeAllItems();

        // Adiciona o procedimento do paciente como o primeiro item da ComboBox
        for (Procedimento proc : procedimentosPaciente) {
            procedimentoComboBox.addItem(proc.getNome() + " (R$ " + proc.getValor() + ")");
        }

        // Adiciona todos os procedimentos disponíveis em seguida
        for (Procedimento procedimento : procedimentos) {
            if (procedimentosPaciente.stream().noneMatch(p -> p.getNome().equals(procedimento.getNome()))) {
                procedimentoComboBox.addItem(procedimento.getNome());
            }
        }
    }

    private String formatarData(Date data) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(data);
    }

    private Date converterStringParaData(String dataStr) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        java.util.Date utilDate = sdf.parse(dataStr);
        return new Date(utilDate.getTime());
    }

    private void buscarPaciente() {
        String cpf = cpfField.getText().trim();
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um CPF para buscar.");
            return;
        }

        Paciente paciente = banco.buscarPaciente(cpf);
        if (paciente == null) {
            JOptionPane.showMessageDialog(this, "Paciente não encontrado.");
        } else {
            nomeField.setText(paciente.getNome());
            dataConsultaField.setText(formatarData(paciente.getDataConsulta())); // Formata a data 

            // Combobox atualizada
            atualizarProcedimentosComboBox(paciente.getProcedimentos());

            JOptionPane.showMessageDialog(this, "Paciente encontrado:\n" + paciente.toString());
        }
    }

    private void adicionarPaciente() {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String dataConsultaStr = dataConsultaField.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || dataConsultaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }
        if (banco.pacienteExiste(cpf)){
        	JOptionPane.showMessageDialog(this,"Já existe este paciente nos registros");
        	return;
        }
        

        try {
            Date dataConsulta = converterStringParaData(dataConsultaStr); // Converte a data do formato dd/MM/aa
            Paciente paciente = new Paciente(nome, cpf, dataConsulta);

            String procedimentoNome = (String) procedimentoComboBox.getSelectedItem();
            Procedimento procedimentoSelecionado = procedimentos.stream()
                    .filter(p -> p.getNome().equals(procedimentoNome))
                    .findFirst()
                    .orElse(null);

            if (procedimentoSelecionado != null) {
                paciente.adicionarProcedimento(procedimentoSelecionado);
            }

            banco.registrarPacienteNoBanco(paciente);
            JOptionPane.showMessageDialog(this, "Paciente adicionado com sucesso!");
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data de consulta inválida. Use o formato dd/MM/yy.");
        }
    }

    private void removerPaciente() {
        String cpf = cpfField.getText().trim();
        if (cpf.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um CPF para remover.");
            return;
        }

        banco.removerPaciente(cpf);
        JOptionPane.showMessageDialog(this, "Paciente removido com sucesso.");
    }

    private void editarPaciente() {
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String dataConsultaStr = dataConsultaField.getText().trim();

        if (nome.isEmpty() || cpf.isEmpty() || dataConsultaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.");
            return;
        }

        if (!banco.pacienteExiste(cpf)) {
            JOptionPane.showMessageDialog(this, "Paciente não encontrado para edição.");
            return;
        }

        try {
            Date dataConsulta = converterStringParaData(dataConsultaStr); // Converte a data do formato dd/MM/aa
            Paciente paciente = new Paciente(nome, cpf, dataConsulta);

            banco.removerPaciente(cpf);
            banco.registrarPacienteNoBanco(paciente);
            JOptionPane.showMessageDialog(this, "Paciente editado com sucesso!");
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data de consulta inválida. Use o formato dd/mm/aa.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUILoja());
    }
}
