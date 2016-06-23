package com.masnegocio.test;

import com.caronte.db.MySQL;
import com.caronte.json.JSONObject;

public class Test 
{
	public static void main(String[] args) throws Exception	
	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		MySQL mySQL = new MySQL("localhost", "3306", "root", "1234567890", "expenses_cloud_v2", 2, 4);

		String query = "SELECT A.i_clave AS Clave, CONCAT(U.v_nombre,' ',U.v_primer_apellido,' ', U.v_segundo_apellido) AS Solicitante, A.v_motivo AS Motivo, DATE_FORMAT(A.d_fecha_creacion,'%Y/%m/%d') AS Fecha, DATE_FORMAT(A.d_fecha_creacion,'%H:%i') AS Hora, M.v_codigo AS Divisa, A.n_monto AS Monto, D.v_nombre AS Departamento, FLOOR(MAX(((AD.n_monto / DA.n_precio) * 100) / ((AD.i_cantidad * H.n_monto_maximo) / DH.n_precio))) AS PorcentajeExcendente FROM anticipo A, divisa M, departamento D, usuario U, detalle_anticipo AD, historico_politicas H, divisa DH, divisa DA WHERE A.v_cliente_mn = ? AND D.v_cliente_mn = ? AND U.v_cliente_mn = ? AND H.v_cliente_mn = ? AND AD.v_cliente_mn = ? AND A.i_clave_divisa = M.i_clave AND A.i_clave_departamento = D.i_clave AND A.i_clave_divisa = DA.i_clave AND H.i_clave_divisa = DH.i_clave AND AD.i_clave_anticipo = A.i_clave AND AD.i_clave_politica_h =  H.i_clave AND A.i_clave_usuario = U.i_clave AND A.i_estatus = 0 GROUP BY Clave";
		
		JSONObject parametros = new JSONObject();
		JSONObject parametro;
		JSONObject filtro;
		JSONObject value;
		
		parametros.addPair("currentPage", 10);
		parametros.addPair("pageSize", 10);
		parametros.addPair("maxPageScrollElements", 13);
		parametros.addPair("fieldOrder", 8);
		parametros.addPair("typeOrder", "DESC");
		
		parametros.resetArray();

		filtro = new JSONObject();
		filtro.addPair("field", "Departamento");
		filtro.addPair("operator", "IN");		
		filtro.resetArray();
		value = new JSONObject();
		value.addPair("type", "string");
		value.addPair("value", "Operaciones");
		filtro.addToArray(value);
		value = new JSONObject();
		value.addPair("type", "string");
		value.addPair("value", "Dirección Comercial");
		filtro.addToArray(value);
		filtro.saveArray("values");
		parametros.addToArray(filtro);
		
//		filtro = new JSONObject();
//		filtro.addPair("field", "Departamento");
//		filtro.addPair("operator", "=");		
//		filtro.resetArray();
//		value = new JSONObject();
//		value.addPair("type", "string");
//		value.addPair("value", "Operaciones");
//		filtro.addToArray(value);
//		filtro.saveArray("values");
//		parametros.addToArray(filtro);

//		filtro = new JSONObject();
//		filtro.addPair("field", "Monto");
//		filtro.addPair("operator", "BETWEEN");		
//		filtro.resetArray();
//		value = new JSONObject();
//		value.addPair("type", "int");
//		value.addPair("value", "20000");
//		filtro.addToArray(value);
//		value = new JSONObject();
//		value.addPair("type", "int");
//		value.addPair("value", "25000");
//		filtro.addToArray(value);
//		filtro.saveArray("values");
//		parametros.addToArray(filtro);

		parametros.saveArray("filters");
		
