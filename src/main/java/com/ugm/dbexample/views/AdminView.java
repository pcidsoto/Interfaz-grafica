/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ugm.dbexample.views;

import com.ugm.dbexample.entities.Empresa;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;
import javax.swing.table.TableCellRenderer;

public class AdminView extends JPanel {
    private final IEmpresaService empresaService;
    private JTable tablaEmpresas;
    private DefaultTableModel modeloTabla;
    private JTextField txtId, txtNombre;
    private JLabel lblId; 
    private Runnable onHomeAction;
    private VerDepartamentosAction verDepartamentosAction;

    public AdminView(IEmpresaService empresaService, MainView aThis) {
        this.empresaService = empresaService;
        setLayout(new BorderLayout());

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridLayout(2, 2));
        panelFormulario.add(new JLabel("ID:"));
        lblId = new JLabel();  // JLabel para mostrar el ID
        panelFormulario.add(lblId);
        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        // Panel de botones
        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnHome = new JButton("Home");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnHome);

        // Tabla de empresas
        String[] columnas = {"ID", "Nombre", "Ver Departamentos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Solo la columna de "Ver Departamentos" es editable
            }
        };
        tablaEmpresas = new JTable(modeloTabla);
        tablaEmpresas.getColumn("Ver Departamentos").setCellRenderer(new ButtonRenderer());
        tablaEmpresas.getColumn("Ver Departamentos").setCellEditor(new ButtonEditor(new JCheckBox()));
        JScrollPane scrollPane = new JScrollPane(tablaEmpresas);

        // Agregar componentes al panel
        add(panelFormulario, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Agregar listeners a los botones
        btnAgregar.addActionListener(e -> agregarEmpresa());
        btnActualizar.addActionListener(e -> actualizarEmpresa());
        btnEliminar.addActionListener(e -> eliminarEmpresa());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnHome.addActionListener(e -> {
            if (onHomeAction != null) {
                onHomeAction.run();
            }
        });
        // Cargar datos iniciales
        cargarEmpresas();

        // Listener para selección en la tabla
        tablaEmpresas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarEmpresaSeleccionada();
            }
        });
    }

    private void cargarEmpresas() {
        modeloTabla.setRowCount(0);
        List<Empresa> empresas = empresaService.obtenerTodasLasEmpresas();
        for (Empresa empresa : empresas) {
             modeloTabla.addRow(new Object[]{
                empresa.getId(),
                empresa.getNombre(),
                "Ver Departamentos"
            });
        }
    }
    
    // Interfaz funcional para manejar la acción de ver departamentos
    public interface VerDepartamentosAction {
        void verDepartamentos(Long empresaId);
    }
    
    // Renderer para el botón en la tabla
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
     // Editor para el botón en la tabla
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed && verDepartamentosAction != null) {
                int row = tablaEmpresas.getSelectedRow();
                int empresaId = (int) tablaEmpresas.getValueAt(row, 0);
                verDepartamentosAction.verDepartamentos(Long.valueOf(empresaId));
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void agregarEmpresa() {
        String nombre = txtNombre.getText();
        if (!nombre.isEmpty()) {
            Empresa nuevaEmpresa = new Empresa();
            nuevaEmpresa.setNombre(nombre);
            empresaService.crearEmpresa(nuevaEmpresa);
            cargarEmpresas();
            limpiarFormulario();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre de la empresa");
        }
    }

    private void actualizarEmpresa() {
        try {
            Integer id = Integer.valueOf(lblId.getText());
            String nombre = txtNombre.getText();
            if (!nombre.isEmpty()) {
                Empresa empresa = empresaService.obtenerEmpresaPorId(id);
                if (empresa != null) {
                    empresa.setNombre(nombre);
                    empresaService.actualizarEmpresa(empresa);
                    cargarEmpresas();
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la empresa con el ID especificado");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese el nombre de la empresa");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la empresa: " + e.getMessage());
        }
    }

    private void eliminarEmpresa() {
        try {
            Integer id = Integer.valueOf(lblId.getText());
            int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta empresa?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirmacion == JOptionPane.YES_OPTION) {
                empresaService.eliminarEmpresa(id);
                cargarEmpresas();
                limpiarFormulario();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la empresa: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        lblId.setText("");
        txtNombre.setText("");
    }

    private void cargarEmpresaSeleccionada() {
        int filaSeleccionada = tablaEmpresas.getSelectedRow();
        if (filaSeleccionada >= 0) {
            lblId.setText(tablaEmpresas.getValueAt(filaSeleccionada, 0).toString());
            txtNombre.setText(tablaEmpresas.getValueAt(filaSeleccionada, 1).toString());
        }
    }
    
    // Método para establecer la acción de volver al Home
    public void setOnHomeAction(Runnable action) {
        this.onHomeAction = action;
    }
    
    // Setter para la acción de ver departamentos
    public void setVerDepartamentosAction(VerDepartamentosAction action) {
        this.verDepartamentosAction = action;
    }
}