/**
 * Build and encode payload for AJAX request
 * 
 * @param api_name Name of API. Usually it's root path parameter, for ex. 
 * 		for /user/{id} request the api_name is "user"
 * @param method Request Method i.e "get", "post" etc.
 * @param path_params Path following api name, for ex. 
 * 	for /user/1/email request the path will be "1/email"
 * @param query_params URL Query params as hash map i.e 
 * 		{"sort_by": "name"}
 * @param body Request payload body
 * @returns Encoded request
 */
function prep_req_payload(api_name, method, path_params, query_params, body) {
	return encode_payload(build_req_payload(api_name, method, 
								path_params, query_params, body));
}

/**
 * Combine payload object out of input parameters 
 * 
 * @param api_name Name of API. Usually it's root path parameter, for ex. 
 * 		for /user/{id} request the api_name is "user"
 * @param method Request Method i.e "get", "post" etc.
 * @param path_params Path following api name, for ex. 
 * 	for /user/1/email request the path will be "1/email"
 * @param query_params URL Query params as hash map i.e 
 * 		{"sort_by": "name"}
 * @param body Request payload body
 * @returns Payload Object
 */
function build_req_payload(api_name, method, path_params, query_params, body) {
	var payload = {
		api: api_name,
		method: method
	};
	
	if (path_params !== undefined)
		payload["path_params"] = path_params.split("/");
	
	if (query_params !== undefined)
		payload["query_params"] = query_params;
	
	if (body != undefined)
		payload["body"] = body;
	
	console.log(payload);
	
	return payload;
}

/**
 * Encode payload
 * 
 * @param payload Payload object
 * @returns String with encoded object
 */
function encode_payload(payload) {
	// BASE64 encode the payload
	return window.btoa(JSON.stringify(payload));
}

/**
 * Make request data object for ajax call
 * 
 * @param payload Encoded payload
 * @returns Data object
 */
function make_req_data(payload) {
	var result = {};
	result[REQ_PARAM_NAME] = payload;
	return result;
}