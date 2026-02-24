package org.example.features.integration.vietqr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * VietQR API Response DTO
 * API: https://api.vietqr.io/v2/business/{taxCode}
 */
@Data
public class VietQRResponse {

    private String code;
    private String desc;
    private VietQRData data;

    @Data
    public static class VietQRData {

        @JsonProperty("name")
        private String name; // Tên công ty

        @JsonProperty("taxCode")
        private String taxCode; // Mã số thuế

        @JsonProperty("address")
        private String address; // Địa chỉ

        @JsonProperty("directorName")
        private String directorName; // Người đại diện

        @JsonProperty("businessType")
        private String businessType; // Loại hình kinh doanh

        @JsonProperty("phoneNumber")
        private String phoneNumber; // Số điện thoại

        @JsonProperty("email")
        private String email; // Email
    }
}
