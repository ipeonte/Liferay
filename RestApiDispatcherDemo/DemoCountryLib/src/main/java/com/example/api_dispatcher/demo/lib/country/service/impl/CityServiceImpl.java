package com.example.api_dispatcher.demo.lib.country.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.example.api_dispatcher.demo.lib.country.common.SearchPredicates;
import com.example.api_dispatcher.demo.lib.country.dto.*;
import com.example.api_dispatcher.demo.lib.country.service.CityService;
import com.example.api_dispatcher.demo.lib.country.shared.RegionListConstants;

/**
 * Region Service
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@Service
public class CityServiceImpl implements CityService {

	/* (non-Javadoc)
	 * @see com.example.api_dispatcher.demo.lib.country.service.CityService#findCity(java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public List<CityDto> findCity(String countryCode, String regionCode,
												SearchParamsDto searchParams) {
		// Sort cities out of total list based on optional criteria
		
		List<CityDto> cities = new ArrayList<>();
		
		for (CountryInfoDto country: RegionListConstants.COUNTRIES_SET.values()) {
			// City check
			if (countryCode != null && !countryCode.equals(country.getCode()))
				continue;
			
			for (RegionInfoDto region: country.getRegions().values()) {
				// Region check
				if (regionCode != null && !regionCode.equals(region.getCode()))
					continue;
				
				for (Entry<String, Integer> entry: region.getCities().entrySet()) {
					CityDto city = new CityDto(entry.getKey(), entry.getValue());
					
					// Check if filtering required
					if (!SearchPredicates.match(searchParams).test(city))
						continue;
					
					// Check if Population filter enabled
					if (searchParams != null && searchParams.getMinSize() != null) {
						// Check at which position insert
						int len = cities.size();
						if (len == 0)
							cities.add(city);
						else
							insertCityBySize(city, cities, 0, len - 1);
					} else {
						// Start From filter
						cities.add(city);
					}
				}
			}
		}
		
		if (searchParams == null || searchParams.getMinSize() == null) {
			// Sort by name
			Collections.sort(cities);
		} 
		
		return cities;
	}

	private void insertCityBySize(CityDto city, List<CityDto> list, int start, int end) {
		int pos = (start + end)/2;
		boolean fend = (start == end);
		
		// Compare with neighbours
		if (city.getPopulation() >= list.get(pos).getPopulation()) {
			if (fend)
				list.add(pos, city);
			else
				insertCityBySize(city, list, start, pos);
		} else {
			if (fend)
				list.add(pos + 1, city);
			else
				insertCityBySize(city, list, pos + 1 , end);
		}
	}
}
