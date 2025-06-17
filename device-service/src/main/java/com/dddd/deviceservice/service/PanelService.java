package com.dddd.deviceservice.service;

import com.dddd.deviceservice.dto.PanelRequestDTO;
import com.dddd.deviceservice.dto.PanelResponseDTO;

import java.util.List;

public interface PanelService {
    PanelResponseDTO createPanel(PanelRequestDTO request);
    PanelResponseDTO getPanelById(Long id);
    List<PanelResponseDTO> getAllPanels();
    PanelResponseDTO updatePanel(Long id, PanelRequestDTO request);
    void deletePanel(Long id);
}