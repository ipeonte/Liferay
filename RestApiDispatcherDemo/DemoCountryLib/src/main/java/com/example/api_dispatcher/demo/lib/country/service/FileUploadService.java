package com.example.api_dispatcher.demo.lib.country.service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Country Service Interface
 * 
 * @author Igor Peonte <igor.144@gmail.com>
 *
 */
public interface FileUploadService {

	String uploadFile(String code, InputStream in) throws IOException;
}
