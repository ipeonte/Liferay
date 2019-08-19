package com.example.api_dispatcher.demo.lib.country.dto;

import java.util.Map;

/**
 * POJO class for Region Info
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class RegionInfoDto extends RegionBaseDto {
	private final Map<String, Integer> cities;

	public RegionInfoDto(String name, String code, Map<String, Integer> cities) {
		super(name, code);
		this.cities = cities;
	}
	
	public Map<String, Integer> getCities() {
		return cities;
	}
}
