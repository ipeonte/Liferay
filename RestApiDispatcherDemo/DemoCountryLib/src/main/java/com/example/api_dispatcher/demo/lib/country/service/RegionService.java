package com.example.api_dispatcher.demo.lib.country.service;

import java.util.List;

import com.example.api_dispatcher.demo.lib.country.dto.RegionBaseDto;
import com.example.api_dispatcher.demo.lib.country.dto.RegionInfoDto;

/**
 * Region Service Interface
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public interface RegionService {

	List<RegionBaseDto> getRegionList(String countryCode);
	
	/**
	 * Find region by Country Code and Region Code
	 * 
	 * @param countryCode Country Code (Mandatory)
	 * @param regionCode Region Code (Mandatory)
	 * @return
	 */
	RegionInfoDto getRegionInfo(String countryCode, String regionCode);
}
