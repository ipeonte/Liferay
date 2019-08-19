package com.example.demo.lib.country.dto;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.api_dispatcher.demo.lib.country.dto.RegionBaseDto;
import com.example.api_dispatcher.demo.lib.country.dto.RegionInfoDto;
import com.example.api_dispatcher.demo.lib.country.service.RegionService;
import com.example.api_dispatcher.demo.lib.country.service.impl.RegionServiceImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { RegionServiceImpl.class })
public class RegionServiceTest {

	@Autowired
	private RegionService _regions;
	
	@Test
	public void testRegionService() {
		checkRegionList("CA", 5, "Alberta", "British Columbia", "Quebec");
		checkRegionList("US", 6, "Arizona", "California", "Texas");
	}
	
	@Test
	public void testRegionInfo() {
		checkRegionInfo("CA", "ON", "Ontario", 4);
		checkRegionInfo("CA", "BC", "British Columbia", 1);
		
		checkRegionInfo("US", "CA", "California", 3);
		checkRegionInfo("US", "IL", "Illinois", 1);
	}
	
	public void checkRegionList(String countryCode, int size, String first, 
				String second, String last) {
		List<RegionBaseDto> regions = _regions.getRegionList(countryCode);
		assertNotNull(regions);
		
		// Check total size
		assertEquals(size, regions.size());
		
		// Check first and last
		assertEquals(first, regions.get(0).getName());
		if (size > 1)
			assertEquals(second, regions.get(1).getName());
		
		assertEquals(last, regions.get(regions.size() - 1).getName());
	}
	
	public void checkRegionInfo(String countryCode, String regionCode, String name, int num) {
		RegionInfoDto region = _regions.getRegionInfo(countryCode, regionCode);
		assertNotNull(region);
		
		assertEquals(name, region.getName());
		assertEquals(regionCode, region.getCode());
		assertNotNull(region.getCities());
		assertEquals(num, region.getCities().size());
	}
}
