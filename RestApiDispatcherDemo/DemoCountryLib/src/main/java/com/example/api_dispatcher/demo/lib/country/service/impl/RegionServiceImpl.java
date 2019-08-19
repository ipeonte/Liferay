package com.example.api_dispatcher.demo.lib.country.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.api_dispatcher.demo.lib.country.dto.CountryInfoDto;
import com.example.api_dispatcher.demo.lib.country.dto.RegionBaseDto;
import com.example.api_dispatcher.demo.lib.country.dto.RegionInfoDto;
import com.example.api_dispatcher.demo.lib.country.service.RegionService;
import com.example.api_dispatcher.demo.lib.country.shared.RegionListConstants;

/**
 * Region Service
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@Service
public class RegionServiceImpl implements RegionService {

	@Override
	public List<RegionBaseDto> getRegionList(String countryCode) {
		CountryInfoDto country = RegionListConstants.COUNTRIES_SET.get(countryCode);
		
		if (country == null)
			return null;
		
		List<RegionBaseDto> regions = new ArrayList<>();
		for (RegionInfoDto region: country.getRegions().values())
			regions.add(new RegionBaseDto(region.getName(), region.getCode()));
		
		// Sort return array
		Collections.sort(regions);
		
		return regions;
	}

	@Override
	public RegionInfoDto getRegionInfo(String countryCode, String regionCode) {
		CountryInfoDto country = RegionListConstants.COUNTRIES_SET.get(countryCode);
		
		return (country == null) ? null : (country.getRegions() == null ? null :
			country.getRegions().get(regionCode));
	}
}
