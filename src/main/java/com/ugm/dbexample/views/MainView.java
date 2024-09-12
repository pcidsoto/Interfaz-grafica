/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ugm.dbexample.views;

// Vista Principal

import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;

public class MainView extends JFrame {
    private IEmpresaService empresaService;
    private IDepartamentoService departamentoService;
    
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainView(IEmpresaService empresaService, IDepartamentoService departamentoService) {
        this.empresaService = empresaService;
        this.departamentoService = departamentoService;

        setTitle("Aplicación de Gestión de Empresas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Crear paneles
        JPanel homePanel = createHomePanel();
        AdminView adminView = new AdminView(empresaService, this);
        DepartamentoView departamentoView = new DepartamentoView(departamentoService, this);
        
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
}