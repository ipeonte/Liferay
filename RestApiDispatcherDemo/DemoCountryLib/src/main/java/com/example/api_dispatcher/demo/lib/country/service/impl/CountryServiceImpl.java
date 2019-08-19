package com.example.api_dispatcher.demo.lib.country.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.api_dispatcher.demo.lib.country.dto.CountryInfoDto;
import com.example.api_dispatcher.demo.lib.country.dto.RegionBaseDto;
import com.example.api_dispatcher.demo.lib.country.service.CountryService;
import com.example.api_dispatcher.demo.lib.country.shared.RegionListConstants;

/**
 * Country service
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@Service
public class CountryServiceImpl implements CountryService {

	@Override
	public List<RegionBaseDto> getCountryList() {
		// Country list already sorted
		return RegionListConstants.COUNTRIES_LIST;
	}

	@Override
	public CountryInfoDto getCountryInfo(String code) {
		return RegionListConstants.COUNTRIES_SET.get(code);
	}

}
