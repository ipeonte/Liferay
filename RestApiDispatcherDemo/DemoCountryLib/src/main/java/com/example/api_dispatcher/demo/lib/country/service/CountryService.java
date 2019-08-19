package com.example.api_dispatcher.demo.lib.country.service;

import java.util.List;

import com.example.api_dispatcher.demo.lib.country.dto.CountryInfoDto;
import com.example.api_dispatcher.demo.lib.country.dto.RegionBaseDto;

/**
 * Country Service Interface
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public interface CountryService {

	List<RegionBaseDto> getCountryList();
	
	CountryInfoDto getCountryInfo(String code);
}
