package com.example.demo.lib.country.dto;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.api_dispatcher.demo.lib.country.service.FileUploadService;
import com.example.api_dispatcher.demo.lib.country.service.impl.FileUploadServiceImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { FileUploadServiceImpl.class })
public class FileUploadTest {

	@Autowired
	private FileUploadService _fupload;

	@Test
	public void testCityCountryList() throws IOException {
		String b64 = _fupload.uploadFile("CA", this.getClass().getResourceAsStream("/test_file.txt"));
		assertEquals("VGVzdCBMaW5lICMxClRlc3QgTGluZSAjMgo=", b64);
	}
}
