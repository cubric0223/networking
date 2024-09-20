package com.networkingProject.rightNow.controller;

import com.networkingProject.rightNow.dto.MessageDTO;
import com.networkingProject.rightNow.dto.Request.Expenditure.CollectionOfMoneyRequestDTO;
import com.networkingProject.rightNow.dto.Request.Expenditure.ExpenditureRequestDTO;
import com.networkingProject.rightNow.dto.Request.Expenditure.ExpenditureUpdateRequestDTO;
import com.networkingProject.rightNow.dto.Response.ExpenditureResponseDTO;
import com.networkingProject.rightNow.dto.Response.ExpenditureSelectResponseDTO;
import com.networkingProject.rightNow.service.ExpenditureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenditure")
public class ExpenditureController {
    private final ExpenditureService expenditureService;

    @PostMapping("")
    public ResponseEntity<MessageDTO> createExpenditure(@RequestBody ExpenditureRequestDTO expenditureRequestDTO) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            expenditureService.createExpenditure(expenditureRequestDTO);
            messageDTO.setMessage("지출 내역 생성 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("")
    public ResponseEntity<MessageDTO> updateExpenditure(@RequestBody ExpenditureUpdateRequestDTO updateRequestDTO) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            expenditureService.updateExpenditure(updateRequestDTO);
            messageDTO.setMessage("지출 내역 수정 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/collectionOfMoney")
    public ResponseEntity<MessageDTO> insertCollectionOfMoney(@RequestBody CollectionOfMoneyRequestDTO collectionOfMoneyRequestDTO) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            expenditureService.updateCollectionOfMoney(collectionOfMoneyRequestDTO.getTravelId(),
                    collectionOfMoneyRequestDTO.getCollectionOfMoney());

            messageDTO.setMessage("사전에 걷은 금액 내역이 입력 성공");
            return ResponseEntity.ok(messageDTO);
        } catch (IllegalArgumentException e) {
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }

    // travelId로 지출 내역 조회
    @GetMapping("/travel/{travelId}")
    public ResponseEntity<List<ExpenditureSelectResponseDTO>> getExpendituresByTravelId(@PathVariable Long travelId) {
        List<ExpenditureSelectResponseDTO> expenditures = expenditureService.getExpendituresByTravelId(travelId);
        return ResponseEntity.ok(expenditures);
    }

    // expenditureId로 지출 상세 정보 조회
    @GetMapping("/details/{expenditureId}")
    public ResponseEntity<ExpenditureResponseDTO> getExpenditureDetails(@PathVariable Long expenditureId) {
        ExpenditureResponseDTO expenditureResponseDTO = expenditureService.getExpenditureDetails(expenditureId);
        return ResponseEntity.ok(expenditureResponseDTO);
    }

    @DeleteMapping("/{expenditureId}")
    public ResponseEntity<MessageDTO> deleteExpenditure(@PathVariable Long expenditureId) {
        MessageDTO messageDTO = new MessageDTO();
        try {
            expenditureService.deleteExpenditure(expenditureId);
            messageDTO.setMessage("지출 내역 삭제 성공");
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            messageDTO.setMessage(e.getMessage());
            return new ResponseEntity<>(messageDTO, HttpStatus.BAD_REQUEST);
        }
    }
}
