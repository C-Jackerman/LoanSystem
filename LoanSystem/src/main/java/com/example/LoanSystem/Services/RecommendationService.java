package com.example.LoanSystem.Services;
import com.example.LoanSystem.Entities.InferenceRunEntity;
import com.example.LoanSystem.Repositories.InferenceRunRepository;

import com.example.LoanSystem.DTOs.RecommendationDTO;
import com.example.LoanSystem.Entities.RecommendationEntity;
import com.example.LoanSystem.Repositories.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository repo;
    private final InferenceRunRepository inferenceRunRepository;

    public List<RecommendationDTO> byRun(Long runId){
        return repo.findByInferenceRunId(runId)
                .stream().map(this::toDTO).toList();
    }

    public List<RecommendationDTO> byApplication(String appId) {
        Long appIdLong;
        try {
            appIdLong = Long.parseLong(appId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid applicationId format: " + appId);
        }
        List<InferenceRunEntity> runs = inferenceRunRepository.findByApplicationId(appIdLong);
        if (runs.isEmpty()) throw new RuntimeException("No inference run for applicationId: " + appId);
        Long runId = runs.get(0).getId();
        List<RecommendationEntity> entities = repo.findByInferenceRunId(runId);
        return entities.stream().map(this::toDTO).toList();
    }

    private RecommendationDTO toDTO(RecommendationEntity e){
        return RecommendationDTO.builder()
                .id(e.getId())
                .inferenceRunId(e.getInferenceRun().getId())
                .recCode(e.getRecCode())
                .message(e.getMessage())
                .expectedGain(e.getExpectedGain())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
