package com.innovation.mock.tool.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.innovation.mock.tool.entity.Constants;
import com.innovation.mock.tool.entity.ElcaData;

@Controller
@RequestMapping("/elca")
public class ElcaController {

	@RequestMapping(method = RequestMethod.GET, produces = "text/html")
	public String index(Model model) {
		model.addAttribute(Constants.ELCA_DATA, new ElcaData());
		return "elca";
	}

	@RequestMapping(value = "/sendElcaRequest", method = RequestMethod.POST)
	public String updateRequest(@ModelAttribute(Constants.ELCA_DATA) ElcaData elcaData, Model model)
			throws JsonProcessingException {
		elcaData.setBankNumber("6900");
		elcaData.setDocumentType("BV049");
		model.addAttribute(Constants.ELCA_DATA, elcaData);
		String result = callWebservice(elcaData);
		return "elca";
	}

	private String callWebservice(ElcaData data) {
		String responseString = "";
		String outputString = "";
		try {
			// Code to make a webservice HTTP request
			String wsURL = "http://localhost:8081/ivy/ws/designer/standard/155FFEC8030D4594?WSDL";
			URL url = new URL(wsURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			String xmlInput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "                    <soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:axon=\"http://axon.feedback.service.esprit.extension.qdb/\">\r\n"
					+ "                       <soapenv:Header/>\r\n" + "                       <soapenv:Body>\r\n"
					+ "                          <axon:eDossierFeedback>\r\n"
					+ "                             <edossierIdentifier>" + data.getElcaId() + "</edossierIdentifier>\r\n"
					+ "                             <externalID>" + data.getDocumentId() + "</externalID>\r\n"
					+ "                             <edossierStatus>OK</edossierStatus>\r\n"
					+ "                             <axonIdentifier>AX01012231</axonIdentifier>\r\n"
					+ "                             <FIN_userbk_nr>"+data.getBankNumber()+"</FIN_userbk_nr>\r\n"
					+ "                             <FIN_Kunden_nr>"+data.getDossierId()+"</FIN_Kunden_nr>\r\n"
					+ "                             <FIN_Belegart>"+data.getDocumentType()+"</FIN_Belegart>\r\n"
					+ "                             <FIN_Obj_art/>\r\n"
					+ "                             <status>pending</status>\r\n"
					+ "                          </axon:eDossierFeedback>\r\n"
					+ "                       </soapenv:Body>\r\n" + "                    </soapenv:Envelope>";

			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", wsURL);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			OutputStream out = httpConn.getOutputStream();
			// Write the content of the request to the outputstream of the HTTP Connection.
			out.write(b);
			out.close();
			// Ready with sending the request.

			// Read the response.
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			// Write the SOAP message response to a String.
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			
		} catch (Exception e) {
			System.out.println("=====" + e);
		}
		return outputString;
	}
}
