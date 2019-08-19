package com.example.api_dispatcher.demo.lib.country.dto;

import java.util.Map;

/**
 * POJO class for Country Info
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

public class CountryInfoDto extends RegionBaseDto {
	private String flag;
	private final String currency;
	private final String regionName;
	private final Map<String, RegionInfoDto> regions;
	
	public CountryInfoDto(String name, String code, String currency,
			String regionName, Map<String, RegionInfoDto> regions) {
		super(name, code);
		
		this.regions = regions;
		this.currency = currency;
		this.regionName = regionName;
	}
	
	public Map<String, RegionInfoDto> getRegions() {
		return regions;
	}
	
	public String getRegionName() {
		return regionName;
	}
	
	public String getCurrency() {
		return currency;
	}

	public String getFlag() {
		return flag;
	}
	
	public void setFlag(String flag) {
		this.flag = flag;
	}
}
