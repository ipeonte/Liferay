package com.example.api_dispatcher.demo.lib.country.shared;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.api_dispatcher.demo.lib.country.dto.*;

public class RegionListConstants {

	public static final List<RegionBaseDto> COUNTRIES_LIST = new ArrayList<>();
	
	public static final Map<String, CountryInfoDto> COUNTRIES_SET = new HashMap<>();
	
	
	// For demo purposes (and short of time) the only added info for Canada and USA
	// For same reasons only top 10 cities added for each country with related provinces/states
	
	// For Canada cities information taken from https://canada.businesschief.com/top10/1003/Canadas-Top-10-Most-Populous-Cities
	// For USA cities information taken from https://www.moving.com/tips/the-top-10-largest-us-cities-by-population/
	
	static {
		Map<String, Integer> CA_ON_CITIES = new HashMap<>();
		CA_ON_CITIES.put("Toronto", 4753120);
		CA_ON_CITIES.put("Kitchener", 422514);
		CA_ON_CITIES.put("Hamilton", 647634);
		CA_ON_CITIES.put("Ottawa", 860928);
										
		Map<String, Integer> CA_MB_CITIES = new HashMap<>();
		CA_MB_CITIES.put("Winnipeg", 641483);
		
		Map<String, Integer> CA_QC_CITIES = new HashMap<>();
		CA_QC_CITIES.put("Montreal", 3616615);
		CA_QC_CITIES.put("Quebec City", 659545);
		
		Map<String, Integer> CA_AB_CITIES = new HashMap<>();
		CA_AB_CITIES.put("Edmonton", 862544);
		CA_AB_CITIES.put("Calgary", 988079);
		
		Map<String, Integer> CA_BC_CITIES = new HashMap<>();
		CA_BC_CITIES.put("Vancouver", 1953252);
		
		Map<String, RegionInfoDto> CA_PROVINCES = new HashMap<>();
		CA_PROVINCES.put("ON", new RegionInfoDto("Ontario", "ON", CA_ON_CITIES));
		CA_PROVINCES.put("MB", new RegionInfoDto("Manitoba", "MB", CA_MB_CITIES));
		CA_PROVINCES.put("QC", new RegionInfoDto("Quebec", "QC", CA_QC_CITIES));
		CA_PROVINCES.put("AB", new RegionInfoDto("Alberta", "AB", CA_AB_CITIES));
		CA_PROVINCES.put("BC", new RegionInfoDto("British Columbia", "BC", CA_BC_CITIES));
		COUNTRIES_SET.put("CA", new CountryInfoDto("Canada", "CA", "CAD", "province", CA_PROVINCES));
		
		Map<String, Integer> US_NY_CITIES = new HashMap<>();
		US_NY_CITIES.put("New York City", 8550405);
		
		Map<String, Integer> US_CA_CITIES = new HashMap<>();
		US_CA_CITIES.put("Los Angeles", 3971883);
		US_CA_CITIES.put("San Diego", 1394928);
		US_CA_CITIES.put("San Jose", 1026908);
		
		Map<String, Integer> US_IL_CITIES = new HashMap<>();
		US_IL_CITIES.put("Chicago", 2720546);

		Map<String, Integer> US_TX_CITIES = new HashMap<>();
		US_TX_CITIES.put("Houston", 2296224);
		US_TX_CITIES.put("San Antonio", 1469845);
		US_TX_CITIES.put("Dallas", 1300092);
		
		Map<String, Integer> US_PA_CITIES = new HashMap<>();
		US_PA_CITIES.put("Philadelphia", 1567442);
		
		Map<String, Integer> US_AZ_CITIES = new HashMap<>();
		US_AZ_CITIES.put("Phoenix", 1563025);
		
		Map<String, RegionInfoDto> US_STATES = new HashMap<>();
		US_STATES.put("NY", new RegionInfoDto("New York", "NY", US_NY_CITIES));
		US_STATES.put("CA", new RegionInfoDto("California", "CA", US_CA_CITIES));
		US_STATES.put("IL", new RegionInfoDto("Illinois", "IL", US_IL_CITIES));
		US_STATES.put("TX", new RegionInfoDto("Texas", "TX", US_TX_CITIES));
		US_STATES.put("PA", new RegionInfoDto("Pennsylvania", "PA", US_PA_CITIES));
		US_STATES.put("AZ", new RegionInfoDto("Arizona", "AZ", US_AZ_CITIES));
		COUNTRIES_SET.put("US", new CountryInfoDto("USA", "US", "USD", "state", US_STATES));
		
		// Index list of provinces
		for (CountryInfoDto country: COUNTRIES_SET.values())
			COUNTRIES_LIST.add(new RegionBaseDto(country.getName(), country.getCode()));
		
		// and sort for faster access
		Collections.sort(COUNTRIES_LIST);
	}
}
