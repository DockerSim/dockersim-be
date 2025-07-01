// 이 인터페이스는 Image 엔티티에 대한 데이터 액세스를 담당합니다.
// 주요 메서드:
// - findByImageId : 이미지 ID로 검색
// - findByNameAndTag : 이미지 이름과 태그로 검색
// - findByName : 이미지 이름으로 검색
// - existsByNameAndTag : 이미지 존재 여부 확인

package com.dockersim.repository;

import com.dockersim.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByImageId(String imageId);

    Optional<Image> findByNameAndTag(String name, String tag);

    List<Image> findByName(String name);

    boolean existsByNameAndTag(String name, String tag);

    boolean existsByImageId(String imageId);
}