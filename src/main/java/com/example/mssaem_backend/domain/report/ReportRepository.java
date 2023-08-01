package com.example.mssaem_backend.domain.report;

import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Report findTopByResourceIdAndReportTypeAndMemberOrderByIdDesc(Long resourceId,
        ReportType reportType, Member member);

    List<Report> findByResourceIdAndReportType(Long resourceId, ReportType reportType);
}
