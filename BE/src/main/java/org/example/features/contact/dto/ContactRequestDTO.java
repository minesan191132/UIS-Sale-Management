package org.example.features.contact.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequestDTO {
    private String fullName;
    private String phone;
    private String email;
    private String subject;
    private String message;
}
