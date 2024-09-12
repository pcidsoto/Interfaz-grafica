package com.ugm.dbexample.views;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import java.awt.BorderLayout;

import javax.swing.JTable;

import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.util.List;

public class DepartamentoView extends JPanel {
    private final IDepartamentoService departamentoService;
    
    private JTable tablaDepartamentos;
    private DefaultTableModel modeloTabla;
    private Long empresaId;
    private Runnable onBackAction;

    public DepartamentoView(IDepartamentoService departamentoService, MainView aThis) {
        this.departamentoService = departamentoService;
        setLayout(new BorderLayout());

        // Crear tabla de departamentos
        String[] columnas = {"ID", "Nombre", "Descripción"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaDepartamentos = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaDepartamentos);

        add(scrollPane, BorderLayout.CENTER);

        // Botón para volver a la vista de empresas
        JButton btnVolver = new JButton("Volver a Empresas");
        btnVolver.addActionListener(e -> {
            if (onBackAction != null) {
                onBackAction.run();
            }
        });
        add(btnVolver, BorderLayout.SOUTH);
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
        cargarDepartamentos();
    }

    private void cargarDepartamentos() {
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

    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }
}
