package com.vitacheck.controller;

import com.vitacheck.dto.UserDto;
import com.vitacheck.global.apiPayload.CustomResponse;
import com.vitacheck.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "user", description = "사용자 정보 관련 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "인증된 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.InfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/me")
    public CustomResponse<UserDto.InfoResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        UserDto.InfoResponse myInfo = userService.getMyInfo(email);
        return CustomResponse.ok(myInfo);
    }

    @Operation(summary = "내 정보 수정", description = "사용자의 닉네임을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.InfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자", content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @PutMapping("/me")
    public CustomResponse<UserDto.InfoResponse> updateMyInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDto.UpdateRequest request
    ) {
        String email = userDetails.getUsername();
        UserDto.InfoResponse updatedInfo = userService.updateMyInfo(email, request);
        return CustomResponse.ok(updatedInfo);
    }

    @Operation(summary = "FCM 토큰 업데이트", description = "클라이언트(앱/웹)의 푸시 알림을 위한 FCM 디바이스 토큰을 등록 또는 갱신합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰 업데이트 성공",
                    content = @Content(examples = @ExampleObject(value = "{\"isSuccess\":true,\"code\":\"COMMON200\",\"message\":\"성공적으로 요청을 수행했습니다.\",\"result\":\"FCM 토큰이 업데이트되었습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(examples = @ExampleObject(value = "{\"isSuccess\":false,\"code\":\"U0001\",\"message\":\"로그인이 필요합니다.\",\"result\":null}"))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "{\"isSuccess\":false,\"code\":\"U0002\",\"message\":\"사용자를 찾을 수 없습니다.\",\"result\":null}")))
    })
    @PutMapping("/me/fcm-token")
    public CustomResponse<String> updateFcmToken(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "사용자의 새로운 FCM 디바이스 토큰")
            @RequestBody UserDto.UpdateFcmTokenRequest request
    ) {
        userService.updateFcmToken(userDetails.getUsername(), request.getFcmToken());
        return CustomResponse.ok("FCM 토큰이 업데이트되었습니다.");
    }
}
