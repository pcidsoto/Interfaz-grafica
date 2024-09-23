package com.ugm.dbexample.use_cases.ports.input;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.entities.Empleado;
import com.ugm.dbexample.entities.Empresa;
import java.util.List;

public interface IEmpleadoService {
    // Métodos CRUD básicos
    Empleado crearEmpleado(Empleado empleado);
    Empleado obtenerEmpleadoPorId(Long id);
    Empleado actualizarEmpleado(Empleado empleado) throws Exception;
    void eliminarEmpleado(Long id) throws Exception;
    
    // Métodos de búsqueda y listado
    List<Empleado> obtenerTodosLosEmpleados();
    int obtenerTotalEmpleados();
    
    // Métodos específicos de negocio
    List<Empleado> buscarEmpleadosPorNombre(String nombre);
    List<Empleado> buscarEmpleadosPorApellidos(String apellidos);
    List<Empleado> obtenerEmpleadosPorDepartamento(Integer departamentoId);
    List<Empleado> obtenerEmpleadosPorEmpresaId(Integer id);
 
    // Métodos para manejo de fechas
    int calcularEdad(Empleado empleado);
    
    // Métodos para asignación y cambio de departamento/empresa
    void asignarDepartamento(Empleado empleado, Departamento departamento);
    void cambiarEmpresa(Empleado empleado, Empresa empresa);
}
