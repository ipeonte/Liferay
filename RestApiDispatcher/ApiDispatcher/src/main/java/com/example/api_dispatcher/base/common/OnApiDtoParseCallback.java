package com.example.api_dispatcher.base.common;

import com.example.api_dispatcher.base.dto.ApiCallDto;
import com.example.api_dispatcher.base.shared.ApiDispatcherException;

/**
 * Class for Api Dto Parser Callback function
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */

@FunctionalInterface
public interface OnApiDtoParseCallback {

	void postProcess(ApiCallDto dto) throws ApiDispatcherException;
}
