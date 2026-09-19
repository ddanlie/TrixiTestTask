package com.trixi.demo.repository;
import com.trixi.demo.entities.MunicipalPart;
import com.trixi.demo.entities.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipalPartRepository extends JpaRepository<MunicipalPart, Long>
{
}