package com.skax.aiplatform.repository.home;

import com.skax.aiplatform.entity.UserIdeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserIdeStatusRepository extends JpaRepository<UserIdeStatus, String> {
    @Query("""
            select u
              from UserIdeStatus u
             where u.memberId = :userId
               and u.imgG = :ide
             order by coalesce(u.lstUpdatedAt, u.fstCreatedAt) desc,
                      u.fstCreatedAt desc
            """)
    List<UserIdeStatus> findAllByUserAndIde(@Param("userId") String userId,
                                            @Param("ide") String ide);

    @Query("""
            select count(u)
              from UserIdeStatus u
             where u.memberId = :userId
               and u.imgG = :ide
               and u.svrUrlNm like concat('%', :path)
            """)
    long countActiveByExactPath(@Param("userId") String userId,
                                @Param("ide") String ide,
                                @Param("path") String path);

    @Query("select u from UserIdeStatus u where u.expAt is not null and u.expAt < :now")
    List<UserIdeStatus> findExpired(@Param("now") LocalDateTime now);
    
    /**
     * 전체 IDE 상태 조회 (조건없이 모든 IDE 조회)
     * 최신순 정렬 (lstUpdatedAt → fstCreatedAt 기준)
     */
    @Query("""
            select u
              from UserIdeStatus u
             order by coalesce(u.lstUpdatedAt, u.fstCreatedAt) desc,
                      u.fstCreatedAt desc
            """)
    List<UserIdeStatus> findAllIdeStatus();
}
