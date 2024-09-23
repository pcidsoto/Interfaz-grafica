package com.ugm.dbexample.views;

import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import com.ugm.dbexample.use_cases.ports.input.IEmpleadoService;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;
import javax.swing.JPanel;

public class EmpleadosView extends JPanel{
    private final IEmpleadoService empleadoService;
    private final IDepartamentoService departamentoService;
    private final IEmpresaService empresaService;
    
    
    public EmpleadosView(
            IEmpleadoService empleadoService,
            IDepartamentoService departamentoService, 
            IEmpresaService empresaService,
            MainView mainView) {
       
        this.empresaService = empresaService;
        this.departamentoService = departamentoService;
        this.empleadoService = empleadoService;
    }

}
