package com.vitacheck.controller;

import com.vitacheck.domain.user.User;
import com.vitacheck.dto.SearchDto;
import com.vitacheck.dto.SupplementByPurposeResponse;
import com.vitacheck.dto.SupplementDto;
import com.vitacheck.dto.SupplementPurposeRequest;
import com.vitacheck.global.apiPayload.CustomException;
import com.vitacheck.global.apiPayload.CustomResponse;
import com.vitacheck.global.apiPayload.code.ErrorCode;
import com.vitacheck.service.StatisticsService;
import com.vitacheck.service.SupplementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/supplements")
@Slf4j

public class SupplementController {

    private final SupplementService supplementService;
    private final StatisticsService statisticsService;

    @GetMapping("/search")
    @Operation(summary = "영양제 통합 검색", description = "키워드, 브랜드명, 성분명으로 영양제를 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "검색 파라미터가 없는 경우")
    })
    public CustomResponse<SearchDto.UnifiedSearchResponse> searchSupplements(
            @AuthenticationPrincipal User user,
            @Parameter(description = "검색 키워드 (상품명)") @RequestParam(required = false) String keyword,
            @Parameter(description = "브랜드 이름") @RequestParam(required = false) String brandName,
            @Parameter(description = "성분 이름") @RequestParam(required = false) String ingredientName,
            @ParameterObject Pageable pageable
    ) {
        if (!StringUtils.hasText(keyword) && !StringUtils.hasText(brandName) && !StringUtils.hasText(ingredientName)) {
            throw new CustomException(ErrorCode.SEARCH_KEYWORD_EMPTY);
        }

        SearchDto.UnifiedSearchResponse response = supplementService.search(user, keyword, brandName, ingredientName, pageable);
        return CustomResponse.ok(response);
    }

    @PostMapping("/by-purposes")
    @Operation(summary = "목적별 영양소 및 영양제 조회", description = "선택한 목적에 맞는 성분 및 관련 영양제를 반환합니다.")
    public CustomResponse<Map<String, SupplementByPurposeResponse>> getSupplementsByPurposes(
            @RequestBody SupplementPurposeRequest request
    ) {
        Map<String, SupplementByPurposeResponse> response = supplementService.getSupplementsByPurposes(request);
        return CustomResponse.ok(response);
    }

    @PostMapping("/{supplementId}/log-click")
    @Operation(summary = "영양제 클릭 기록", description = "사용자가 특정 영양제를 클릭했음을 기록하고 통계를 업데이트합니다.")
    public CustomResponse<String> logSupplementClick(
            @AuthenticationPrincipal User user,
            @PathVariable Long supplementId
    ) {
        if (user != null) {
            statisticsService.incrementSupplementClickCount(user, supplementId);
        }else {
            log.info("유저없음");
        }
        return CustomResponse.ok("클릭이 기록되었습니다.");
    }
}