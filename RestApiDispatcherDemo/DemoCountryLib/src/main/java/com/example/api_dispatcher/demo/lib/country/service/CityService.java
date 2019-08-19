package com.example.api_dispatcher.demo.lib.country.service;

import java.util.List;

import com.example.api_dispatcher.demo.lib.country.dto.CityDto;
import com.example.api_dispatcher.demo.lib.country.dto.SearchParamsDto;

/**
 * City Service Interface
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public interface CityService {

	/**
	 * Find Cities but input criteria
	 * 
	 * @param countryCode - Country Code (optional)
	 * @param regionCode - Region Code (optional)
	 * @param minSize - min population size to include (optional)
	 * @param SearchParamsDto - sorting parameters (optional)
	 * 
	 * @return List of cities or empty list
	 */
	List<CityDto> findCity(String countryCode, String regionCode, SearchParamsDto searchParams);
}
