// State of current country->region selection
LAST_COUNTRY = "";
LAST_REGION = "";

// List of uploaded flags
FLAGS = {};

$(document).ready(
		function() {
			$("#country_list").change(function() {
				on_country_change();
			});

			$("#region_list").change(function() {
				on_region_change();
			});

			$("#search_city").click(function() {
				on_city_search();
			});

			$("#chk_start_from").click(function() {
				on_start_from_check(this.checked);
			});

			$("#city_filter").click(function() {
				on_city_filter_apply(this.checked);
			});

			// bind 'myForm' and provide a simple callback function
			$('form#flag_form').ajaxForm(on_file_upload());

			// Retrieve country list
			ajax_call_plain("countries", "get",
			  function(data) {
				console.log(data);

				$.each(data, function(idx, country) {
					$("#country_list").append(
							$('<option>').text(country.name).attr('value',
									country.code));
				});
			});
		});

function on_file_upload() {
	return {
		beforeSubmit : function(arr, $form, options) {
			var code = $("#country_list").val();
			arr.push({
				name: REQ_PARAM_NAME,
				value: prep_req_payload("flag", "post",
											$("#country_list").val())
			});
			
			return true;
		},
		
		success : function(data) {
			// Strip spaces
			var fdata = data.substring(1, data.length - 1);
			
			$('input#fname').val("");
			toggle_flag(true, fdata);
		}
	};
}

function set_flag(fdata) {
	$("#flag").attr("src", "data:image/png;base64, " + fdata);
}

function on_country_change() {
	var code = $("#country_list").val();
	LAST_COUNTRY = code;

	// Reset city filter
	$("#city_filter").attr("checked", false);

	if (code == "") {
		$("#flag_form").hide();
		$("#region_wrapper").hide();
		$("#region_list").empty();
		$("#cities_list_res").hide();

		return;
	}

	ajax_call_path("countries", "get", code, 
	  function(data) {
		// Clear previous list
		$("#region_list").empty();

		// Show hidden elements
		$("#region_wrapper").show();

		var region_name = data.region_name.replace(/\b\w/g, function(l) {
			return l.toUpperCase();
		});

		$("#region_title").html(region_name);

		$("#region_list").append(
				$('<option>').text("Select " + region_name).attr('value', ""))

		$.each(data.regions,
				function(idx, region) {
					$("#region_list").append(
							$('<option>').text(region.name).attr('value',
									region.code));
				});
		
		// Toggle flag visibility
		toggle_flag(data.flag !== undefined, data.flag);
	});

	search_country_city(code);
}

function toggle_flag(visible, fdata) {
	if (visible) {
		// Set data first to prevent old data flickering
		set_flag(fdata);
		$("#flag_wrapper").show();
		$('form#flag_form').hide();
	} else {
		$("#flag_wrapper").hide();
		$('form#flag_form').show();
	}
}
function search_country_city(code) {
	ajax_call_path("cities", "get", code,
	  function(data) {
		set_city_list(data, "list");
	  });
}

function search_country_city_ex(data) {
	ajax_call_data(data, function(data) {
		set_city_list(data, "list");
	});
}

function search_region_city(code) {
	ajax_call_path("cities", "get", $("#country_list").val() + "/" + code,
	  function(data) {
		set_city_list(data, "list");
	  });
}

function on_region_change() {
	var code = $("#region_list").val();
	LAST_REGION = code;

	if (code == "") {
		$("cities_wrapper").hide();
		$("#city_list").empty();
		search_country_city($("#country_list").val());

		return;
	}

	search_region_city(code);
}

function get_search_body() {

	var body = {
		min_size : Number($("#min_size_param").val())
	};

	if ($("#chk_start_from").is(":checked"))
		body["start_from"] = $("#start_char_param").val();

	return body
}

function on_city_search() {
	ajax_call_body("cities", "get", JSON.stringify(get_search_body()),
	  function(data) {
		set_city_list(data, "search");
	  });
}

function on_city_filter_apply(enabled) {
	// Create payload manually
	var data = {
		api: "cities",
		method: "get",
	};

	if (enabled) {
		var qparams = {};

		if (LAST_COUNTRY != "")
			qparams["country_code"] = LAST_COUNTRY;
		if (LAST_REGION != "")
			qparams["region_code"] = LAST_REGION;

		data["query_params"] = qparams;

		data.body = JSON.stringify(get_search_body());
	} else {
		var path_params = [];
		if (LAST_COUNTRY != "")
			path_params.push(LAST_COUNTRY);
		if (LAST_REGION != "")
			path_params.push(LAST_REGION);

		data["path_params"] = path_params;
	}

	search_country_city_ex(data);
}

function set_city_list(data, prefix) {
	var id = "#city_" + prefix;
	var res = "#cities_" + prefix + "_res";

	// Clear previous list
	$(id).empty();
	$(res).show();

	$.each(data, function(idx, city) {
		$(id).append(
				$('<tr>').append($('<td>').text(city.name)).append(
						$('<td>').text(city.population)));
	});
}

function on_start_from_check(enabled) {
	$("#wrapper_start_from").toggleClass("disabled");
	$("#start_char_param").attr("disabled", !enabled);
}

/*****************************************************************************/
/*                             AJAX CALL HELPERS                             */
/*****************************************************************************/

function ajax_call_plain(api_name, method, on_success) {
	ajax_call_data(build_req_payload(api_name, method), on_success);
}

function ajax_call_path(api_name, method, path, on_success) {
	ajax_call_data(build_req_payload(api_name, method, path), on_success);
}

function ajax_call_body(api_name, method, body, on_success) {
	ajax_call_data(build_req_payload(api_name, method,
								undefined, undefined, body),
		on_success);
}

function ajax_call_body(api_name, method, body, on_success) {
	ajax_call_data(build_req_payload(api_name, method,
								undefined, undefined, body),
		on_success);
}

function ajax_call(api_name, method, path_params, query_params, body, on_success) {
	ajax_call_data(build_req_payload(api_name, method,
					path_params, query_params, body), on_success);
}

function ajax_call_data(data, on_success) {
	$("#error").hide();

	$.ajax({
		url : BASE_URL,
		type : 'POST',
		dataType : 'json',
		data : make_req_data(encode_payload(data)),
		success : function(data) {
			if (data.error != undefined) {
				$("#error").html("Error #" + data.error.message).show();
			} else if (typeof on_success == "function")
				on_success(data);
		}
	});
}
