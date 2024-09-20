package com.networkingProject.rightNow.service;

import com.networkingProject.rightNow.dto.Request.TravelRequestDTO;
import com.networkingProject.rightNow.entity.Travel;
import com.networkingProject.rightNow.entity.TravelMem;
import com.networkingProject.rightNow.entity.User;
import com.networkingProject.rightNow.repository.TravelMemRepository;
import com.networkingProject.rightNow.repository.TravelRepository;
import com.networkingProject.rightNow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.json.simple.parser.ParseException;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TravelService {
    private final TravelRepository travelRepository;
    private final TravelMemRepository travelMemRepository;
    private final UserRepository userRepository;
    private final ExpenditureService expenditureService;

    // 여행 계획 생성
    public void createTravel(TravelRequestDTO travelRequestDTO, Long userId) {
        // 여행 이름 중복 체크
        if (travelRepository.existsByTravelName(travelRequestDTO.getTravelName())) {
            throw new IllegalArgumentException("해당 이름의 여행이 이미 존재합니다.");
        }

        // 여행 계획 생성
        Travel travel = Travel.builder()
                .startDate(travelRequestDTO.getStartDate())
                .endDate(travelRequestDTO.getEndDate())
                .memo(travelRequestDTO.getMemo())
                .collectionOfMoney(0)
                .travelName(travelRequestDTO.getTravelName())
                .build();
        travelRepository.save(travel);

        // 로그인된 유저 정보 가져오기
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // travel_mem 테이블에 유저와 여행 정보 저장
        TravelMem travelMem = TravelMem.builder()
                .travelId(travel)
                .userId(user)
                .build();
        travelMemRepository.save(travelMem);
    }

    // 초대 링크 생성
    public String generateInviteLink(Long travelId) {
        return "http://beancp.com:8082/travel/join/" + travelId;
    }

    // 초대 링크 통해서 여행에 참여
    @Transactional
    public void joinTravel(Long userId, Long travelId) {
        // 여행 계획 찾기
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행 계획이 존재하지 않습니다."));

        // 유저 찾기
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        // 유저가 이미 여행에 참여하고 있는지 확인
        if (travelMemRepository.existsByTravelIdAndUserId(travel, user)) {
            throw new IllegalArgumentException("이미 참여한 유저입니다.");
        }

        // TravelMem에 유저와 여행 기록 추가
        TravelMem travelMem = TravelMem.builder()
                .travelId(travel)
                .userId(user)
                .build();
        travelMemRepository.save(travelMem);
    }

    @Transactional
    public void deleteTravel(Long travelId) {
        // 여행이 존재하는지 확인
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다."));

        // 1. 해당 여행과 관련된 모든 Expenditure 및 ExpendMem 삭제
        expenditureService.deleteExpendituresByTravelId(travelId);

        // 2. 해당 여행과 관련된 모든 TravelMem 삭제
        travelMemRepository.deleteByTravelId(travelId);

        // 3. 여행 삭제
        travelRepository.delete(travel);
    }

    // 유저가 참여중인 모든 여행 검색
    public List<Travel> getTravelsByUserId(Long userId) {
        return travelMemRepository.findAllTravelsByUserId(userId);
    }

    // 여행에 참여 중인 멤버들의 이름
    public List<String> getMemberNamesByTravelId(Long travelId) {
        return travelMemRepository.findAllMemberNamesByTravelId(travelId);
    }

    public List<String> extractAssistantResponses(String jsonResponse) {
        List<String> assistantMessages = new ArrayList<>();

        try {
            // JSON 응답 파싱
            JSONParser parser = new JSONParser();
            JSONObject responseObj = (JSONObject) parser.parse(jsonResponse);

            // messages 배열 가져오기
            JSONArray messagesArray = (JSONArray) responseObj.get("messages");

            // "assistant >"로 시작하는 메시지 추출
            for (Object messageObj : messagesArray) {
                String message = (String) messageObj;
                if (message.startsWith("assistant >")) {
                    // "assistant >" 이후의 메시지 추출
                    String assistantMessage = message.substring("assistant >".length()).trim();

                    // 1., 2., 3. 으로 구분된 세 가지 항목을 리스트로 나눔
                    String[] parts = assistantMessage.split("\\n\\n");  // 각 항목 사이에는 두 줄의 공백이 있음
                    for (String part : parts) {
                        assistantMessages.add(part.trim());
                    }
                    break;  // "assistant >" 메시지는 하나만 있을 것이므로, 찾고 나면 반복을 멈춤
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();  // 파싱 중 오류 처리
        }

        return assistantMessages;
    }
}
