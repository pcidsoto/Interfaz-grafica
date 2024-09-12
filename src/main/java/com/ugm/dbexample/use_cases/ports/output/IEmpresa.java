package com.ugm.dbexample.use_cases.ports.output;

import com.ugm.dbexample.entities.Empresa;
import com.ugm.dbexample.exceptions.NonexistentEntityException;
import java.util.List;

public interface IEmpresa {

    void create(Empresa empresa);

    void edit(Empresa empresa) throws NonexistentEntityException, Exception;

    void destroy(int id) throws NonexistentEntityException;

    List<Empresa> findEmpresaEntities();

    List<Empresa> findEmpresaEntities(int maxResults, int firstResult);

    Empresa findEmpresa(int id);

    int getEmpresaCount();
    
    List<Empresa> findEmpresaByNombre(String nombre);
}