package com.ugm.dbexample.views;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.entities.Empleado;
import com.ugm.dbexample.entities.Empresa;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;


public class EmpleadoFormModalView extends JPanel{
    private JLabel lblId;
    private JTextField txtNombre, txtApellidos, txtSalarioMensual;
    private JComboBox<Departamento> comboDepartamento;
    private JButton btnSubmit, btnCancelar;
    private JSpinner spinnerFechaNacimiento;

    private Empleado empleadoActual;
    
    public EmpleadoFormModalView(List<Departamento> departamentos) {
        setLayout(new BorderLayout());
        initComponents(departamentos);
    }
    
    private void initComponents(List<Departamento> departamentos) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ID (solo lectura)
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        lblId = new JLabel();
        formPanel.add(lblId, gbc);

        // Nombre
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombre = new JTextField(20);
        formPanel.add(txtNombre, gbc);

        // Apellidos
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Apellidos:"), gbc);
        gbc.gridx = 1;
        txtApellidos = new JTextField(20);
        formPanel.add(txtApellidos, gbc);

        // Fecha de Nacimiento
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Fecha de Nacimiento:"), gbc);
        gbc.gridx = 1;
        spinnerFechaNacimiento = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinnerFechaNacimiento, "yyyy-MM-dd");
        spinnerFechaNacimiento.setEditor(dateEditor);
        formPanel.add(spinnerFechaNacimiento, gbc);

        // Salario Mensual
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Salario Mensual:"), gbc);
        gbc.gridx = 1;
        txtSalarioMensual = new JTextField(10);
        formPanel.add(txtSalarioMensual, gbc);

        // Departamento
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Departamento:"), gbc);
        gbc.gridx = 1;
        comboDepartamento = new JComboBox<>(departamentos.toArray(new Departamento[0]));
        formPanel.add(comboDepartamento, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        btnSubmit = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnSubmit);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void setEmpleado(Empleado empleado) {
        this.empleadoActual = empleado;
        if (empleado != null) {
            lblId.setText(empleado.getId() != null ? empleado.getId().toString() : "");
            txtNombre.setText(empleado.getNombre());
            txtApellidos.setText(empleado.getApellidos());
            txtSalarioMensual.setText(empleado.getSalarioMensual().toString());
            comboDepartamento.setSelectedItem(empleado.getDepartamento());
            spinnerFechaNacimiento.setValue(Date.from(empleado.getFechaNacimiento().atStartOfDay(ZoneId.systemDefault()).toInstant()));

        } else {
            spinnerFechaNacimiento.setValue(new Date()); // Fecha actual por defecto
            limpiarCampos();
        }
    }

    public Empleado getEmpleado() throws Exception {
        Empleado empleado = empleadoActual != null ? empleadoActual : new Empleado();
        empleado.setNombre(txtNombre.getText());
        empleado.setApellidos(txtApellidos.getText());
        try {
            Date selectedDate = (Date) spinnerFechaNacimiento.getValue();
            empleado.setFechaNacimiento(selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            
        } catch (DateTimeParseException e) {
            throw new Exception("Formato de fecha inválido. Use YYYY-MM-DD.");
        }
        try {
            empleado.setSalarioMensual(new BigDecimal(txtSalarioMensual.getText()));
        } catch (NumberFormatException e) {
            throw new Exception("Salario inválido. Ingrese un número válido.");
        }
        empleado.setDepartamento((Departamento) comboDepartamento.getSelectedItem());
        return empleado;
    }

    private void limpiarCampos() {
        lblId.setText("");
        txtNombre.setText("");
        txtApellidos.setText("");
        spinnerFechaNacimiento.setValue(new Date());
        txtSalarioMensual.setText("");
        comboDepartamento.setSelectedIndex(-1);
    }

    public JButton getBtnSubmit() {
        return btnSubmit;
    }

    public JButton getBtnCancelar() {
        return btnCancelar;
    }
}
