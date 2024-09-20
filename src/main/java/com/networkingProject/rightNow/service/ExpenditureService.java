package com.networkingProject.rightNow.service;

import com.networkingProject.rightNow.dto.Request.Expenditure.ExpenditureRequestDTO;
import com.networkingProject.rightNow.dto.Request.Expenditure.ExpenditureUpdateRequestDTO;
import com.networkingProject.rightNow.dto.Response.ExpenditureResponseDTO;
import com.networkingProject.rightNow.dto.Response.ExpenditureSelectResponseDTO;
import com.networkingProject.rightNow.entity.ExpendMem;
import com.networkingProject.rightNow.entity.Expenditure;
import com.networkingProject.rightNow.entity.Travel;
import com.networkingProject.rightNow.entity.User;
import com.networkingProject.rightNow.repository.ExpendMemRepository;
import com.networkingProject.rightNow.repository.ExpenditureRepository;
import com.networkingProject.rightNow.repository.TravelRepository;
import com.networkingProject.rightNow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenditureService {
    private final ExpenditureRepository expenditureRepository;
    private final UserRepository userRepository;
    private final TravelRepository travelRepository;
    private final ExpendMemRepository expendMemRepository;

    @Transactional
    public void createExpenditure(ExpenditureRequestDTO expenditureRequestDTO) {
        List<String> userNames = expenditureRequestDTO.getName();
        Long travelId = expenditureRequestDTO.getTravelId();

        // travelId로 여행 계획 찾기
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다."));

        // Expenditure 생성
        Expenditure expenditure = Expenditure.builder()
                .expenditureName(expenditureRequestDTO.getExpenditureName())
                .classification(expenditureRequestDTO.getClassification())
                .receipt(expenditureRequestDTO.getReceipt())
                .memo(expenditureRequestDTO.getMemo())
                .travelId(travel)
                .expenditureMoney(expenditureRequestDTO.getExpenditureMoney())
                .build();
        System.out.println(expenditure);
        // 저장
        expenditureRepository.save(expenditure);

        // 각 사용자에 대해 ExpendMem 저장
        for (String name : userNames) {
            // 사용자 이름으로 User 찾기
            User user = userRepository.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + name));
            // ExpendMem 생성 및 저장
            ExpendMem expendMem = ExpendMem.builder()
                    .expenditureId(expenditure)
                    .userId(user)
                    .build();
            expendMemRepository.save(expendMem);
        }
    }

    @Transactional
    public void updateExpenditure(ExpenditureUpdateRequestDTO updateRequestDTO) {
        // 1. 지출 항목 찾기
        Expenditure expenditure = expenditureRepository.findById(updateRequestDTO.getExpenditureId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지출 내역이 존재하지 않습니다."));

        // 2. 여행 찾기
        travelRepository.findById(updateRequestDTO.getTravelId())
                .orElseThrow(() -> new IllegalArgumentException("해당 여행이 존재하지 않습니다."));

        // 3. ExpendMem에서 기존 사용자들 삭제
        expendMemRepository.deleteByExpenditureId(expenditure.getExpenditureId());

        // 4. 새롭게 사용자 추가
        List<String> userNames = updateRequestDTO.getName();
        for (String name : userNames) {
            // 사용자 이름으로 User 찾기
            User user = userRepository.findByName(name)
                    .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + name));

            // ExpendMem 생성 및 저장
            ExpendMem expendMem = ExpendMem.builder()
                    .expenditureId(expenditure)  // 기존의 지출 항목
                    .userId(user)  // 새로 추가할 사용자
                    .build();
            expendMemRepository.save(expendMem);
        }

        // 5. Expenditure 업데이트
        expenditure.setExpenditureName(updateRequestDTO.getExpenditureName());
        expenditure.setClassification(updateRequestDTO.getClassification());
        expenditure.setReceipt(updateRequestDTO.getReceipt());
        expenditure.setMemo(updateRequestDTO.getMemo());
        expenditure.setExpenditureMoney(updateRequestDTO.getExpenditureMoney());

        // 6. 변경 사항 저장
        expenditureRepository.save(expenditure);
    }


    // collectionOfMoney 업데이트 메서드
    @Transactional
    public void updateCollectionOfMoney(Long travelId, int newCollectionOfMoney) {
        Travel travel = travelRepository.findById(travelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 여행 ID를 찾을 수 없습니다: " + travelId));

        travel.setCollectionOfMoney(newCollectionOfMoney);

        // 업데이트된 여행 정보를 저장
        travelRepository.save(travel);
    }

    @Transactional
    public void deleteExpendituresByTravelId(Long travelId) {
        // travelId에 속하는 모든 지출 항목 찾기
        List<Expenditure> expenditures = expenditureRepository.findByTravelId(travelId);

        // 모든 지출 항목에 대해 연결된 ExpendMem 삭제
        for (Expenditure expenditure : expenditures) {
            expendMemRepository.deleteByExpenditureId(expenditure.getExpenditureId());
        }

        // 지출 항목 삭제
        expenditureRepository.deleteByTravelId(travelId);
    }

    public List<ExpenditureSelectResponseDTO> getExpendituresByTravelId(Long travelId) {
        return expenditureRepository.findByTravelIdResponse(travelId);
    }

    // expenditureId로 지출 세부사항 및 ExpendMem에 있는 사용자 이름 조회
    @Transactional
    public ExpenditureResponseDTO getExpenditureDetails(Long expenditureId) {
        // Expenditure에서 expenditureMoney, receipt, memo 조회
        Expenditure expenditure = expenditureRepository.findById(expenditureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지출 내역이 존재하지 않습니다."));

        // ExpendMem에서 해당 지출에 참여한 사용자 이름 조회
        List<String> userNames = expendMemRepository.findUserNamesByExpenditureId(expenditureId);

        // 필요한 정보를 담은 DTO 반환
        return new ExpenditureResponseDTO(
                expenditure.getExpenditureName(),
                expenditure.getExpenditureMoney(),
                expenditure.getReceipt(),
                expenditure.getMemo(),
                userNames
        );
    }

    @Transactional
    public void deleteExpenditure(Long expenditureId) {
        // Expenditure 존재 여부 확인
        Expenditure expenditure = expenditureRepository.findById(expenditureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지출 내역이 존재하지 않습니다: " + expenditureId));

        // ExpendMem에서 해당 expenditureId를 참조하는 데이터 삭제
        expendMemRepository.deleteByExpenditureId(expenditureId);

        // 이제 Expenditure 삭제
        expenditureRepository.delete(expenditure);
    }

    public void updateImageUrl(Long expenditureId, String imageUrl) {
        Expenditure expenditure = expenditureRepository.findByExpenditureId(expenditureId).orElseThrow(() ->
                new RuntimeException("expenditure not found"));
        expenditure.setReceipt(imageUrl);
        expenditureRepository.save(expenditure);
    }
}