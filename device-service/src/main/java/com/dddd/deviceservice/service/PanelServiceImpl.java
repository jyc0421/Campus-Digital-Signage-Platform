package com.dddd.deviceservice.service;

import com.dddd.deviceservice.dto.*;
import com.dddd.deviceservice.entity.Panel;
import com.dddd.deviceservice.exception.ResourceNotFoundException;
import com.dddd.deviceservice.repository.PanelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PanelServiceImpl implements PanelService {

    private final PanelRepository panelRepository;

    @Override
    public PanelResponseDTO createPanel(PanelRequestDTO request) {
        Panel panel = new Panel();
        panel.setSubscriberId(request.getSubscriberId());
        panel.setName(request.getName());
        panel.setLocation(request.getLocation());
        panel.setIpAddress(request.getIpAddress());
        panel.setMacAddress(request.getMacAddress());
        panel.setStatus(Panel.Status.OFFLINE);
        return toDTO(panelRepository.save(panel));
    }

    @Override
    public PanelResponseDTO getPanelById(Long id) {
        Panel panel = panelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found"));
        return toDTO(panel);
    }

    @Override
    public List<PanelResponseDTO> getAllPanels() {
        return panelRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PanelResponseDTO updatePanel(Long id, PanelRequestDTO request) {
        Panel panel = panelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Panel not found"));
        panel.setName(request.getName());
        panel.setLocation(request.getLocation());
        panel.setIpAddress(request.getIpAddress());
        panel.setMacAddress(request.getMacAddress());
        return toDTO(panelRepository.save(panel));
    }

    @Override
    public void deletePanel(Long id) {
        panelRepository.deleteById(id);
    }

    private PanelResponseDTO toDTO(Panel panel) {
        return new PanelResponseDTO(
                panel.getId(),
                panel.getName(),
                panel.getLocation(),
                panel.getIpAddress(),
                panel.getMacAddress(),
                panel.getStatus().name(),
                panel.getLastHeartbeat()
        );
    }
}