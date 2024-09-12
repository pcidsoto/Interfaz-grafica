/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ugm.dbexample.persistence;

import com.ugm.dbexample.use_cases.ports.output.IDepartamento;
import com.ugm.dbexample.entities.Departamento;
import java.io.Serializable;
import jakarta.persistence.Query;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import com.ugm.dbexample.entities.Empresa;
import com.ugm.dbexample.entities.Empleado;
import com.ugm.dbexample.exceptions.NonexistentEntityException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author piter
 */
public class DepartamentoJpaController implements  IDepartamento,Serializable {

    public DepartamentoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public void create(Departamento departamento) {
        if (departamento.getEmpleados() == null) {
            departamento.setEmpleados(new ArrayList<Empleado>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Empresa empresa = departamento.getEmpresa();
            if (empresa != null) {
                empresa = em.getReference(empresa.getClass(), empresa.getId());
                departamento.setEmpresa(empresa);
            }
            List<Empleado> attachedEmpleados = new ArrayList<Empleado>();
            for (Empleado empleadosEmpleadoToAttach : departamento.getEmpleados()) {
                empleadosEmpleadoToAttach = em.getReference(empleadosEmpleadoToAttach.getClass(), empleadosEmpleadoToAttach.getId());
                attachedEmpleados.add(empleadosEmpleadoToAttach);
            }
            departamento.setEmpleados(attachedEmpleados);
            em.persist(departamento);
            if (empresa != null) {
                empresa.getDepartamentos().add(departamento);
                empresa = em.merge(empresa);
            }
            for (Empleado empleadosEmpleado : departamento.getEmpleados()) {
                Departamento oldDepartamentoOfEmpleadosEmpleado = empleadosEmpleado.getDepartamento();
                empleadosEmpleado.setDepartamento(departamento);
                empleadosEmpleado = em.merge(empleadosEmpleado);
                if (oldDepartamentoOfEmpleadosEmpleado != null) {
                    oldDepartamentoOfEmpleadosEmpleado.getEmpleados().remove(empleadosEmpleado);
                    oldDepartamentoOfEmpleadosEmpleado = em.merge(oldDepartamentoOfEmpleadosEmpleado);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void edit(Departamento departamento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Departamento persistentDepartamento = em.find(Departamento.class, departamento.getId());
            Empresa empresaOld = persistentDepartamento.getEmpresa();
            Empresa empresaNew = departamento.getEmpresa();
            List<Empleado> empleadosOld = persistentDepartamento.getEmpleados();
            List<Empleado> empleadosNew = departamento.getEmpleados();
            if (empresaNew != null) {
                empresaNew = em.getReference(empresaNew.getClass(), empresaNew.getId());
                departamento.setEmpresa(empresaNew);
            }
            List<Empleado> attachedEmpleadosNew = new ArrayList<Empleado>();
            for (Empleado empleadosNewEmpleadoToAttach : empleadosNew) {
                empleadosNewEmpleadoToAttach = em.getReference(empleadosNewEmpleadoToAttach.getClass(), empleadosNewEmpleadoToAttach.getId());
                attachedEmpleadosNew.add(empleadosNewEmpleadoToAttach);
            }
            empleadosNew = attachedEmpleadosNew;
            departamento.setEmpleados(empleadosNew);
            departamento = em.merge(departamento);
            if (empresaOld != null && !empresaOld.equals(empresaNew)) {
                empresaOld.getDepartamentos().remove(departamento);
                empresaOld = em.merge(empresaOld);
            }
            if (empresaNew != null && !empresaNew.equals(empresaOld)) {
                empresaNew.getDepartamentos().add(departamento);
                empresaNew = em.merge(empresaNew);
            }
            for (Empleado empleadosOldEmpleado : empleadosOld) {
                if (!empleadosNew.contains(empleadosOldEmpleado)) {
                    empleadosOldEmpleado.setDepartamento(null);
                    empleadosOldEmpleado = em.merge(empleadosOldEmpleado);
                }
            }
            for (Empleado empleadosNewEmpleado : empleadosNew) {
                if (!empleadosOld.contains(empleadosNewEmpleado)) {
                    Departamento oldDepartamentoOfEmpleadosNewEmpleado = empleadosNewEmpleado.getDepartamento();
                    empleadosNewEmpleado.setDepartamento(departamento);
                    empleadosNewEmpleado = em.merge(empleadosNewEmpleado);
                    if (oldDepartamentoOfEmpleadosNewEmpleado != null && !oldDepartamentoOfEmpleadosNewEmpleado.equals(departamento)) {
                        oldDepartamentoOfEmpleadosNewEmpleado.getEmpleados().remove(empleadosNewEmpleado);
                        oldDepartamentoOfEmpleadosNewEmpleado = em.merge(oldDepartamentoOfEmpleadosNewEmpleado);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = departamento.getId();
                if (findDepartamento(id) == null) {
                    throw new NonexistentEntityException("The departamento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Departamento departamento;
            try {
                departamento = em.getReference(Departamento.class, id);
                departamento.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The departamento with id " + id + " no longer exists.", enfe);
            }
            Empresa empresa = departamento.getEmpresa();
            if (empresa != null) {
                empresa.getDepartamentos().remove(departamento);
                empresa = em.merge(empresa);
            }
            List<Empleado> empleados = departamento.getEmpleados();
            for (Empleado empleadosEmpleado : empleados) {
                empleadosEmpleado.setDepartamento(null);
                empleadosEmpleado = em.merge(empleadosEmpleado);
            }
            em.remove(departamento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public List<Departamento> findDepartamentoEntities() {
        return findDepartamentoEntities(true, -1, -1);
    }

    @Override
    public List<Departamento> findDepartamentoEntities(int maxResults, int firstResult) {
        return findDepartamentoEntities(false, maxResults, firstResult);
    }

    private List<Departamento> findDepartamentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Departamento.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Departamento findDepartamento(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Departamento.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public int getDepartamentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Departamento> rt = cq.from(Departamento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Departamento> getDepartamentosByEmpresaID(Long empresaId) {
        EntityManager em = getEntityManager();
        try {
            em = getEntityManager();
            em.getTransaction().begin();

            TypedQuery<Departamento> query = em.createQuery(
                "SELECT d FROM Departamento d WHERE d.empresa.id = :empresaId", Departamento.class);
            query.setParameter("empresaId", empresaId);
            List<Departamento> result = query.getResultList();

            em.getTransaction().commit();
            return result;
        } catch (Exception e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Error al obtener departamentos para la empresa con ID: " + empresaId, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
}
