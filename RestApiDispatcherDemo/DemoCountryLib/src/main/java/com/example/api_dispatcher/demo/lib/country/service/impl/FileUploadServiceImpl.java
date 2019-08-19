package com.example.api_dispatcher.demo.lib.country.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.example.api_dispatcher.demo.lib.country.service.FileUploadService;
import com.example.api_dispatcher.demo.lib.country.shared.RegionListConstants;

/**
 * Country service
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

	@Override
	public String uploadFile(String code, InputStream in) throws IOException {
		// Read file and return back base64 data for picture
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
	    int cnt;
	    while ((cnt = in.read(buffer)) != -1) {
	        out.write(buffer, 0, cnt);
	    }
		
	    String flag = new String(Base64.getEncoder().encode(out.toByteArray()));
	    RegionListConstants.COUNTRIES_SET.get(code).setFlag(flag);
	    
		return flag;
	}

}
