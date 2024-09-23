package com.ugm.dbexample.views;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.entities.Empleado;
import com.ugm.dbexample.entities.Empresa;
import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import com.ugm.dbexample.use_cases.ports.input.IEmpleadoService;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class EmpleadosView extends JPanel{
    private final IEmpleadoService empleadoService;
    private final IDepartamentoService departamentoService;
    private final IEmpresaService empresaService;
    
    private Runnable onBackAction;
    private Integer empresaId;
    
    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<Departamento> comboDepartamento;
    
    
    public EmpleadosView(
            IEmpleadoService empleadoService,
            IDepartamentoService departamentoService, 
            IEmpresaService empresaService,
            MainView mainView) {
       
        this.empresaService = empresaService;
        this.departamentoService = departamentoService;
        this.empleadoService = empleadoService;
        
        setLayout(new BorderLayout());
        
        // Panel de filtros en la parte superior
        JPanel panelFiltros = crearPanelFiltros();
        add(panelFiltros, BorderLayout.NORTH);
        
        // Panel de botones a la izquierda
        JPanel panelBotones = crearPanelBotones();
        add(panelBotones, BorderLayout.WEST);
        
        // Tabla de empleados en el centro
        crearTablaEmpleados();
        JScrollPane scrollPane = new JScrollPane(tablaEmpleados);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void mostrarFormularioEmpleado(Empleado empleado) {
        List<Departamento> departamentos = departamentoService.obtenerDepartamentosPorEmpresaId(this.empresaId);

        EmpleadoFormModalView formModal = new EmpleadoFormModalView(departamentos);
        formModal.setEmpleado(empleado); // null para nuevo empleado

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Empleado", true);
        dialog.getContentPane().add(formModal);

        formModal.getBtnSubmit().addActionListener(e -> {
            try {
                Empleado empleadoActualizado = formModal.getEmpleado();
                if (empleado == null) {
                    Empresa empresa = empresaService.obtenerEmpresaPorId(this.empresaId);
                    empleadoActualizado.setEmpresa(empresa);
                    empleadoService.crearEmpleado(empleadoActualizado);
                } else {
                    empleadoService.actualizarEmpleado(empleadoActualizado);
                }
                dialog.dispose();
                // Actualizar la vista de empleados
                // actualizarTablaEmpleados();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formModal.getBtnCancelar().addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        txtBuscar = new JTextField(20);
        panel.add(new JLabel("Buscar:"));
        panel.add(txtBuscar);
        
        comboDepartamento = new JComboBox<>();
        panel.add(new JLabel("Departamento:"));
        panel.add(comboDepartamento);
        
        // Añadir listeners
        txtBuscar.addActionListener(e -> buscarEmpleado());
        comboDepartamento.addActionListener(e -> filtrarEmpleados());
        
        
        return panel;
    }
    
    private JPanel crearPanelBotones() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5));
        
        JButton btnAgregar = new JButton("Agregar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVolver = new JButton("Volver a Empresas");
        
        panel.add(btnAgregar);
        panel.add(btnActualizar);
        panel.add(btnEliminar);
        panel.add(btnVolver);
        
        // Añadir listeners
        btnAgregar.addActionListener(e -> {
            mostrarFormularioEmpleado(null);
            cargarEmpleados();
        });
        btnActualizar.addActionListener(e -> {
            int filaSeleccionada = tablaEmpleados.getSelectedRow();
            if (filaSeleccionada >= 0) {
                Long empleadoId = (Long) tablaEmpleados.getValueAt(filaSeleccionada, 0);
                Empleado empleado = empleadoService.obtenerEmpleadoPorId(empleadoId);
                mostrarFormularioEmpleado(empleado);
                cargarEmpleados();
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un empleado para actualizar.");
            }
        });
        btnEliminar.addActionListener(e -> eliminarEmpleadoSeleccionado());
        btnVolver.addActionListener(e -> {
            if (onBackAction != null) onBackAction.run();
        });
        
        return panel;
    }
    
    private void crearTablaEmpleados() {
        String[] columnas = {"ID", "Nombre", "Apellidos", "Departamento", "Fecha Nacimiento", "Salario"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaEmpleados = new JTable(modeloTabla);
        
        // Configurar ordenamiento
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(modeloTabla);
        tablaEmpleados.setRowSorter(sorter);
    }
    
    private void cargarEmpleados() {
        modeloTabla.setRowCount(0);
        List<Empleado> empleados = empleadoService.obtenerEmpleadosPorEmpresaId(empresaId);
        for (Empleado empleado : empleados) {
            modeloTabla.addRow(new Object[]{
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellidos(),
                empleado.getDepartamento().getNombre(),
                empleado.getFechaNacimiento(),
                empleado.getSalarioMensual()
            });
        }
    }
    
    private void eliminarEmpleadoSeleccionado() {
        try{
            int filaSeleccionada = tablaEmpleados.getSelectedRow();
            if (filaSeleccionada >= 0) {
                Long empleadoId = (Long) tablaEmpleados.getValueAt(filaSeleccionada, 0);
                Empleado empleado = empleadoService.obtenerEmpleadoPorId(empleadoId);
                int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este colaborador?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    empleadoService.eliminarEmpleado(empleado.getId());
                    cargarEmpleados();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un empleado para eliminar.");
            }
        
        }catch(Exception e){
        
        }
    }
    
    private void filtrarEmpleados() {
        // Implementar lógica de filtrado
        int index = comboDepartamento.getSelectedIndex();
  
        if (index == 0){
            System.out.println("Filtrando por todos los departamentos");
            cargarEmpleados();
            return;
        }
        
        Departamento dpto = comboDepartamento.getItemAt(index);
        if (dpto == null) return;
        
        System.out.println("Filtrando empleados po " + dpto.getNombre() + " " + dpto.getId());
        modeloTabla.setRowCount(0);
        List<Empleado> empleados = empleadoService.obtenerEmpleadosPorDepartamento(dpto.getId().intValue());
        for (Empleado empleado : empleados) {
            modeloTabla.addRow(new Object[]{
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellidos(),
                empleado.getDepartamento().getNombre(),
                empleado.getFechaNacimiento(),
                empleado.getSalarioMensual()
            });
        }  
    }
    
    private void buscarEmpleado() {
        
        String textoBusqueda = txtBuscar.getText().toLowerCase().trim();
        System.out.println("Buscando Empleado: " + textoBusqueda);

        modeloTabla.setRowCount(0);
        List<Empleado> empleados = empleadoService.obtenerTodosLosEmpleados();

        for (Empleado empleado : empleados) {
            if (empleado.getNombre().toLowerCase().contains(textoBusqueda) ||
                empleado.getApellidos().toLowerCase().contains(textoBusqueda)) {

                modeloTabla.addRow(new Object[]{
                    empleado.getId(),
                    empleado.getNombre(),
                    empleado.getApellidos(),
                    empleado.getDepartamento().getNombre(),
                    empleado.getFechaNacimiento(),
                    empleado.getSalarioMensual()
                });
            }
        }
    }
    
    // Lista de departamentos para utilizar como filtro.
    private void cargarDepartamentos() {
        comboDepartamento.removeAllItems();
        Departamento all = new Departamento();
        all.setId(-1L);
        all.setNombre("Todos los departamentos");
        comboDepartamento.addItem(all);
        List<Departamento> departamentos = departamentoService.obtenerDepartamentosPorEmpresaId(this.empresaId);
        for (Departamento departamento : departamentos) {
            comboDepartamento.addItem(departamento);
        }
    }
    
    public void setOnBackAction(Runnable action) {
        this.onBackAction = action;
    }
    
    public void setEmpresaId(Integer empresaId) {
        System.out.println("Configurando empresa: " + empresaId + " para agregar empleados");
        this.empresaId = empresaId;
        cargarEmpleados();
        cargarDepartamentos();
    }
}
