package com.example.api_dispatcher.demo.lib.country.dto;

/**
 * POJO class for base region
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

public class RegionBaseDto implements Comparable<RegionBaseDto> {
	private final String code;
	private final String name;
	
	public RegionBaseDto(String name, String code) {
		this.name = name;
		this.code = code;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCode() {
		return code;
	}

	@Override
	public int compareTo(RegionBaseDto o) {
		return this.name.compareTo(o.getName());
	}
}
