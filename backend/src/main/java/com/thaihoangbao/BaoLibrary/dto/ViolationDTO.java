package com.thaihoangbao.BaoLibrary.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationDTO {
    private Integer violationId;
    
    private Integer loanDetailId;
    
    private String violationType;
    
    private String description;
    
    private Date violationDate;
    
    private BigDecimal fineAmount;
    
    private Boolean isPaid;
    
    private Integer userId;
    
    private String bookTitle;
}
