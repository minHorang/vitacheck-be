package com.vitacheck.repository;

import com.vitacheck.domain.IngredientDosage;
import com.vitacheck.domain.user.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IngredientDosageRepository extends JpaRepository<IngredientDosage, Long> {

    /**
     * 주어진 성분 ID 리스트와 사용자의 연령/성별에 맞는 모든 섭취 기준을 조회합니다.
     */
    @Query("SELECT d FROM IngredientDosage d WHERE d.ingredient.id IN :ingredientIds " +
            "AND d.gender IN (:gender, 'ALL') " +
            "AND :age BETWEEN d.minAge AND d.maxAge")
    List<IngredientDosage> findApplicableDosages(
            @Param("ingredientIds") List<Long> ingredientIds,
            @Param("gender") Gender gender,
            @Param("age") int age);

    @Query("SELECT d FROM IngredientDosage d " +
            "WHERE d.ingredient.id = :ingredientId " +
            "AND (:age BETWEEN d.minAge AND d.maxAge) " +
            "AND d.gender IN (:gender, com.vitacheck.domain.user.Gender.ALL) " +
            "ORDER BY d.gender DESC") // MALE/FEMALE이 ALL보다 우선순위가 높도록 정렬
    List<IngredientDosage> findApplicableDosages(
            @Param("ingredientId") Long ingredientId,
            @Param("gender") Gender gender,
            @Param("age") int age);



    default Optional<IngredientDosage> findBestDosage(Long ingredientId, Gender gender, int age) {
        // 우선순위대로 정렬된 목록에서 가장 첫 번째 결과를 반환합니다.
        return findApplicableDosages(ingredientId, gender, age)
                .stream()
                .findFirst();
    }
}