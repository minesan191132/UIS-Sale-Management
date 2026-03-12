package org.example.features.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.features.company.entity.User;

/**
 * UserAddress entity
 * Stores delivery addresses for users
 */
@Entity
@Table(name = "user_addresses")
@Getter
@Setter
@NoArgsConstructor
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", length = 200, nullable = false)
    private String fullName;

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "province", length = 100, nullable = false)
    private String province;

    @Column(name = "district", length = 100, nullable = false)
    private String district;

    @Column(name = "ward", length = 100, nullable = false)
    private String ward;

    @Column(name = "detail", length = 500, nullable = false)
    private String detail;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}