		parametros.resetArray();
		for (int i = 0; i < 5; i++)
		{
			parametro = new JSONObject();
			parametro.addPair("type", "string");
			parametro.addPair("value", "e-xpenses");
			parametros.addToArray(parametro);
		}
		parametros.saveArray("parameters");
		
		
		// System.out.println(parametros);
		System.out.println(mySQL.executePagedQuery(query, parametros));
		
		
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		MySQL mySQL = new MySQL("172.28.82.196", "3306", "root", "MN4dm1n", "pac_mn", 2, 10);
//		System.out.println(mySQL.executeQuery("SELECT FACTURA_TIMBRADA FROM factura_timbrada WHERE ID_FACTURA_TIMBRADA = 10838443;", null));
		
//		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
//		
//		Oracle oracleXE = new Oracle("172.28.15.121", "1521", "PACMAN", "nb*cG65A", "XE", 2, 10);
//		Oracle oracleXE2 = new Oracle("172.20.36.208", "1521", "PACMAN", "nb*cG65A", "XE2PAC", 2, 10);
//		Oracle oracleXE208 = new Oracle("172.28.15.121", "1521", "PACMAN", "nb*cG65A", "XE208", 2, 10);
//		
//		System.out.println(oracleXE.executeQuery("SELECT ID_FACTURA_TIMBRADA, FACTURA_TIMBRADA FROM factura_timbrada WHERE ID_FACTURA_TIMBRADA = 10838443;", null));
//		System.out.println(oracleXE2.executeQuery("SELECT ID, FACTURA_TIMBRADA FROM FACTURAS_EXITO WHERE ID = 9562714", null));
//		System.out.println(oracleXE208.executeQuery("SELECT FACTURA_TIMBRADA FROM factura_timbrada WHERE ID_FACTURA_TIMBRADA = 10838443;", null));
		
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		MySQL mySQL = new MySQL("localhost", "3306", "root", "1234567890", "masnegocio_maildelivery", 10, 100);
//
//		System.out.println(mySQL.executeQuery("SELECT i_clave AS clave, v_plantilla AS plantilla, UNCOMPRESS(v_datos) AS datos FROM mail WHERE i_id_proceso_envio = 1 AND i_estatus = 2 ORDER BY i_clave;", null));
//		
//		
//		System.out.println("Listo");
		
		
//
//		JSONObject parametros = new JSONObject();
//		JSONObject parametro;
//
//		parametros.resetArray();
//
//		parametro = new JSONObject();
//		parametro.addPair("type", "int");
//		parametro.addPair("value", "4");
//		parametros.addToArray(parametro);
//
//		parametro = new JSONObject();
//		parametro.addPair("type", "int");
//		parametro.addPair("value", "5");
//		parametros.addToArray(parametro);
//		
//		parametros.saveArray("parameters");
//		
//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		
//		System.out.println(mySQL.executeQuery("SELECT * FROM esquema_cfdi32 WHERE i_clave BETWEEN ? AND ?;", parametros));
//		
//		JSONObject jsonObject;
//		
//		jsonObject  = JSON.parse("{ \"p_uuid\" : \"00013X31-243E-4D94-A11F-9786F08DA56E\", \"p_rfc_emisor\" : \"HIM890120VEA\", \"p_razon_social_emisor\" : \"HERBALIFE INTERNACIONAL DE MEXICO SA DE CV\", \"p_rfc_receptor\" : \"LAPR801007A47\", \"p_razon_social_receptor\" : \"MARIA DEL ROSARIO LAZARO PROCOPIO\", \"p_cp_domicilio_fiscal\" : \"45609\", \"p_cp_expedicion\" : \"45609\", \"p_no_certificado_emisor\" : \"00001000000400206517\", \"p_no_certificado_sat\" : \"00001000000203253077\", \"p_serie\" : \"\", \"p_folio\" : \"\", \"p_forma_pago\" : \"TRANSFERENCIA ELECTRONICA DE FONDOS\", \"p_condiciones_pago\" : \"Pago en una sola exhibicion\", \"p_motivo_descuento\" : \"NA\", \"p_tipo_cambio\" : \"\", \"p_moneda\" : \"MX\", \"p_tipo_comprobante\" : \"egreso\", \"p_metodo_pago\" : \"Deposito en cuenta\", \"p_lugar_expedicion\" : \"San Pedro Tlaquepaque, Jalisco\", \"p_numero_cuenta_pago\" : \"\", \"p_folio_fiscal_original\" : \"\", \"p_serie_folio_fiscal_original\" : \"\", \"p_total\" : \"3258.79\", \"p_subTotal\" : \"3083.66\", \"p_descuento\" : \"0.00\", \"p_fecha_emision\" : \"2016-05-09 03:27:57\", \"p_fecha_timbrado\" : \"2016-05-09 05:03:48\", \"p_fecha_folio_fiscal_original\" : null, \"p_monto_folio_fiscal_original\" : null, \"p_addenda\" : \"N\", \"p_contenido\" : \"PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiIHN0YW5kYWxvbmU9Im5vIj8+DQo8Y2ZkaTpDb21wcm9iYW50ZSB4bWxuczpjZmRpPSJodHRwOi8vd3d3LnNhdC5nb2IubXgvY2ZkLzMiIHhtbG5zOnhzaT0iaHR0cDovL3d3dy53My5vcmcvMjAwMS9YTUxTY2hlbWEtaW5zdGFuY2UiIEx1Z2FyRXhwZWRpY2lvbj0iU2FuIFBlZHJvIFRsYXF1ZXBhcXVlLCBKYWxpc2NvIiBNb25lZGE9Ik1YIiBjZXJ0aWZpY2Fkbz0iTUlJR1hEQ0NCRVNnQXdJQkFnSVVNREF3TURFd01EQXdNREEwTURBeU1EWTFNVGN3RFFZSktvWklodmNOQVFFTEJRQXdnZ0d5TVRnd05nWURWUVFEREM5QkxrTXVJR1JsYkNCVFpYSjJhV05wYnlCa1pTQkJaRzFwYm1semRISmhZMm5EczI0Z1ZISnBZblYwWVhKcFlURXZNQzBHQTFVRUNnd21VMlZ5ZG1samFXOGdaR1VnUVdSdGFXNXBjM1J5WVdOcHc3TnVJRlJ5YVdKMWRHRnlhV0V4T0RBMkJnTlZCQXNNTDBGa2JXbHVhWE4wY21GamFjT3piaUJrWlNCVFpXZDFjbWxrWVdRZ1pHVWdiR0VnU1c1bWIzSnRZV05wdzdOdU1SOHdIUVlKS29aSWh2Y05BUWtCRmhCaFkyOWtjMEJ6WVhRdVoyOWlMbTE0TVNZd0pBWURWUVFKREIxQmRpNGdTR2xrWVd4bmJ5QTNOeXdnUTI5c0xpQkhkV1Z5Y21WeWJ6RU9NQXdHQTFVRUVRd0ZNRFl6TURBeEN6QUpCZ05WQkFZVEFrMVlNUmt3RndZRFZRUUlEQkJFYVhOMGNtbDBieUJHWldSbGNtRnNNUlF3RWdZRFZRUUhEQXREZFdGMWFIVERxVzF2WXpFVk1CTUdBMVVFTFJNTVUwRlVPVGN3TnpBeFRrNHpNVjB3V3dZSktvWklodmNOQVFrQ0RFNVNaWE53YjI1ellXSnNaVG9nUVdSdGFXNXBjM1J5WVdOcHc3TnVJRU5sYm5SeVlXd2daR1VnVTJWeWRtbGphVzl6SUZSeWFXSjFkR0Z5YVc5eklHRnNJRU52Ym5SeWFXSjFlV1Z1ZEdVd0hoY05NVFV3T0RJd01UWXpORFV3V2hjTk1Ua3dPREl3TVRZek5EVXdXakNCL0RFek1ERUdBMVVFQXhNcVNFVlNRa0ZNU1VaRklFbE9WRVZTVGtGRFNVOU9RVXdnUkVVZ1RVVllTVU5QSUZOQklFUkZJRU5XTVRNd01RWURWUVFwRXlwSVJWSkNRVXhKUmtVZ1NVNVVSVkpPUVVOSlQwNUJUQ0JFUlNCTlJWaEpRMDhnVTBFZ1JFVWdRMVl4TXpBeEJnTlZCQW9US2toRlVrSkJURWxHUlNCSlRsUkZVazVCUTBsUFRrRk1JRVJGSUUxRldFbERUeUJUUVNCRVJTQkRWakVsTUNNR0ExVUVMUk1jU0VsTk9Ea3dNVEl3VmtWQklDOGdVazlNUWpZMk1ERXlNa3RDT0RFZU1Cd0dBMVVFQlJNVklDOGdVazlNUWpZMk1ERXlNa2hLUTBKUVRqQXdNUlF3RWdZRFZRUUxFd3RNWVhNZ1JuVmxiblJsY3pDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBS2l3VGdiZVhnbTJtdVM1eStwNHd3OVZPZ3BNaHZTNHFqWDdaQ2ZQdUdrMXhtbFQraUxPeFpZbTgxQ1kvZy8wS2p0S0FFSkRVazBZeWQzVjZlcXBuVHdDaEN6RnJsOHhjOHFkUzRrb200dWxiZEtabldHZCs2d3RyRjlrblFwTFZ6RFZIY3IwZFFMQ2NJK3NXcThrME1HYzFQYU45TTg0WkQxTWRtditSUFJrVThSK2laaDdOWGk5d1BVTUdudi9Md1orOVk3ejBSVTlsczh4Q2I0OVQ5T0krUlZuMTR0YnJNQ1RXUEthdTRuVittUjdlZ0Mxem90YllNTEJrUHB3S0NBd2FHanN4dEdKZ3MrYm5VWC9lQi9rQ1RDWVpBMXpvcGdTaXc2RFlkMzBRSjhVK1c1NkhVQ1l3S1h0V1Y5V1d2NzE1d3hnS1NZaWxJSDhVVDYxaEdFQ0F3RUFBYU1kTUJzd0RBWURWUjBUQVFIL0JBSXdBREFMQmdOVkhROEVCQU1DQnNBd0RRWUpLb1pJaHZjTkFRRUxCUUFEZ2dJQkFBSDNiZUdhNWxvS1VmSmVvYUNlMjFZaGNTUjVDeldIWUgrSzhaNSswYVduOWFSZ1NtTUJ0K1A2Wm9TdE0rNE5MR0tQbmNTV2RuNGRSWlRwVmlLR2h1NmRSWExpRDA2aksycEJjdHFJUXVpQkN0cE45SGRaWFNNRm9OU3IzR3ZyVmVqQmd2YS82aUxpei9XU2x4bU9vTGlxSE53NCt4Q1JrYnNlS1Y5cGRKY0tpSGVtdTVudWtoc1VVYzR1Y2d4WUxOTVVmRkUvMHdYV3lOdE54MU5BM1VEbkppQ2cxRHV1Y2F3T3hHMmREem0xc2RKTmJmLzJlQjZ3WmFyUjVCWjB3K0ZNdVFHaVpZZ28zTmJlNnNNVU1Hd3cxV2wxa2ttbzBKVkNqUFZTc04raVhuLzZ1R3RsNExGTG9mb1VyUG9pM3BseDdiNWN3NnFydnlBR1VFUzRVKzMwdGo1TmhPVlpUaTltUUlhU3VsRmZaSFAxYURoVGNpMnlQOGV2aXNONGYyK1hRa2RPOTc0MXlVR2xHRUFvVzNyaFA1b0dBemJqa3BMSFNycjhZUzFaSVBCa2JtbStqRThBVmEyQURybVp6MFdDMDl0RFJOQlZRb0RaR29nbGFSVEpiOTB3ajRmL0VpWDA4N3ZLUTJ3M1o2UE5wckltQzEyYTl5QmJoQkxGeVl2clVWMElWclBTLzlUYkJpSCtBaHNITmJMR2NIMEx1S0dpY0FVR0pFODlwMzcyMW9SanlBNm1TQWprZml3bzFlRFI3R05BbHFkZTNFOHJNaCtudUZzcUZpVUNuMnM1NzhyblI2RExKOVpONlFna1RUakRUWWwwMkRXYkdjMVZGUXg4N2EyNzVnTmRrdm9MYUV2amNCc2lrK3A0aFJ3dmtxejdNOXEvcUNqTiIgY29uZGljaW9uZXNEZVBhZ289IlBhZ28gZW4gdW5hIHNvbGEgZXhoaWJpY2lvbiIgZGVzY3VlbnRvPSIwLjAwIiBmZWNoYT0iMjAxNi0wNS0wOVQwMzoyNzo1NyIgZm9ybWFEZVBhZ289IlRSQU5TRkVSRU5DSUEgRUxFQ1RST05JQ0EgREUgRk9ORE9TIiBtZXRvZG9EZVBhZ289IkRlcG9zaXRvIGVuIGN1ZW50YSIgbW90aXZvRGVzY3VlbnRvPSJOQSIgbm9DZXJ0aWZpY2Fkbz0iMDAwMDEwMDAwMDA0MDAyMDY1MTciIHNlbGxvPSJsUFo4U0laOFFiMlZNOWtmZURUeWE3QVJ4VkJQSDRJbklyVEJ4MUgzeGZRTS9OaGNac0xWWmZ5enNNN282Y3Z4cjdWdUxYU0t6blZtenJ6T3VMdW00LzRDcDZyRkErUFhHTjI2MzlVcnBHa0FnNUFmVkVib3A3WlF6a2cwOVM2TWh3a0lTUU8reDBBeEZMc1oxV1VuQ0c0MHA2K2RpUnpHZnlXR1FrV2puZkJHWDMyOFdiNm1EZjFzWUJ4eXRrVmJRcFZOd2xaRDlDbXBXR2VIZTdwWkhKMDFPQVBSREZEcy93ME1tZ0lBZWNsMFpiY1cwdTNBUjBJY0N1WnNmSktxWHluVEM1UEFML2JyejZjMUdyS2JwM2hjMFdJY3Axa0paVHhxZTQ1cldTVDBtMW4vMDZ5d00xMURyRC9tV1BCVDF0MkZhelZ4SGp4TmxpWWRoWGNFeHc9PSIgc3ViVG90YWw9IjMwODMuNjYiIHRpcG9EZUNvbXByb2JhbnRlPSJlZ3Jlc28iIHRvdGFsPSIzMjU4Ljc5IiB2ZXJzaW9uPSIzLjIiIHhzaTpzY2hlbWFMb2NhdGlvbj0iaHR0cDovL3d3dy5zYXQuZ29iLm14L2NmZC8zIGh0dHA6Ly93d3cuc2F0LmdvYi5teC9zaXRpb19pbnRlcm5ldC9jZmQvMy9jZmR2MzIueHNkIGh0dHA6Ly93d3cuc2F0LmdvYi5teC9sZXllbmRhc0Zpc2NhbGVzIGh0dHA6Ly93d3cuc2F0LmdvYi5teC9zaXRpb19pbnRlcm5ldC9jZmQvbGV5ZW5kYXNGaXNjYWxlcy9sZXllbmRhc0Zpc2MueHNkIGh0dHA6Ly93d3cuc2F0LmdvYi5teC9ub21pbmEgaHR0cDovL3d3dy5zYXQuZ29iLm14L3NpdGlvX2ludGVybmV0L2NmZC9ub21pbmEvbm9taW5hMTEueHNkIj4NCiAgPGNmZGk6RW1pc29yIG5vbWJyZT0iSEVSQkFMSUZFIElOVEVSTkFDSU9OQUwgREUgTUVYSUNPIFNBIERFIENWIiByZmM9IkhJTTg5MDEyMFZFQSI+DQogICAgPGNmZGk6RG9taWNpbGlvRmlzY2FsIGNhbGxlPSJBdi4gQ2FtaW5vIGFsIElURVNPIiBjb2RpZ29Qb3N0YWw9IjQ1NjA5IiBjb2xvbmlhPSJDb2wuIEVsIE1hbnRlIiBlc3RhZG89IkphbGlzY28iIG11bmljaXBpbz0iU2FuIFBlZHJvIFRsYXF1ZXBhcXVlIiBub0V4dGVyaW9yPSI4OTAwIiBub0ludGVyaW9yPSIxQSIgcGFpcz0iTWV4aWNvIiAvPg0KICAgIDxjZmRpOkV4cGVkaWRvRW4gY2FsbGU9IkF2LiBDYW1pbm8gYWwgSVRFU08iIGNvZGlnb1Bvc3RhbD0iNDU2MDkiIGNvbG9uaWE9IkNvbC4gRWwgTWFudGUiIGVzdGFkbz0iSmFsaXNjbyIgbXVuaWNpcGlvPSJTYW4gUGVkcm8gVGxhcXVlcGFxdWUiIG5vRXh0ZXJpb3I9Ijg5MDAiIG5vSW50ZXJpb3I9IjFBIiBwYWlzPSJNZXhpY28iIC8+DQogICAgPGNmZGk6UmVnaW1lbkZpc2NhbCBSZWdpbWVuPSJHRU5FUkFMIERFIExFWSBQQVJBIFBFUlNPTkFTIE1PUkFMRVMiIC8+DQogIDwvY2ZkaTpFbWlzb3I+DQogIDxjZmRpOlJlY2VwdG9yIG5vbWJyZT0iTUFSSUEgREVMIFJPU0FSSU8gTEFaQVJPIFBST0NPUElPIiByZmM9IkxBUFI4MDEwMDdBNDciPg0KICAgIDxjZmRpOkRvbWljaWxpbyBwYWlzPSJNZXhpY28iIC8+DQogIDwvY2ZkaTpSZWNlcHRvcj4NCiAgPGNmZGk6Q29uY2VwdG9zPg0KICAgIDxjZmRpOkNvbmNlcHRvIGNhbnRpZGFkPSIxIiBkZXNjcmlwY2lvbj0iUGFnbyBkZSBjb21pc2lvbmVzLCBhc2ltaWxhZG8gYSBzYWxhcmlvcyIgaW1wb3J0ZT0iMjkwOC41MyIgbm9JZGVudGlmaWNhY2lvbj0iMSIgdW5pZGFkPSJTZXJ2aWNpbyIgdmFsb3JVbml0YXJpbz0iMjkwOC41MyIgLz4NCiAgPC9jZmRpOkNvbmNlcHRvcz4NCiAgPGNmZGk6SW1wdWVzdG9zIHRvdGFsSW1wdWVzdG9zUmV0ZW5pZG9zPSIwIj4NCiAgICA8Y2ZkaTpSZXRlbmNpb25lcz4NCiAgICAgIDxjZmRpOlJldGVuY2lvbiBpbXBvcnRlPSIwIiBpbXB1ZXN0bz0iSVNSIiAvPg0KICAgIDwvY2ZkaTpSZXRlbmNpb25lcz4NCiAgPC9jZmRpOkltcHVlc3Rvcz4NCiAgPGNmZGk6Q29tcGxlbWVudG8+DQogICAgPG5vbWluYTpOb21pbmEgeG1sbnM6bm9taW5hPSJodHRwOi8vd3d3LnNhdC5nb2IubXgvbm9taW5hIiBDTEFCRT0iMDAyMjcwNzAwMzg3MTI5Njc3IiBDVVJQPSJMQVBSODAxMDA3TUdSWlJTMDQiIERlcGFydGFtZW50bz0iQVNJTUlMQURPUyIgRmVjaGFGaW5hbFBhZ289IjIwMTYtMDUtMTUiIEZlY2hhSW5pY2lhbFBhZ289IjIwMTYtMDUtMTUiIEZlY2hhSW5pY2lvUmVsTGFib3JhbD0iMjAwNS0wMi0xOCIgRmVjaGFQYWdvPSIyMDE2LTA1LTE1IiBOdW1EaWFzUGFnYWRvcz0iMSIgTnVtRW1wbGVhZG89IjExNTk3Mjg0IiBOdW1TZWd1cmlkYWRTb2NpYWw9Ii0iIFBlcmlvZGljaWRhZFBhZ289IkNvbWlzaW9uIiBQdWVzdG89IkNPTUlTSU9OSVNUQSIgUmVnaXN0cm9QYXRyb25hbD0iLSIgU2FsYXJpb0Jhc2VDb3RBcG9yPSIwLjAwIiBTYWxhcmlvRGlhcmlvSW50ZWdyYWRvPSIwLjAwIiBUaXBvUmVnaW1lbj0iOCIgVmVyc2lvbj0iMS4xIiB4c2k6c2NoZW1hTG9jYXRpb249Imh0dHA6Ly93d3cuc2F0LmdvYi5teC9jZmQvMyBodHRwOi8vd3d3LnNhdC5nb2IubXgvY2ZkLzMvY2ZkdjMyLnhzZCBodHRwOi8vd3d3LnNhdC5nb2IubXgvbm9taW5hIGh0dHA6Ly93d3cuc2F0LmdvYi5teC9zaXRpb19pbnRlcm5ldC9jZmQvbm9taW5hL25vbWluYTExLnhzZCAiPg0KICAgICAgPG5vbWluYTpQZXJjZXBjaW9uZXMgVG90YWxFeGVudG89IjAuMDAiIFRvdGFsR3JhdmFkbz0iMzA4My42NiI+DQogICAgICAgIDxub21pbmE6UGVyY2VwY2lvbiBDbGF2ZT0iMTAxIiBDb25jZXB0bz0iQ29taXNpb24iIEltcG9ydGVFeGVudG89IjAuMDAiIEltcG9ydGVHcmF2YWRvPSIzMDgzLjY2IiBUaXBvUGVyY2VwY2lvbj0iMDI4IiAvPg0KICAgICAgPC9ub21pbmE6UGVyY2VwY2lvbmVzPg0KICAgICAgPG5vbWluYTpEZWR1Y2Npb25lcyBUb3RhbEV4ZW50bz0iMC4wMCIgVG90YWxHcmF2YWRvPSIxNzUuMTMiPg0KICAgICAgICA8bm9taW5hOkRlZHVjY2lvbiBDbGF2ZT0iMTAyIiBDb25jZXB0bz0iSVNSIFJFVEVOSURPIEFTSU1JTEFET1MiIEltcG9ydGVFeGVudG89IjAuMDAiIEltcG9ydGVHcmF2YWRvPSIxNzUuMTMiIFRpcG9EZWR1Y2Npb249IjAwMiIgLz4NCiAgICAgIDwvbm9taW5hOkRlZHVjY2lvbmVzPg0KICAgIDwvbm9taW5hOk5vbWluYT4NCiAgICA8bGV5ZW5kYXNGaXNjOkxleWVuZGFzRmlzY2FsZXMgeG1sbnM6bGV5ZW5kYXNGaXNjPSJodHRwOi8vd3d3LnNhdC5nb2IubXgvbGV5ZW5kYXNGaXNjYWxlcyIgdmVyc2lvbj0iMS4wIiB4c2k6c2NoZW1hTG9jYXRpb249Imh0dHA6Ly93d3cuc2F0LmdvYi5teC9sZXllbmRhc0Zpc2NhbGVzIGh0dHA6Ly93d3cuc2F0LmdvYi5teC9zaXRpb19pbnRlcm5ldC9jZmQvbGV5ZW5kYXNGaXNjYWxlcy9sZXllbmRhc0Zpc2MueHNkIj4NCiAgICAgIDxsZXllbmRhc0Zpc2M6TGV5ZW5kYSBkaXNwb3NpY2lvbkZpc2NhbD0iTElTUiIgbm9ybWE9IkFSVElDVUxPIDk2IiB0ZXh0b0xleWVuZGE9IklTUiBSRVRFTklETyBERSBDT05GT1JNSURBRCBDT04gRUwgQVJUSUNVTE8gOTYgTElTUiIgLz4NCiAgICA8L2xleWVuZGFzRmlzYzpMZXllbmRhc0Zpc2NhbGVzPg0KICAgIDx0ZmQ6VGltYnJlRmlzY2FsRGlnaXRhbCB2ZXJzaW9uPSIxLjAiIFVVSUQ9IjAwMDEzQjMxLTI0M0UtNEQ5NC1BMTFGLTk3ODZGMDhEQTU2RSIgRmVjaGFUaW1icmFkbz0iMjAxNi0wNS0wOVQwNTowMzo0OCIgc2VsbG9DRkQ9ImxQWjhTSVo4UWIyVk05a2ZlRFR5YTdBUnhWQlBINEluSXJUQngxSDN4ZlFNL05oY1pzTFZaZnl6c003bzZjdnhyN1Z1TFhTS3puVm16cnpPdUx1bTQvNENwNnJGQStQWEdOMjYzOVVycEdrQWc1QWZWRWJvcDdaUXprZzA5UzZNaHdrSVNRTyt4MEF4RkxzWjFXVW5DRzQwcDYrZGlSekdmeVdHUWtXam5mQkdYMzI4V2I2bURmMXNZQnh5dGtWYlFwVk53bFpEOUNtcFdHZUhlN3BaSEowMU9BUFJERkRzL3cwTW1nSUFlY2wwWmJjVzB1M0FSMEljQ3Vac2ZKS3FYeW5UQzVQQUwvYnJ6NmMxR3JLYnAzaGMwV0ljcDFrSlpUeHFlNDVyV1NUMG0xbi8wNnl3TTExRHJEL21XUEJUMXQyRmF6VnhIanhObGlZZGhYY0V4dz09IiBub0NlcnRpZmljYWRvU0FUPSIwMDAwMTAwMDAwMDIwMzI1MzA3NyIgc2VsbG9TQVQ9Imo0RFArUHNnczExVkRoZVVZdlJ5b0NLNEd2ZmZXTzZwVEZNLzhaK1hWK3FndDFJVndIdUc0WmVTV2ZPL1lCTmFNbGFyaEg4NkEvaU5tOHQ1VzlPbW5VemVqZUJLZmhFbkU0MTZhY2pQV1RUeFBBamEzYWtLMCtzZnBkbmUwNERkTFZUdGNmOHdEZnhiUTBndWdQQTR0VlQyU21zVHB1M0xpMllpaU9XVTJLdz0iIHhzaTpzY2hlbWFMb2NhdGlvbj0iaHR0cDovL3d3dy5zYXQuZ29iLm14L1RpbWJyZUZpc2NhbERpZ2l0YWwgaHR0cDovL3d3dy5zYXQuZ29iLm14L3NpdGlvX2ludGVybmV0L2NmZC9UaW1icmVGaXNjYWxEaWdpdGFsL1RpbWJyZUZpc2NhbERpZ2l0YWwueHNkIiB4bWxuczp0ZmQ9Imh0dHA6Ly93d3cuc2F0LmdvYi5teC9UaW1icmVGaXNjYWxEaWdpdGFsIiAvPg0KICA8L2NmZGk6Q29tcGxlbWVudG8+DQo8L2NmZGk6Q29tcHJvYmFudGU+\", \"p_complementos\" : \"NNNNNNNNNNSSNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN\", \"p_sello_mn\" : \"N\", \"p_fecha_publicacion\" : null, \"p_fecha_cancelacion\" : null }");
//		jsonObject = mySQL.executeSP("SP_Almacenar_CFDI32", jsonObject);
//
//		System.out.println(jsonObject);
	}
}
