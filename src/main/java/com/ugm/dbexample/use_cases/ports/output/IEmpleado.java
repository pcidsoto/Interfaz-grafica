package com.ugm.dbexample.use_cases.ports.output;

import com.ugm.dbexample.entities.Empleado;
import com.ugm.dbexample.exceptions.NonexistentEntityException;
import java.util.List;

public interface IEmpleado {
    void create(Empleado empleado);
    Empleado findEmpleado(Long id);
    void edit(Empleado empleado) throws NonexistentEntityException, Exception;
    void destroy(Long id) throws NonexistentEntityException;
    List<Empleado> findEmpleadoEntities();
    List<Empleado> findEmpleadoEntities(int maxResults, int firstResult);
    int getEmpleadoCount();
    List<Empleado> getEmpleadosByEmpresaId(Integer id);
    List<Empleado> getEmpleadosByDepartamentoId(Integer id);

}
