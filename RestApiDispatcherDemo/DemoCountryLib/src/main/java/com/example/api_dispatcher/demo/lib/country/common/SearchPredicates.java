package com.example.api_dispatcher.demo.lib.country.common;
/**
 * List of search predicates
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

import java.util.function.Predicate;

import com.example.api_dispatcher.demo.lib.country.dto.CityDto;
import com.example.api_dispatcher.demo.lib.country.dto.SearchParamsDto;

public class SearchPredicates {

	public static Predicate<CityDto> isPopulationMoreThan(Integer minSize) {
		return p -> minSize == null || p.getPopulation() >= minSize;
	}
	
	public static Predicate<CityDto> isNameStartsFrom(String startFrom) {
		return p -> startFrom == null 
				|| p.getName().toLowerCase().startsWith(startFrom.toLowerCase());
	}
	
	public static Predicate<CityDto> match(SearchParamsDto searchParams) {
		return p -> searchParams == null || isPopulationMoreThan(searchParams.getMinSize())
				.and(isNameStartsFrom(searchParams.getStartFrom())).test(p);
	}
}
