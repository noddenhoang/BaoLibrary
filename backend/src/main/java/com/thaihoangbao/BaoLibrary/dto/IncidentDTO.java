package com.thaihoangbao.BaoLibrary.dto;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentDTO {
    private Integer incidentId;
    
    private Integer userId;
    
    private String userName;
    
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
    
    private Integer bookId;
    
    private String bookTitle;
    
    private Date reportDate;
    
    private String status;
    
    private Date resolvedDate;
    
    private String resolution;
}
