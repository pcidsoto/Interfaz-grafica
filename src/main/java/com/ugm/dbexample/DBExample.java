package com.ugm.dbexample;

import com.ugm.dbexample.persistence.*;
import com.ugm.dbexample.use_cases.interactors.DepartamentoServiceImpl;
import com.ugm.dbexample.use_cases.interactors.EmpleadoServiceImpl;
import com.ugm.dbexample.use_cases.interactors.EmpresaServiceImpl;
import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import com.ugm.dbexample.use_cases.ports.input.IEmpleadoService;
import com.ugm.dbexample.views.MainView;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.SwingUtilities;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;


public class DBExample {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistenceUnit");
        
        EmpresaJpaController empresaRepository = new EmpresaJpaController(emf);
        EmpleadoJpaController empleadoRepository = new EmpleadoJpaController(emf);
        DepartamentoJpaController departamentoRepository = new DepartamentoJpaController(emf);
      
        // Instancias de servcios.
        IEmpresaService empresaService = new EmpresaServiceImpl(empresaRepository);
        IDepartamentoService departamentoService = new DepartamentoServiceImpl(departamentoRepository);
        IEmpleadoService empleadoService = new EmpleadoServiceImpl(empleadoRepository);
        
        SwingUtilities.invokeLater(() -> {
            MainView mainView = new MainView(
                    empresaService, 
                    departamentoService,
                    empleadoService,
                    emf
            );
            mainView.setVisible(true);
        });
    }
}
