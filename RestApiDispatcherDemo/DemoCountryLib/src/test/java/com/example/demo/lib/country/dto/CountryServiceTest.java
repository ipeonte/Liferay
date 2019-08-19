package com.example.demo.lib.country.dto;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.api_dispatcher.demo.lib.country.dto.CountryInfoDto;
import com.example.api_dispatcher.demo.lib.country.dto.RegionBaseDto;
import com.example.api_dispatcher.demo.lib.country.service.CountryService;
import com.example.api_dispatcher.demo.lib.country.service.impl.CountryServiceImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { CountryServiceImpl.class })
public class CountryServiceTest {

	@Autowired
	private CountryService _countries;
	
	@Test
	public void testCountryListService() {
		List<RegionBaseDto> clist = _countries.getCountryList();
		
		assertNotNull(clist);
		assertEquals(2, clist.size());
		
		checkCountry("Canada", "CA", clist.get(0));
		checkCountry("USA", "US", clist.get(1));
	}
	
	@Test
	public void testCountryInfoService() {
		checkCountryInfo("Canada", "CA", "CAD", "province", 5, _countries.getCountryInfo("CA"));
		checkCountryInfo("USA", "US", "USD", "state", 6, _countries.getCountryInfo("US"));
	}
	
	private void checkCountry(String name, String code, RegionBaseDto country) {
		assertEquals(name, country.getName());
		assertEquals(code, country.getCode());
	}
	
	private void checkCountryInfo(String name, String code, String currency,
				String regionName, int num, CountryInfoDto country) {
		assertNotNull(country);
		assertEquals(name, country.getName());
		assertEquals(code, country.getCode());
		assertEquals(currency, country.getCurrency());
		assertEquals(regionName, country.getRegionName());
		assertNotNull(country.getRegions());
		assertEquals(num, country.getRegions().size());
	}
	
}
