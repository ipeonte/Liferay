package com.example.demo.lib.country.dto;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.api_dispatcher.demo.lib.country.dto.CityDto;
import com.example.api_dispatcher.demo.lib.country.dto.SearchParamsDto;
import com.example.api_dispatcher.demo.lib.country.service.CityService;
import com.example.api_dispatcher.demo.lib.country.service.impl.CityServiceImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { CityServiceImpl.class })
public class CityServiceTest {

	@Autowired
	private CityService _cities;

	@Test
	public void testCityCountryList() {
		// Test unknown country
		checkCityList("QQ", "", "", "", 0);

		// Check list for each country
		checkCityList("CA", "Calgary", "Edmonton", "Winnipeg", 10);
		checkCityList("US", "Chicago", "Dallas", "San Jose", 10);

		// Check list for all countries
		checkCityList(null, "Calgary", "Chicago", "Winnipeg", 20);
	}

	@Test
	public void testCityRegionList() {
		// Check invalid region
		checkCityList("CA", "QQ", "", "", "", 0);

		checkCityList("CA", "ON", "Hamilton", "Kitchener", "Toronto", 4);
		checkCityList("US", "TX", "Dallas", "Houston", "San Antonio", 3);
	}

	@Test
	public void testCitySize() {
		// Search millions
		checkCityListEx(_cities.findCity("CA", null, getSearchParams(1000000)),
				"Toronto", "Montreal", "Vancouver", 3);
		checkCityListEx(_cities.findCity("US", null,  getSearchParams(1000000)),
				"New York City", "Los Angeles", "San Jose", 10);
		
		// Search all for Canada
		checkCityListEx(_cities.findCity("CA", null,  getSearchParams(0)),
				"Toronto", "Montreal", "Kitchener", 10);
	}

	@Test
	public void testCityStartFrom() {
		checkCityListSearch(getSearchParams("T"), "CA", "Toronto");
		checkCityListSearch(getSearchParams("S"), "US", "San Antonio", "San Diego", "San Jose", 3);
		checkCityListSearch(getSearchParams(1000000, "S"), "US", "San Antonio", "San Diego", "San Jose", 3);
		checkCityListSearch(getSearchParams(1100000, "S"), "US", "San Antonio", "San Diego", null, 2);
		checkCityListSearch(getSearchParams(1000000, "P"), "US", "Philadelphia", "Phoenix", null, 2);
	}
	
	private void checkCityListEx(List<CityDto> cities, String first, String second, String last, int num) {
		assertNotNull(cities);

		int len = cities.size();
		assertEquals(num, len);

		if (num == 0)
			return;

		assertEquals(first, cities.get(0).getName());
		if (len > 1) {
			assertEquals(second, cities.get(1).getName());
			
			if (len > 2)
				assertEquals(last, cities.get(len - 1).getName());
		}

	}

	private void checkCityList(String countryCode, String regionCode, String first, String second, String last,
			int num) {
		List<CityDto> cities = _cities.findCity(countryCode, regionCode, null);
		
		checkCityListEx(cities, first, second, last, num);
	}

	private void checkCityList(String countryCode, String first, String second, String last, int num) {
		checkCityList(countryCode, null, first, second, last, num);
	}
	
	private void checkCityListSearch(SearchParamsDto searchParams, String countryCode, String first, 
															String second, String last, int num) {
		List<CityDto> cities = _cities.findCity(countryCode, null, searchParams);
		
		checkCityListEx(cities, first, second, last, num);
	}
	
	private void checkCityListSearch(SearchParamsDto searchParams, String countryCode, String name) {
		checkCityListSearch(searchParams, countryCode, name, null, null, 1);
	}
	
	private SearchParamsDto getSearchParams(Integer minSize) {
		SearchParamsDto dto = new SearchParamsDto();
		dto.setMinSize(minSize);
		
		return dto;
	}
	
	private SearchParamsDto getSearchParams(String startFrom) {
		SearchParamsDto dto = new SearchParamsDto();
		dto.setStartFrom(startFrom);
		
		return dto;
	}
	
	private SearchParamsDto getSearchParams(Integer minSize, String startFrom) {
		SearchParamsDto dto = getSearchParams(minSize);
		dto.setStartFrom(startFrom);
		
		return dto;
	}
}
