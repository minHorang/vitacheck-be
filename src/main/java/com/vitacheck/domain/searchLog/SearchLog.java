package com.vitacheck.domain.searchLog;

import com.vitacheck.domain.common.BaseTimeEntity;
import com.vitacheck.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "search_logs")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SearchCategory category;

}
