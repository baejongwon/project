package com.kg.library.donate;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DonateMapper {

	List<DonateDTO> donateForm(int begin, int end, String select, String search);

	int donateWriteProc(DonateDTO dto);

	int totalCount(String select, String search);

	DonateDTO donateContent(String no);

}