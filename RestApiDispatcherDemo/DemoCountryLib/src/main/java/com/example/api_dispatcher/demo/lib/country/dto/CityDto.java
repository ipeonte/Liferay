package com.example.api_dispatcher.demo.lib.country.dto;

/**
 * POJO class for city
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

public class CityDto implements Comparable<CityDto> {
	private final String name;
	private final Integer population;
	
	public CityDto(String name,Integer population) {
		this.name = name;
		this.population = population;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getPopulation() {
		return population;
	}
	
	@Override
	public int compareTo(CityDto o) {
		return this.name.compareTo(o.getName());
	}
	
	@Override
	public String toString() {
		return name + ":" + population;
	}
}
