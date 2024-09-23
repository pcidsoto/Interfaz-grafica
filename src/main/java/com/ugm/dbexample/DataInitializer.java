/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ugm.dbexample;

import com.ugm.dbexample.entities.Departamento;
import com.ugm.dbexample.entities.Empleado;
import com.ugm.dbexample.entities.Empresa;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

public class DataInitializer {
    private final EntityManagerFactory emf;
    private final boolean shouldInitializeData;
    private final Random random = new Random();

    public DataInitializer(EntityManagerFactory emf, boolean shouldInitializeData) {
        this.emf = emf;
        this.shouldInitializeData = shouldInitializeData;
    }

    public void initializeData() {
        if (!shouldInitializeData) {
            return;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Verificar si ya existen datos
            long empresaCount = (long) em.createQuery("SELECT COUNT(e) FROM Empresa e WHERE e.nombre = 'Universidad Global de Magallanes'").getSingleResult();
            if (empresaCount > 0) {
                System.out.println("La base de datos ya contiene datos. Saltando la inicialización.");
                return;
            }

            // Crear la universidad
            Empresa universidad = new Empresa();
            universidad.setNombre("Universidad Global de Magallanes");
            em.persist(universidad);

            // Crear 10 departamentos
            String[] nombresDepartamentos = {
                "Facultad de Ciencias", "Facultad de Ingeniería", "Facultad de Humanidades",
                "Facultad de Ciencias Sociales", "Facultad de Medicina", "Facultad de Derecho",
                "Facultad de Economía y Negocios", "Facultad de Educación", "Facultad de Artes",
                "Departamento de I+D"
            };

            Departamento[] departamentos = new Departamento[10];
            for (int i = 0; i < 10; i++) {
                departamentos[i] = new Departamento();
                departamentos[i].setNombre(nombresDepartamentos[i]);
                departamentos[i].setEmpresa(universidad);
                em.persist(departamentos[i]);
            }

            // Crear 40 empleados
            String[] nombres = {"Juan", "María", "Pedro", "Ana", "Luis", "Carla", "Diego", "Sofía"};
            String[] apellidos = {"García", "Rodríguez", "Martínez", "López", "González", "Pérez", "Sánchez", "Romero"};

            for (int i = 0; i < 40; i++) {
                Empleado empleado = new Empleado();
                empleado.setNombre(nombres[random.nextInt(nombres.length)]);
                empleado.setApellidos(apellidos[random.nextInt(apellidos.length)] + " " + 
                                      apellidos[random.nextInt(apellidos.length)]);
                empleado.setFechaNacimiento(generarFechaNacimientoAleatoria());
                empleado.setSalarioMensual(new BigDecimal(30000 + random.nextInt(70000)));
                empleado.setDepartamento(departamentos[random.nextInt(departamentos.length)]);
                empleado.setEmpresa(universidad);
                em.persist(empleado);
            }

            em.getTransaction().commit();
            System.out.println("Datos de prueba inicializados correctamente: 1 universidad, 10 departamentos y 40 empleados.");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private LocalDate generarFechaNacimientoAleatoria() {
        int year = 1960 + random.nextInt(40); // Empleados nacidos entre 1960 y 1999
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28); // Simplificamos a 28 días para evitar problemas con febrero
        return LocalDate.of(year, month, day);
    }
}
