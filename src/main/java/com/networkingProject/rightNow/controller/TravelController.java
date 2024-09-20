package com.networkingProject.rightNow.controller;

import com.networkingProject.rightNow.dto.MessageDTO;
import com.networkingProject.rightNow.dto.Request.TravelRecommendDetailDTO;
import com.networkingProject.rightNow.dto.Request.TravelRecommendRequestDTO;
import com.networkingProject.rightNow.dto.Request.TravelRequestDTO;
import com.networkingProject.rightNow.entity.Travel;
import com.networkingProject.rightNow.service.TravelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/travel")
public class TravelController {
    private final TravelService travelService;
    private final RestTemplate restTemplate;

    @PostMapping("/create")
    public ResponseEntity<MessageDTO> createTravel(@Valid @RequestBody TravelRequestDTO travelRequestDTO,
                                                   BindingResult bindingResult,
                                                   HttpServletRequest httpServletRequest) {
        MessageDTO messageDTO = new MessageDTO();

        // 유효성 검사 오류가 있을 경우 처리
        if (bindingResult.hasErrors()) {
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                messageDTO.setMessage(error.getDefaultMessage());
                return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
            }
        }

        // 세션에서 유저 정보 가져오기
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            messageDTO.setMessage("로그인이 필요합니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        // 세션에서 가져온 userId를 이용해 로그인된 유저 처리
        Long userId = (Long) session.getAttribute("userId");
        try {
            travelService.createTravel(travelRequestDTO, userId);
            messageDTO.setMessage("여행 계획 생성 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.CREATED);
        }catch(IllegalArgumentException e){
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }

    // 초대 링크를 통해 들어온 새로운 참여자
    @GetMapping("/join/{travelId}")
    public ResponseEntity<MessageDTO> joinTravel(@PathVariable Long travelId,
                                                 HttpServletRequest httpServletRequest) {
        MessageDTO messageDTO = new MessageDTO();

        // 세션에서 로그인된 유저 정보 확인
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            messageDTO.setMessage("로그인이 필요합니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        Long userId = (Long) session.getAttribute("userId");

        try {
            travelService.joinTravel(userId, travelId);
            messageDTO.setMessage("여행 참여 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }

    // 초대링크 생성하는 컨트롤러
    @GetMapping("/invite/{travelId}")
    public ResponseEntity<String> generateInviteLink(@PathVariable Long travelId,
                                                     HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }
        // 초대 링크 생성
        try{
            String inviteLink = travelService.generateInviteLink(travelId);
            return new ResponseEntity<>(inviteLink, HttpStatus.OK);
        }
        catch (IllegalArgumentException e){
            String stringE = e.toString();
            return new ResponseEntity<>(stringE, HttpStatus.BAD_REQUEST);
        }
    }

    // 현재 내가 참여했던 모든 여행 검색
    @GetMapping("/my-travels")
    public ResponseEntity<List<Travel>> getMyTravels(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Long userId = (Long) session.getAttribute("userId");
        List<Travel> travelList = travelService.getTravelsByUserId(userId);
        return new ResponseEntity<>(travelList, HttpStatus.OK);
    }

    @GetMapping("/{travelId}/members")
    public ResponseEntity<List<String>> getTravelMembers(@PathVariable Long travelId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<String> memberNames = travelService.getMemberNamesByTravelId(travelId);
            if (memberNames.isEmpty()){
                memberNames.add("참여중인 여행이 없습니다.");
                return new ResponseEntity<>(memberNames, HttpStatus.OK);
            }
            return new ResponseEntity<>(memberNames, HttpStatus.OK);
        }
        catch(IllegalArgumentException e){
            List<String> exception = new ArrayList<String>();
            exception.add(e.toString());
            return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{travelId}")
    public ResponseEntity<MessageDTO> deleteTravel(@PathVariable Long travelId, HttpServletRequest request) {
        MessageDTO messageDTO = new MessageDTO();

        // 세션에서 로그인된 유저 정보 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            messageDTO.setMessage("로그인이 필요합니다.");
            return new ResponseEntity<>(messageDTO, HttpStatus.UNAUTHORIZED);
        }

        try {
            travelService.deleteTravel(travelId);
            messageDTO.setMessage("여행 삭제 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/recommend")
    public ResponseEntity<List<String>> sendTravelRecommend(@RequestBody TravelRecommendRequestDTO requestDTO) {
        // 요청 데이터를 content 형식으로 변환
        String content = String.format("%d, %s~%s (%d박 %d일), %d명 한국 국내여행지 3군데 추천해줘" +
                        "숙박, 활동, 식사에 관한 내용과 이에 따른 예상 비용을 간단하게 제안해줘",
                requestDTO.getBudget(),
                requestDTO.getStartDate(),
                requestDTO.getEndDate(),
                requestDTO.getEndDate().getDayOfYear() - requestDTO.getStartDate().getDayOfYear() - 1,
                requestDTO.getEndDate().getDayOfYear() - requestDTO.getStartDate().getDayOfYear(),
                requestDTO.getNumOfPeople());

        // 외부 API 요청 본문 작성
        String externalApiUrl = "http://beancp.com:8083/start";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON 형태의 요청 본문 생성
        String jsonBody = String.format("{\"content\": \"%s\"}", content);

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // 외부 API에 POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                externalApiUrl, HttpMethod.POST, requestEntity, String.class);

        // 응답 본문에서 assistant 메시지를 추출
        List<String> assistantMessages = travelService.extractAssistantResponses(response.getBody());

        // 결과를 클라이언트에게 반환
        return new ResponseEntity<>(assistantMessages, response.getStatusCode());
    }

    @PostMapping("/recommend/detail")
    public ResponseEntity<?> sendTravelRecommend(@RequestBody TravelRecommendDetailDTO detail) {

        // 외부 API 요청 본문 작성
        String externalApiUrl = "http://beancp.com:8083/start";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // JSON 형태의 요청 본문 생성
        String jsonBody = String.format("{\"content\": \"%s\"}", detail.getDetail());

        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

        // 외부 API에 POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                externalApiUrl, HttpMethod.POST, requestEntity, String.class);

        // 응답 본문에서 assistant 메시지를 추출
        List<String> assistantMessages = travelService.extractAssistantResponses(response.getBody());

        // 결과를 클라이언트에게 반환
        return new ResponseEntity<>(assistantMessages, response.getStatusCode());
    }
}
