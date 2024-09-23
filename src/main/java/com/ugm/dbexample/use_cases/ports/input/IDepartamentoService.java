package com.ugm.dbexample.use_cases.ports.input;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.exceptions.NonexistentEntityException;
import java.util.List;

public interface IDepartamentoService {
    void create(Departamento departamento);
    Departamento findDepartamento(Long id);
    void edit(Departamento departamento) throws NonexistentEntityException, Exception;
    void destroy(Long id) throws NonexistentEntityException;
    List<Departamento> findDepartamentoEntities();
    List<Departamento> findDepartamentoEntities(int maxResults, int firstResult);
    int getDepartamentosCount();
    List<Departamento> obtenerDepartamentosPorEmpresaId(Integer id);
}
