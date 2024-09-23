package com.ugm.dbexample.views;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.entities.Empresa;
import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.util.List;

public class DepartamentoView extends JPanel {
    private final IDepartamentoService departamentoService;
    private final IEmpresaService empresaService;
    
    private JTable tablaDepartamentos;
    private DefaultTableModel modeloTabla;
    private Integer empresaId;
    private Runnable onBackAction;
    private JLabel idLabel;
    private JTextField nombreField, descripcionField;
    
    // Declaración de los botones
    private JButton btnAgregar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnVolverEmpresas;

    public DepartamentoView(
            IDepartamentoService departamentoService, 
            IEmpresaService empresaService,
            MainView mainView) {
        this.departamentoService = departamentoService;
        this.empresaService = empresaService;
        setLayout(new BorderLayout());

        // Panel superior con título
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(new JLabel("Departamentos"));
        add(topPanel, BorderLayout.NORTH);

        // Panel principal con diseño BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel de botones en el WEST
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Creación de los botones
        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        btnVolverEmpresas = new JButton("Volver a Empresas");
        
        // Agregar los botones al panel
        buttonPanel.add(btnAgregar);
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnEliminar);
        buttonPanel.add(btnLimpiar);
        buttonPanel.add(btnVolverEmpresas);
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        
        btnAgregar.addActionListener(e -> {
            agregarDepartamento();
            cargarDepartamentos();
            limpiarFormulario();
        });
        
        btnEliminar.addActionListener(e -> {
            eliminarDepartamento();
            cargarDepartamentos();
            limpiarFormulario();
        });
        
        btnLimpiar.addActionListener(e -> {
            limpiarFormulario();
        });
        
        btnActualizar.addActionListener(e -> {
            actualizarDepartamento();
            cargarDepartamentos();
            limpiarFormulario();
        });
        
        btnVolverEmpresas.addActionListener(e -> {
            if (onBackAction != null) {
                onBackAction.run();
            }
        });
        
        

        // Panel central con campos de entrada y tabla
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Campos de entrada
        JPanel inputPanel = new JPanel(new GridLayout(3, 4, 6, 6));
        inputPanel.add(new JLabel("ID:"));
        idLabel = new JLabel();
        inputPanel.add(idLabel);
        inputPanel.add(new JLabel("Nombre:"));
        nombreField = new JTextField(20);
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Descripción:"));
        descripcionField = new JTextField(20);
        inputPanel.add(descripcionField);
        
        centerPanel.add(inputPanel, BorderLayout.NORTH);

        // Tabla de departamentos
        String[] columnas = {"ID", "Nombre", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaDepartamentos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaDepartamentos);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Actualizar Departamento.
        // Listener para selección en la tabla
        tablaDepartamentos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDepartamentoSeleccionado();
            }
        });
        
        // panel principal.
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    public void setEmpresaId(Integer empresaId) {
        System.out.println("Configurando empresa: " + empresaId + " para agregar departamentos");
        this.empresaId = empresaId;
        cargarDepartamentos();
    }

    private void cargarDepartamentos() {
        System.out.println("Actualizando lista de departamentos");
        modeloTabla.setRowCount(0);
        List<Departamento> departamentos = departamentoService.obtenerDepartamentosPorEmpresaId(this.empresaId);
        for (Departamento departamento : departamentos) {
            modeloTabla.addRow(new Object[]{
                departamento.getId(),
                departamento.getNombre(),
                departamento.getDescripcion()
            });
        }
    }

    private void agregarDepartamento(){
        System.out.println("Agregando un departamento nuevo");
        Empresa empresa = this.empresaService.obtenerEmpresaPorId(this.empresaId);
      
        try{
           
            String nombre = nombreField.getText();
            if (!nombre.isEmpty()) {
                Departamento nuevoDepartamento = new Departamento();
                nuevoDepartamento.setNombre(nombre);
                nuevoDepartamento.setDescripcion("Default");
                nuevoDepartamento.setEmpresa(empresa);
                departamentoService.create(nuevoDepartamento);
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre del departamento");
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(this, "Error al agregar departamento: " + e.getMessage());
        }
    }
    
    private void actualizarDepartamento() {
        try {
            Long id = Long.valueOf(idLabel.getText());
            String nombre = nombreField.getText();
            String descripcion = descripcionField.getText();
            if (!nombre.isEmpty()) {
                Departamento departamento = departamentoService.findDepartamento(id);
                if (departamento != null) {
                    departamento.setNombre(nombre);
                    departamento.setDescripcion(descripcion);
                    departamentoService.edit(departamento);
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la empresa con el ID especificado");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre de la empresa");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la empresa: " + e.getMessage());
        }
    }
    
    private void eliminarDepartamento(){
        Long id = Long.valueOf(idLabel.getText());
        try{
            departamentoService.destroy(id);
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, "Error al eliminar departamento " + e.getMessage());
        }
    }
    
    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }
    
    private void limpiarFormulario() {
        idLabel.setText("");
        nombreField.setText("");
        descripcionField.setText("");
    }
    
     private void cargarDepartamentoSeleccionado() {
        int filaSeleccionada = tablaDepartamentos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            idLabel.setText(tablaDepartamentos.getValueAt(filaSeleccionada, 0).toString());
            nombreField.setText(tablaDepartamentos.getValueAt(filaSeleccionada, 1).toString());
            descripcionField.setText(tablaDepartamentos.getValueAt(filaSeleccionada, 2).toString());
        }
    }
}
