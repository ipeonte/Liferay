<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet"%>

<portlet:resourceURL id="api_call" var="baseUrl"></portlet:resourceURL>
<portlet:resourceURL id="file_upload_call" var="uploadFileUrl"></portlet:resourceURL>

<script type="text/javascript">
  var BASE_URL = "<%=baseUrl%>";
	var REQ_PARAM_NAME = "<c:out value="${reqParamName}" />";
</script>

<hr />
<table id="api_dispatcher_demo">  
	<tr>
		<td class="vcell control">
		  <table>
		    <tr>
		      <td>
		  <b>Country Info</b> <br><br>
		    Country: <select id="country_list">
				  <option value="">Select Country</option>

		    </select>
		      </td>
		      </tr>
		      <tr>
		      <td>
       
			  <div id="region_wrapper" class="hidden-block">
          <div id="flag_wrapper">
            <img id="flag" />
	        </div>
	        
	        <form id="flag_form" action="<%=uploadFileUrl%>" 
	                enctype="multipart/form-data"
	                         method="post" class="hidden-block"> 
	            Upload Country Flag: <input type="file" name="file" id="fname" />
	            <input type="hidden" name="rp"
	              value="eyJhcGkiOiJmbGFnIiwibWV0aG9kIjoicG9zdCIsInBhdGhfcGFyYW1zIjpbIkNBIl19" /> 
	            <input type="submit" value="Upload" /> 
	        </form>
        
        
				  <span id="region_title"></span>: 
				  <select id="region_list"></select>
				  
			  </div>
			  </td>
			  </tr>
			  </table>
	  </td>
	  <td class="data">
	    <div id="cities_list_res" class="hidden-block">
	      <b>Cities Info: </b>
	      <table class="cities">
	        <tr>
	          <th>Name</th>
	          <th>Population</th>
	        <tr>
	        <tbody id="city_list">
	        </tbody>
	      </table>
	      <hr />
	      <div><input type="checkbox" id="city_filter" /> Apply City Filter below</div>
	    </div>
	  </td>
	</tr>
	
	<tr>
	 <td colspan="2">
	   <hr />
	 </td>
	</tr>
	<tr>
    <td colspan="2">
      <b>Search City :</b>
          <p>With Population more than: <input type="text" 
            id="min_size_param" value="1000000" /> people</p>
          <p><input type="checkbox" id="chk_start_from" checked />
            <span id="wrapper_start_from"><b>And</b> starting with letter: </span>
          </p>
          <p><input type="text" 
              id="start_char_param" value="T" /> people</p>
        <button id="search_city">Search</button>
        <table id="cities_search_res" class="cities hidden-block">
          <thead>
            <tr>
              <th>Name</th>
              <th>Population</th>
            </tr>
          </thead>
          <tbody id="city_search">
          </tbody>
        </table>
    </td>
  </tr>
</table>

<div id="error" class="hidden-block"></div>