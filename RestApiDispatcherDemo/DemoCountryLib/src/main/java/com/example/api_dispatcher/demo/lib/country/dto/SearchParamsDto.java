package com.example.api_dispatcher.demo.lib.country.dto;

import javax.validation.constraints.Min;

/**
 * Class to handle search parameters
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public class SearchParamsDto {

	@Min(1)
	private Integer minSize;
	
	private String startFrom;

	public Integer getMinSize() {
		return minSize;
	}

	public void setMinSize(Integer minSize) {
		this.minSize = minSize;
	}

	public String getStartFrom() {
		return startFrom;
	}

	public void setStartFrom(String startFrom) {
		this.startFrom = startFrom;
	}
	
	
}
