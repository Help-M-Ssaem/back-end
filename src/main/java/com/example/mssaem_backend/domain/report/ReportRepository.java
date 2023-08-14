package com.example.mssaem_backend.domain.report;

import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findTopByResourceIdAndReportTargetAndMemberOrderByIdDesc(Long resourceId,
        ReportTarget reportTarget, Member member);

    List<Report> findByResourceIdAndReportTarget(Long resourceId, ReportTarget reportTarget);
}
