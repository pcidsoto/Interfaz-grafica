package com.ugm.dbexample.use_cases.interactors;


import com.ugm.dbexample.entities.Empresa;
import com.ugm.dbexample.use_cases.ports.output.IEmpresa;
import com.ugm.dbexample.exceptions.NonexistentEntityException;
import com.ugm.dbexample.use_cases.ports.input.IEmpresaService;
import java.util.List;

public class EmpresaServiceImpl implements IEmpresaService {

    private final IEmpresa empresaRepository;

    public EmpresaServiceImpl(IEmpresa empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Override
    public Empresa crearEmpresa(Empresa empresa) {
        empresaRepository.create(empresa);
        return empresa;
    }

    @Override
    public Empresa obtenerEmpresaPorId(Integer id) {
        return empresaRepository.findEmpresa(id);
    }

    @Override
    public List<Empresa> obtenerTodasLasEmpresas() {
        return empresaRepository.findEmpresaEntities();
    }

    @Override
    public Empresa actualizarEmpresa(Empresa empresa) {
        try {
            empresaRepository.edit(empresa);
            return empresa;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la empresa", e);
        }
    }

    @Override
    public void eliminarEmpresa(Integer id) {
        try {
            empresaRepository.destroy(id);
        } catch (NonexistentEntityException e) {
            throw new RuntimeException("La empresa no existe", e);
        }
    }

    @Override
    public List<Empresa> buscarEmpresasPorNombre(String nombre) {
        return empresaRepository.findEmpresaByNombre(nombre);
    }
}
