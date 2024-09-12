package com.ugm.dbexample.use_cases.ports.input;

import com.ugm.dbexample.entities.Empresa;
import java.util.List;

public interface IEmpresaService {
    Empresa crearEmpresa(Empresa empresa);
    Empresa obtenerEmpresaPorId(Integer id);
    List<Empresa> obtenerTodasLasEmpresas();
    Empresa actualizarEmpresa(Empresa empresa);
    void eliminarEmpresa(Integer id);
    List<Empresa> buscarEmpresasPorNombre(String nombre);
}
