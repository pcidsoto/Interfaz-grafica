package com.ugm.dbexample.use_cases.interactors;

import com.ugm.dbexample.entities.Empleado;
import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.entities.Empresa;
import com.ugm.dbexample.exceptions.NonexistentEntityException;
import com.ugm.dbexample.persistence.EmpleadoJpaController;
import com.ugm.dbexample.use_cases.ports.input.IEmpleadoService;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EmpleadoServiceImpl implements IEmpleadoService {

    private final EmpleadoJpaController empleadoRepository;

    public EmpleadoServiceImpl(EmpleadoJpaController empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public Empleado crearEmpleado(Empleado empleado){
        empleadoRepository.create(empleado);
        return empleado;
    }

    @Override
    public Empleado obtenerEmpleadoPorId(Long id) {
        return empleadoRepository.findEmpleado(id);
    }

    @Override
    public Empleado actualizarEmpleado(Empleado empleado) throws NonexistentEntityException, Exception {
        empleadoRepository.edit(empleado);
        return empleado;
    }

    @Override
    public void eliminarEmpleado(Long id) throws NonexistentEntityException {
        empleadoRepository.destroy(id);
    }

    @Override
    public List<Empleado> obtenerTodosLosEmpleados() {
        return empleadoRepository.findEmpleadoEntities();
    }

    @Override
    public int obtenerTotalEmpleados() {
        return empleadoRepository.getEmpleadoCount();
    }

    @Override
    public List<Empleado> buscarEmpleadosPorNombre(String nombre) {
        return obtenerTodosLosEmpleados().stream()
                .filter(e -> e.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Empleado> buscarEmpleadosPorApellidos(String apellidos) {
        return obtenerTodosLosEmpleados().stream()
                .filter(e -> e.getApellidos().toLowerCase().contains(apellidos.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Empleado> obtenerEmpleadosPorDepartamento(Departamento departamento) {
        return obtenerTodosLosEmpleados().stream()
                .filter(e -> e.getDepartamento().equals(departamento))
                .collect(Collectors.toList());
    }

    @Override
    public List<Empleado> obtenerEmpleadosPorEmpresaId(Integer id) {
        return empleadoRepository.getEmpleadosByEmpresaId(id);
    }

    @Override
    public int calcularEdad(Empleado empleado) {
        return Period.between(empleado.getFechaNacimiento(), LocalDate.now()).getYears();
    }

    @Override
    public void asignarDepartamento(Empleado empleado, Departamento departamento) {
        empleado.setDepartamento(departamento);
        try {
            actualizarEmpleado(empleado);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void cambiarEmpresa(Empleado empleado, Empresa empresa) {
        empleado.setEmpresa(empresa);
        try {
            actualizarEmpleado(empleado);
        } catch (Exception ex) {
            Logger.getLogger(EmpleadoServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}