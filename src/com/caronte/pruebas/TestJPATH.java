package com.caronte.pruebas;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.caronte.jpath.JPATH;
import com.caronte.json.JSON;
import com.caronte.json.JSONObject;
import com.caronte.json.JSONValue;

public class TestJPATH 
{
	public static void main(String[] args) 
	{
		try
		{
//			byte[] bytes = Files.readAllBytes(Paths.get("C:\\JSON\\EjemploJSON03.txt"));
//
//			String string = new String(bytes, "UTF-8");
//			JSONObject jsonObject = JSON.parse(string);
//			
//			JSONValue jsonValue = JPATH.find(jsonObject, "/comprobante/emisor/regimenFiscal[0]/regimen");
//			
//			if (jsonValue != null)
//			{
//				System.out.println(jsonValue.getValue());
//			}
//			else
//			{
//				System.out.println("Valor no encontrado");
//			}
//			
//			JPATH.remove(jsonObject, "/comprobante/receptor");
//			JPATH.remove(jsonObject, "/comprobante/conceptos");
//			JPATH.remove(jsonObject, "/comprobante/complemento");
//			JPATH.remove(jsonObject, "/comprobante/impuestos");
//			JPATH.remove(jsonObject, "/comprobante/emisor/nombre");
//			JPATH.remove(jsonObject, "/comprobante/emisor/rfc");
//			JPATH.remove(jsonObject, "/comprobante/emisor/regimenFiscal");
//			JPATH.remove(jsonObject, "/comprobante/emisor/domicilioFiscal");
//			JPATH.remove(jsonObject, "/comprobante/emisor/expedidoEn");
//			
//			System.out.println(jsonObject);

//			JPATH.count("", path)
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
