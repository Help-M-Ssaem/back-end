package com.example.mssaem_backend.domain.search;

import com.example.mssaem_backend.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchRepository extends JpaRepository<Search, Long> {

  List<Search> findAllByMember(Member member);
}
