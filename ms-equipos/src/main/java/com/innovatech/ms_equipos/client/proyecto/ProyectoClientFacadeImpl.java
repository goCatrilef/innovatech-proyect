package com.innovatech.ms_equipos.client.proyecto;

import org.springframework.stereotype.Component;

@Component
public class ProyectoClientFacadeImpl implements ProyectoClientFacade {

    @Override
    public boolean existeProyecto(Long proyectoId) {
        return proyectoId != null && proyectoId > 0;
    }
}