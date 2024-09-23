package com.ugm.dbexample.views;

// Vista Principal

import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import com.ugm.dbexample.use_cases.ports.input.IEmpleadoService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;
import jakarta.persistence.EntityManagerFactory;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainView extends JFrame {
    private EntityManagerFactory emf;
    
    private IEmpresaService empresaService;
    private IDepartamentoService departamentoService;
    private IEmpleadoService empleadoService;
    
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainView(
            IEmpresaService empresaService, 
            IDepartamentoService departamentoService,
            IEmpleadoService empleadoService,
            EntityManagerFactory emf
            ) {
        this.empresaService = empresaService;
        this.departamentoService = departamentoService;
        this.empleadoService = empleadoService;
        this.emf = emf;

        setTitle("Aplicación de Gestión de Empresas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Crear paneles
        JPanel homePanel = createHomePanel();
        EmpresaView adminView = new EmpresaView(empresaService, this);
        DepartamentoView departamentoView = new DepartamentoView(
                departamentoService,empresaService ,this);
        
        // Establecer la acción de volver al Home
        adminView.setOnHomeAction(() -> cardLayout.show(mainPanel, "Home"));
        adminView.setVerDepartamentosAction(empresaId -> {
            departamentoView.setEmpresaId(empresaId);
            cardLayout.show(mainPanel, "Departamentos");
        });
        departamentoView.setOnBackAction(() -> cardLayout.show(mainPanel, "Admin"));

        // Añadir paneles al mainPanel
        mainPanel.add(homePanel, "Home");
        mainPanel.add(adminView, "Admin");
        mainPanel.add(departamentoView, "Departamentos");

        // Añadir mainPanel al frame
        add(mainPanel);
        
         // Agregar un listener para el cierre de la ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton adminButton = new JButton("Administrar Empresas");
        adminButton.addActionListener(e -> cardLayout.show(mainPanel, "Admin"));
        
        panel.add(new JLabel("Bienvenido a la Aplicación de Gestión de Empresas", SwingConstants.CENTER), BorderLayout.CENTER);
        panel.add(adminButton, BorderLayout.SOUTH);
        
        return panel;
    }

    public void showHome() {
        cardLayout.show(mainPanel, "Home");
    }
    
    @Override
    public void dispose() {
        System.out.println("Cerrando conexión a DB");
        if (emf != null && emf.isOpen()) {
            emf.close();
            System.out.println("EntityManagerFactory cerrado.");
        }
        super.dispose();
    }
}