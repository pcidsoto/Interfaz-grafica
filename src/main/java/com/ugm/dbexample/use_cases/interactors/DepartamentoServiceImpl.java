package com.ugm.dbexample.use_cases.interactors;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.use_cases.ports.output.IDepartamento;
import com.ugm.dbexample.exceptions.NonexistentEntityException;
import com.ugm.dbexample.use_cases.ports.input.IDepartamentoService;
import java.util.List;

public class DepartamentoServiceImpl implements IDepartamentoService {
    
    private final IDepartamento departamentoRepository;

    public DepartamentoServiceImpl(IDepartamento repository) {
        this.departamentoRepository = repository;
    }

    @Override
    public void create(Departamento departamento) {
        departamentoRepository.create(departamento);
    }

    @Override
    public Departamento findDepartamento(Long id) {
        return departamentoRepository.findDepartamento(id);
    }

    @Override
    public void edit(Departamento departamento) throws NonexistentEntityException, Exception {
        departamentoRepository.edit(departamento);
    }

    @Override
    public void destroy(Long id) throws NonexistentEntityException {
        departamentoRepository.destroy(id);
    }

    @Override
    public List<Departamento> findDepartamentoEntities() {
        return departamentoRepository.findDepartamentoEntities();
    }

    @Override
    public List<Departamento> findDepartamentoEntities(int maxResults, int firstResult) {
        return departamentoRepository.findDepartamentoEntities(maxResults, firstResult);
    }

    @Override
    public int getDepartamentosCount() {
        return departamentoRepository.getDepartamentoCount();
    }

    @Override
    public List<Departamento> obtenerDepartamentosPorEmpresaId(Long empresaId) {
        try {
            List<Departamento> departamentos = departamentoRepository.getDepartamentosByEmpresaID(empresaId);
            if (departamentos.isEmpty()) {
                // Loggea o maneja el caso de no encontrar departamentos
                System.out.println("No se encontraron departamentos para la empresa con ID: " + empresaId);
            }
            return departamentos;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la empresa", e);
        }
    }
    
}
