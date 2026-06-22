package com.innovatech.bff_service.service.impl;

import com.innovatech.bff_service.client.ApiGatewayClient;
import com.innovatech.bff_service.dto.TareaDTO;
import com.innovatech.bff_service.dto.TareaRequestDTO;
import com.innovatech.bff_service.service.BffTareaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BffTareaServiceImpl implements BffTareaService {

    private final ApiGatewayClient apiGatewayClient;

    @Override
    public List<TareaDTO> listarTareasPorProyecto(Long proyectoId) {
        return apiGatewayClient.obtenerTareasPorProyecto(proyectoId);
    }

    @Override
    public TareaDTO crearTarea(TareaRequestDTO request) {
        return apiGatewayClient.crearTarea(request);
    }
}
